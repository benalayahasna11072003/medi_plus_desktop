package controllers;

import entities.Avis;
import entities.Reponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.AvisService;
import services.ReponseService;
import utils.SUser;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AvisDetailsController extends NavigateurController {

    public Button deleteButton;
    @FXML
    private Label patientValue;
    @FXML
    private Label professionalValue;
    @FXML
    private Circle ratingCircle;
    @FXML
    private Label ratingValue;
    @FXML
    private Label dateValue;
    @FXML
    private Label commentValue;
    @FXML
    private ListView<Reponse> responsesListView;
    private final AvisService avisService = new AvisService();

    private Avis avis;
    private List<Reponse> responses;

    public void setData(Avis avis, List<Reponse> responses) {
        this.avis = avis;
        this.responses = responses;
        populateData();
    }

    private void populateData() {
        // Set values in UI components
        patientValue.setText(avis.getUser().getNameUser());
        professionalValue.setText(avis.getProfessional().getNameUser());
        ratingValue.setText(String.valueOf(avis.getNote()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateValue.setText(avis.getDateAvis().toString());//.format(formatter));

        commentValue.setText(avis.getCommentaire());

        // Configure responses ListView with a custom cell factory
        responsesListView.setCellFactory(listView -> new ResponseListCell());
        responsesListView.getItems().setAll(responses);
    }

    public void handleDeleteAvis(ActionEvent actionEvent) {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer avis");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette avis?");
        alert.setContentText("Cette action ne peut pas être annulée.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    // Delete from database
                    avisService.deleteOne(avis);

                    // Refresh the list
                    //reviewsListView.setItems(getSampleReviewsWithHeader());

                    showAlert("Succès", "La avis a été supprimée avec succès");
                    handleListAvis(actionEvent);
                } catch (SQLException e) {
                    showAlert("Erreur", "Échec de la suppression: " + e.getMessage());
                }
            }
        });
    }

    // Custom ListCell for responses
    private class ResponseListCell extends ListCell<Reponse> {
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

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Label responseDate = new Label("Responded on: " + response.getDateReponse());//.format(formatter));
                responseDate.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
                container.getChildren().addAll(responderName, responseText, responseDate);
                // Check if the current user is the owner of this response
                if (SUser.getUser() != null && SUser.getUser().getId() == response.getMadeBy().getId()) {
                    // Add buttons for update and delete
                    HBox buttonBox = new HBox(10);
                    buttonBox.setPadding(new Insets(5, 0, 0, 0));

                    Button updateButton = new Button("Update");
                    updateButton.getStyleClass().add("btn-primary");
                    updateButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                    updateButton.setGraphic(new Label(""));
                    updateButton.setGraphicTextGap(5);
                    updateButton.setPrefWidth(100);
                    updateButton.setOnAction(e -> handleUpdateResponse(response));

                    Button deleteButton = new Button("Delete");
                    deleteButton.getStyleClass().add("btn-danger");
                    deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                    deleteButton.setGraphic(new Label(""));
                    deleteButton.setGraphicTextGap(5);
                    deleteButton.setPrefWidth(100);
                    deleteButton.setOnAction(e -> handleDeleteResponse(response));

                    buttonBox.getChildren().addAll(updateButton, deleteButton);
                    container.getChildren().add(buttonBox);
                }

                setGraphic(container);
            }
        }
    }
    // Handle update response action
    private void handleUpdateResponse(Reponse response) {
        // Show update dialog and refresh list upon successful update
        UpdateResponseController.showUpdateResponseDialog(response, () -> {
            // Refresh the responses from database
            ReponseService reponseService = new ReponseService();
            try {
                responses = reponseService.getResponsesByAvisId(avis.getRef());
                responsesListView.getItems().setAll(responses);
            } catch (SQLException e) {
                showAlert("SQL Error", e.getMessage());
            }
        });
    }



    // Handle delete response action
    private void handleDeleteResponse(Reponse response) {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Response");
        alert.setHeaderText("Are you sure you want to delete this response?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // Delete from database and refresh the list
                ReponseService reponseService = new ReponseService();
                try {
                    reponseService.deleteOne(response);
                    responses.remove(response);
                    responsesListView.getItems().setAll(responses);
                } catch (SQLException e) {
                    showAlert("SQL erreur", e.getMessage());
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

    // Static method to open the popup
    public static void showAvisDetails(Avis avis, List<Reponse> responses) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(AvisDetailsController.class.getResource("/AvisDetailsPopup.fxml"));
            BorderPane root = loader.load();

            // Get controller
            AvisDetailsController controller = loader.getController();
            controller.setData(avis, responses);

            // Create stage
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Review Details");
            popup.setResizable(false);

            // Set scene
            Scene scene = new Scene(root);
            popup.setScene(scene);

            // Show popup
            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load review details: " + e.getMessage());
            alert.showAndWait();
        }

    }


}