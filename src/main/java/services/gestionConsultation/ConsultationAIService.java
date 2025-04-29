package services.gestionConsultation;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.Consultation;
import entities.User;
import okhttp3.*;
import utils.Const;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConsultationAIService {

    private static final String API_KEY = Const.API_KEY;
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    /**
     * Analyzes consultation reason and returns scheduling recommendations
     *
     * @param reason       The consultation reason text
     * @param professional The medical professional being consulted
     * @return Map containing urgency level and recommended scheduling
     */
    public ConsultationRecommendation analyzeConsultationReason(String reason, User professional) {
        try {
            // Create request to Gemini
            String prompt = buildPrompt(reason, professional);
            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", prompt);

            JsonArray partsArray = new JsonArray();
            partsArray.add(textPart);

            JsonObject content = new JsonObject();
            content.addProperty("role", "user");
            content.add("parts", partsArray);

            JsonArray contentsArray = new JsonArray();
            contentsArray.add(content);

            JsonObject requestBody = new JsonObject();
            requestBody.add("contents", contentsArray);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    requestBody.toString()
            );
            System.out.println("request body" + body);
            Request request = new Request.Builder()
                    .url(API_URL + "?key=" + API_KEY)
                    .post(body)
                    .build();

            // Execute request
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            System.out.println("response body: " + responseBody);

// Extracting the 'text' from the response
            String jsonString = new JsonParser().parse(responseBody)
                    .getAsJsonObject()
                    .getAsJsonArray("candidates")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0)
                    .getAsJsonObject()
                    .get("text")
                    .getAsString();

// Removing the code block markers (```json and ```)
            jsonString = jsonString.replaceAll("```json", "").replaceAll("```", "").trim();

// Now parse the extracted JSON string into a JSON object
            JsonObject parsedJson = JsonParser.parseString(jsonString).getAsJsonObject();

// Now you can work with the parsedJson
            System.out.println("Parsed JSON: " + parsedJson);
            // Parse AI response
            return parseAIResponse(jsonString);

        } catch (IOException e) {
            System.err.println("Error calling AI service: " + e.getMessage());
            return new ConsultationRecommendation(UrgencyLevel.NORMAL,
                    "Unable to analyze reason. Please select any available date.",
                    new ArrayList<>());
        }
    }

    private String buildPrompt(String reason, User professional) {
        ConsultationService consultationService = new ConsultationService();
        List<LocalDate> indispoDates = new ArrayList<>(); // initialize it to avoid null issues
        try {
            List<Consultation> consultations = consultationService.findByProfessional(professional.getId());
            indispoDates = consultations.stream()
                    .map(Consultation::getDateConsultation)
                    .toList();
        } catch (SQLException e) {
            e.printStackTrace();
            // Optional: you could also log or throw a custom exception
        }

        // Prepare the unavailable dates as a String (for example as a list)
        String indispoDatesStr = indispoDates.stream()
                .map(LocalDate::toString)
                .collect(Collectors.joining(", "));

        return "En tant qu'assistant de planification médicale, analysez uniquement le motif de consultation suivant : " + reason + ". "
                + "Le médecin est un médecin généraliste. "
                + "Pour votre analyse, sachez que les dates suivantes sont indisponibles : [" + indispoDatesStr + "]. "
                + "Cependant, NE faites PAS mention de ces dates dans votre explication. "
                + "Votre tâche est de déterminer uniquement le niveau d'urgence (URGENT, HIGH, NORMAL, ROUTINE) et de fournir des recommandations de planification. "
                + "N'utilisez que ces valeurs en anglais (URGENT, HIGH, NORMAL, ROUTINE) pour le niveau d'urgence. "
                + "Assurez-vous d'inclure un jour de la semaine recommandé (0 = Lundi à 6 = Dimanche) en tenant compte de la disponibilité du médecin. "
                + "Fournissez la réponse au format JSON avec les champs suivants : "
                + "urgencyLevel, explanation (centrée uniquement sur le motif médical), recommendedDayOfWeek, recommendedTimeOfDay (MORNING, AFTERNOON, EVENING). "
                + "Veillez à inclure tous les champs obligatoirement.";
    }


    private ConsultationRecommendation parseAIResponse(String aiResponse) {
        try {
            System.out.println("ai respons e\n" + aiResponse);
            // Parse the AI response as a JsonObject directly
            JsonObject recommendation = gson.fromJson(aiResponse, JsonObject.class);

            // Extract the urgency level string and normalize it
            String urgencyStr = recommendation.get("urgencyLevel").getAsString();

            // Map French urgency levels to our enum values
            UrgencyLevel urgency;
            if (urgencyStr.contains("LEV") || urgencyStr.equalsIgnoreCase("ÉLEVÉ") ||
                    urgencyStr.equalsIgnoreCase("ELEVE")) {
                urgency = UrgencyLevel.HIGH;
            } else if (urgencyStr.contains("URG") || urgencyStr.equalsIgnoreCase("URGENT")) {
                urgency = UrgencyLevel.URGENT;
            } else if (urgencyStr.contains("ROUT") || urgencyStr.equalsIgnoreCase("ROUTINIER")) {
                urgency = UrgencyLevel.ROUTINE;
            } else {
                // Default to NORMAL for any other case
                urgency = UrgencyLevel.NORMAL;
            }

            String explanation = recommendation.get("explanation").getAsString();

            // Process recommended days
            List<Integer> recommendedDays = new ArrayList<>();
            if (recommendation.has("recommendedDayOfWeek")) {
                int day = recommendation.get("recommendedDayOfWeek").getAsInt();
                recommendedDays.add(day);
            }

            return new ConsultationRecommendation(urgency, explanation, recommendedDays);
        } catch (Exception e) {
            System.err.println("Error parsing AI response: " + e.getMessage());
            e.printStackTrace(); // This will give you detailed error information
            return new ConsultationRecommendation(UrgencyLevel.NORMAL,
                    "Unable to analyze reason. Please select any available date.",
                    new ArrayList<>());
        }
    }
    // Helper classes
    public enum UrgencyLevel {
        URGENT,  // Same day or next day
        HIGH,    // Within 2-3 days
        NORMAL,  // Within a week
        ROUTINE  // Any time is fine
    }

    public static class ConsultationRecommendation {
        private final UrgencyLevel urgencyLevel;
        private final String explanation;
        private final List<Integer> recommendedDays; // Days of week (0=Monday, 6=Sunday)

        public ConsultationRecommendation(UrgencyLevel urgencyLevel, String explanation,
                                          List<Integer> recommendedDays) {
            this.urgencyLevel = urgencyLevel;
            this.explanation = explanation;
            this.recommendedDays = recommendedDays;
        }

        public UrgencyLevel getUrgencyLevel() {
            return urgencyLevel;
        }

        public String getExplanation() {
            return explanation;
        }

        public List<Integer> getRecommendedDays() {
            return recommendedDays;
        }

        // Return dates within the next 30 days that match our day recommendations
        public List<LocalDate> getRecommendedDates(LocalDate startDate) {
            List<LocalDate> dates = new ArrayList<>();
            LocalDate current = startDate;

            // Look ahead up to 30 days
            for (int i = 0; i < 30; i++) {
                int dayOfWeek = current.getDayOfWeek().getValue() - 1; // 0=Monday in our system
                if (recommendedDays.isEmpty() || recommendedDays.contains(dayOfWeek)) {
                    dates.add(current);
                }
                current = current.plusDays(1);
            }

            return dates;
        }
    }
}