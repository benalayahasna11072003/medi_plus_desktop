package controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.MonthView;
import entities.Consultation;
import entities.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import services.gestionConsultation.ConsultationAIService;
import services.gestionConsultation.ConsultationService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarComponent extends BorderPane {

    private final MonthView monthView;
    private final Calendar professionalCalendar;
    private final Calendar recommendationCalendar;
    private final CalendarSource calendarSource;
    private User professional;
    private final ConsultationService consultationService;
    private LocalDate selectedDate;
    private final List<LocalDate> unavailableDates;
    private final ConsultationAIService aiService;

    private ConsultationAIService.ConsultationRecommendation currentRecommendation;
    private List<LocalDate> recommendedDates;
    private String consultationReason;

    // Labels for UI feedback
    private Label statusLabel;
    private Label recommendationLabel;

    public CalendarComponent() {
        this.getStylesheets().add(getClass().getResource("/gestionConcultation/calendar-styles.css").toExternalForm());
        this.consultationService = new ConsultationService();
        this.aiService = new ConsultationAIService();
        this.selectedDate = LocalDate.now();
        this.unavailableDates = new ArrayList<>();
        this.recommendedDates = new ArrayList<>();

        // Create a month view
        monthView = new MonthView();
        monthView.setShowToday(true);
        monthView.setShowWeekNumbers(true);
        monthView.setPrefHeight(500);
        monthView.setPrefWidth(800);

        // Create header
        Label headerLabel = new Label("Select Consultation Date");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        headerLabel.setAlignment(Pos.CENTER);

        // Add recommendation label
        recommendationLabel = new Label("Please enter consultation reason for date recommendations");
        recommendationLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        recommendationLabel.setAlignment(Pos.CENTER);

        // Add status label for availability info
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        statusLabel.setAlignment(Pos.CENTER);

        VBox topContainer = new VBox(10);
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setPadding(new Insets(10));
        topContainer.getChildren().addAll(headerLabel, recommendationLabel, statusLabel);

        // Create calendar for professional bookings
        professionalCalendar = new Calendar("Booked Consultations");
        professionalCalendar.setStyle(Calendar.Style.STYLE1);

        // Create calendar for AI recommendations
        recommendationCalendar = new Calendar("Recommended Dates");
        recommendationCalendar.setStyle(Calendar.Style.STYLE2);

        // Set up calendar source
        calendarSource = new CalendarSource("Consultation Calendar");
        calendarSource.getCalendars().addAll(professionalCalendar, recommendationCalendar);

        // Add calendar to view
        monthView.getCalendarSources().add(calendarSource);

        // Add date selection listener
        monthView.setOnMouseClicked(event -> {
            LocalDate clicked = monthView.getDate();
            if (!unavailableDates.contains(clicked)) {
                selectedDate = clicked;
                updateStatusLabel(clicked);
                System.out.println("Date selected: " + selectedDate);
            } else {
                updateStatusLabel(clicked);
            }
        });

        setTop(topContainer);
        setCenter(monthView);

        // Apply CSS for custom styling
        getStyleClass().add("calendar-component");
    }

    private void updateStatusLabel(LocalDate date) {
        if (unavailableDates.contains(date)) {
            statusLabel.setText("Cette date n'est pas disponible.");
            statusLabel.setTextFill(Color.RED);
        }
        else if (date.isBefore(LocalDate.now()))
        {
            statusLabel.setText("Veuillez choisir une date à partir de demain.");
            statusLabel.setTextFill(Color.RED);
        }
        else if ( date.isEqual(LocalDate.now()))
        {
            statusLabel.setText("La date d'aujourd'hui n'est pas autorisée. Choisissez à partir de demain.");
            statusLabel.setTextFill(Color.RED);
        }
        else if (recommendedDates.contains(date)) {
            statusLabel.setText("Date recommandée basée sur votre raison de consultation.");
            statusLabel.setTextFill(Color.GREEN);
        } else {
            statusLabel.setText("Date disponible.");
            statusLabel.setTextFill(Color.BLACK);
        }
    }

    public void updateAIRecommendation(String reason) {
        if (professional == null || reason == null || reason.trim().isEmpty()) {
            return;
        }

        this.consultationReason = reason;

        // Clear previous recommendations
        recommendationCalendar.clear();
        recommendedDates.clear();

        // Get new recommendations from AI
        currentRecommendation = aiService.analyzeConsultationReason(reason, professional);

        // Get recommended dates
        recommendedDates = currentRecommendation.getRecommendedDates(LocalDate.now());

        // Add entries to the recommendation calendar
        for (LocalDate date : recommendedDates) {
            if (!unavailableDates.contains(date)) {
                Entry<String> entry = new Entry<>("Recommended");
                entry.setFullDay(true);
                entry.changeStartDate(date);
                entry.changeEndDate(date);

                // Set color based on urgency
                switch (currentRecommendation.getUrgencyLevel()) {
                    case URGENT:
                        entry.setTitle("URGENT");
                        break;
                    case HIGH:
                        entry.setTitle("HIGH Priority");
                        break;
                    case NORMAL:
                        entry.setTitle("Recommended");
                        break;
                    case ROUTINE:
                        entry.setTitle("Good Option");
                        break;
                }

                recommendationCalendar.addEntry(entry);
            }
        }

        // Update recommendation label in the UI
        updateRecommendationLabel();
    }

    private void updateRecommendationLabel() {
        if (currentRecommendation != null) {
            recommendationLabel.setText(currentRecommendation.getExplanation());
            recommendationLabel.setWrapText(true);

            // Set color based on urgency
            switch (currentRecommendation.getUrgencyLevel()) {
                case URGENT:
                    recommendationLabel.setTextFill(Color.RED);
                    break;
                case HIGH:
                    recommendationLabel.setTextFill(Color.ORANGE);
                    break;
                default:
                    recommendationLabel.setTextFill(Color.BLACK);
            }
        }
    }

    public void setProfessional(User professional) {
        this.professional = professional;
        if (professional != null) {
            professionalCalendar.setName(professional.getNameUser() + "'s Calendar");
            loadProfessionalConsultations();
        }
    }

    public void loadProfessionalConsultations() {
        if (professional == null) {
            return;
        }

        // Clear existing entries and unavailable dates
        professionalCalendar.clear();
        unavailableDates.clear();

        try {
            // Get all consultations for the professional
            List<Consultation> consultations = consultationService.findByProfessional(professional.getId());

            for (Consultation consultation : consultations) {
                LocalDate date = consultation.getDateConsultation();
                unavailableDates.add(date);

                // Create entry for the consultation
                Entry<String> entry = new Entry<>("réservé");
                entry.setFullDay(true);
                entry.changeStartDate(date);
                entry.changeEndDate(date);
                professionalCalendar.addEntry(entry);
            }
        } catch (SQLException e) {
            System.err.println("Error loading consultations: " + e.getMessage());
        }
    }

    public boolean isProfessionalAvailable(LocalDate date) {
        return !unavailableDates.contains(date);
    }

    public boolean isRecommendedDate(LocalDate date) {
        return recommendedDates.contains(date);
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setDate(LocalDate date) {
        monthView.setDate(date);
        selectedDate = date;
    }

    public ConsultationAIService.ConsultationRecommendation getCurrentRecommendation() {
        return currentRecommendation;
    }
}