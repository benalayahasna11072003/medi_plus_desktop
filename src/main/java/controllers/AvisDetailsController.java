package controllers;

import entities.Avis;
import entities.Reponse;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;
import org.json.JSONObject;
import services.gestionAvis.AvisService;
import services.gestionAvis.ReponseService;
import utils.SUser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.Const.API_KEY;

public class AvisDetailsController extends NavigateurController {

    private static final Logger LOGGER = Logger.getLogger(AvisDetailsController.class.getName());

    public Button deleteButton;
    @FXML private Label patientValue;
    @FXML private Label professionalValue;
    @FXML private Rating starRating;
    @FXML private Label ratingValue;
    @FXML private Label dateValue;
    @FXML private Label commentValue;
    @FXML private ListView<Reponse> responsesListView;
    @FXML private MenuButton translateMenuButton;

    private final AvisService avisService = new AvisService();
    private Avis avis;
    private List<Reponse> responses;
    private String originalComment;
    private String translatedComment;

    @FXML
    public void initialize() {
        // Configure the Rating control to be non-editable
        starRating.setPartialRating(false);
        starRating.setDisable(true); // Make it read-only
    }

    public void setData(Avis avis, List<Reponse> responses) {
        this.avis = avis;
        this.responses = responses;
        populateData();
    }

    private void populateData() {
        patientValue.setText(avis.getUser().getNameUser());
        professionalValue.setText(avis.getProfessional().getNameUser());

        // Set the star rating and update the label
        starRating.setRating(avis.getNote());
        ratingValue.setText(avis.getNote() + "/5");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateValue.setText(avis.getDateAvis().toString());

        originalComment = avis.getCommentaire();
        commentValue.setText(originalComment);

        responsesListView.setCellFactory(listView -> new ResponseListCell());
        responsesListView.getItems().setAll(responses);
    }

    // New method to handle MenuItem translation actions
    @FXML
    public void handleTranslate(ActionEvent event) {
        MenuItem selectedItem = (MenuItem) event.getSource();
        String language = selectedItem.getText();

        // Update menu button text to show selected language
        translateMenuButton.setText("Traduire (" + language + ")");

        // Log the translation request
        LOGGER.info("Translation requested for language: " + language);
        System.out.println("Translation requested for language: " + language);

        // Translate the comment to the selected language
        translateComment(language);
    }

    // Handle the translation of the comment
    private void translateComment(String language) {
        // If the selected language is French, just show the original text
        if (language.equals("fr") || language.equalsIgnoreCase("French")) {
            commentValue.setText(originalComment);
            LOGGER.info("Displaying original French comment");
            System.out.println("Displaying original French comment");
            return;
        }

        // Show loading state for other languages
        commentValue.setText("Translating...");

        Task<String> translateTask = new Task<>() {
            @Override
            protected String call() {
                return translateText(originalComment, language);
            }
        };

        translateTask.setOnSucceeded(event -> {
            translatedComment = translateTask.getValue();
            commentValue.setText(translatedComment);
            LOGGER.info("Translation completed successfully");
            System.out.println("Translation completed successfully: " + translatedComment);
        });

        translateTask.setOnFailed(event -> {
            Throwable error = translateTask.getException();
            LOGGER.log(Level.SEVERE, "Translation failed", error);
            System.out.println("Translation failed: " + error.getMessage());
            commentValue.setText("Translation error: " + error.getMessage());
        });

        new Thread(translateTask).start();
    }

    // Translate text using the Google Translate API
    public static String translateText(String text, String targetLangCode) {
        HttpURLConnection conn = null;
        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-002:generateContent?key=" + API_KEY;
            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String prompt = String.format("Translate the following text to %s. Only return the translated sentence with no explanation or commentary:\n\"%s\"", targetLangCode, text);

            JSONObject requestBody = new JSONObject()
                    .put("contents", new org.json.JSONArray()
                            .put(new JSONObject()
                                    .put("parts", new org.json.JSONArray()
                                            .put(new JSONObject().put("text", prompt)))));

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                String errorResponse = getResponseContent(conn.getErrorStream());
                System.out.println("API Error - Response Code: " + responseCode);
                System.out.println("Error Response Body: " + errorResponse);
                return "Error " + responseCode + ": " + errorResponse;
            }

            String responseBody = getResponseContent(conn.getInputStream());
            System.out.println("API Success Response: " + responseBody);

            JSONObject jsonResponse = new JSONObject(responseBody);
            String translatedText = jsonResponse
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            return translatedText;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Translation exception: " + e.getClass().getName() + ": " + e.getMessage());
            return "Error: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // Helper method to get response content from stream
    private static String getResponseContent(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
        }
        return response.toString();
    }

    // Handle deletion of the review
    public void handleDeleteAvis(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer avis");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette avis?");
        alert.setContentText("Cette action ne peut pas être annulée.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    avisService.deleteOne(avis);
                    //showAlert("Succès", "L'avis a été supprimé avec succès");
                    handleListAvis(actionEvent);
                } catch (SQLException e) {
                    //showAlert("Erreur", "Échec de la suppression: " + e.getMessage());
                }
            }
        });
    }

    // Custom ListCell for displaying Reponse items
    private class ResponseListCell extends ListCell<Reponse> {
        private boolean isTranslated = false;
        private String originalText;
        private String translatedText;

        @Override
        protected void updateItem(Reponse response, boolean empty) {
            super.updateItem(response, empty);

            if (empty || response == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox container = new VBox(5);
                container.setPadding(new Insets(10));
                container.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 5;");

                Label responderName = new Label(response.getMadeBy().getNameUser());
                responderName.setStyle("-fx-font-weight: bold;");

                Label responseText = new Label(response.getReponse());
                responseText.setWrapText(true);
                originalText = response.getReponse();

                Label responseDate = new Label("Répondu le: " + response.getDateReponse());
                responseDate.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

                container.getChildren().addAll(responderName, responseText, responseDate);
                HBox buttonBox = new HBox(10);
                buttonBox.setPadding(new Insets(5, 0, 0, 0));
                // Create a MenuButton instead of a regular Button
                MenuButton translateMenuBtn = new MenuButton("Traduire");
                translateMenuBtn.getStyleClass().add("translate-button");
                translateMenuBtn.setPrefWidth(100);

                // Create language menu items
                MenuItem frenchItem = new MenuItem("fr");
                MenuItem englishItem = new MenuItem("en");
                MenuItem spanishItem = new MenuItem("es");
                MenuItem germanItem = new MenuItem("de");
                MenuItem italianItem = new MenuItem("it");
                // Add more languages as needed

                // Add the items to the menu button
                translateMenuBtn.getItems().addAll(frenchItem,englishItem, spanishItem, germanItem, italianItem);

                // Set action for each menu item
                for (MenuItem item : translateMenuBtn.getItems()) {
                    item.setOnAction(e -> {
                        final String language = item.getText();
                        translateMenuBtn.setText("Traduire (" + language + ")");

                        // Skip API call if the target language is French
                        if (language.equals("fr")) {
                            responseText.setText(originalText);
                            System.out.println("Displaying original French response");
                            return;
                        }

                        // Show loading state
                        responseText.setText("Traduction en cours...");

                        Task<String> translateTask = new Task<>() {
                            @Override
                            protected String call() {
                                return translateText(originalText, language);
                            }
                        };

                        translateTask.setOnSucceeded(ev -> {
                            translatedText = translateTask.getValue();
                            responseText.setText(translatedText);
                            System.out.println("Response translation successful");
                        });

                        translateTask.setOnFailed(ev -> {
                            System.out.println("Response translation failed: " + translateTask.getException().getMessage());
                            responseText.setText("Erreur de traduction: " + translateTask.getException().getMessage());
                        });

                        new Thread(translateTask).start();
                    });
                }
                if (SUser.getUser() != null && SUser.getUser().getId() == response.getMadeBy().getId()) {



                    Button updateButton = new Button("Modifier");
                    updateButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                    updateButton.setPrefWidth(100);
                    updateButton.setOnAction(e -> handleUpdateResponse(response));

                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                    deleteButton.setPrefWidth(100);
                    deleteButton.setOnAction(e -> handleDeleteResponse(response));

                    buttonBox.getChildren().addAll(translateMenuBtn, updateButton, deleteButton);
                    container.getChildren().add(buttonBox);
                }else{



                    buttonBox.getChildren().addAll(translateMenuBtn);
                    container.getChildren().add(buttonBox);
                }

                setGraphic(container);
            }
        }
    }

    private void handleUpdateResponse(Reponse response) {
        UpdateResponseController.showUpdateResponseDialog(response, () -> {
            ReponseService reponseService = new ReponseService();
            try {
                responses = reponseService.getResponsesByAvisId(avis.getRef());
                responsesListView.getItems().setAll(responses);
            } catch (SQLException e) {
                showAlert("Erreur SQL", e.getMessage());
            }
        });
    }

    private void handleDeleteResponse(Reponse response) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression de réponse");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réponse ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                ReponseService reponseService = new ReponseService();
                try {
                    reponseService.deleteOne(response);
                    responses.remove(response);
                    responsesListView.getItems().setAll(responses);
                } catch (SQLException e) {
                    showAlert("Erreur SQL", e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showAvisDetails(Avis avis, List<Reponse> responses) {
        try {
            FXMLLoader loader = new FXMLLoader(AvisDetailsController.class.getResource("/gestionAvis/AvisDetailsPopup.fxml"));
            BorderPane root = loader.load();

            AvisDetailsController controller = loader.getController();
            controller.setData(avis, responses);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Détails de l'avis");
            popupStage.setScene(new Scene(root));
            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}