package controllers;

import entities.Reponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.gestionAvis.ReponseService;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateResponseController extends NavigateurController{

    @FXML
    private TextArea responseTextArea;

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    private Reponse response;
    private Runnable onUpdateSuccessCallback;

    public void setData(Reponse response, Runnable onUpdateSuccessCallback) {
        this.response = response;
        this.onUpdateSuccessCallback = onUpdateSuccessCallback;

        // Set the current response text in the text area
        responseTextArea.setText(response.getReponse());
    }

    @FXML
    private void handleSave() {
        String updatedText = responseTextArea.getText().trim();

        if (updatedText.isEmpty()) {
            showAlert("Erreur", "La réponse ne peut pas être vide !");
            return;
        }else if (updatedText.length()<3){
            showAlert("Erreur", "La réponse ne peut pas dépasser 3 caractères !");
            return;
        }
        // Check for bad words
        if (containsBadWords(updatedText)) {
            showAlert("Contenu inapproprié", "Votre réponse contient des mots inappropriés. Veuillez modifier votre texte.");
            return;
        }
        try {
            // Update the response object
            response.setReponse(updatedText);

            // Save to database
            ReponseService reponseService = new ReponseService();
            reponseService.updateOne(response);

            // Execute callback to refresh the list in the parent view
            if (onUpdateSuccessCallback != null) {
                onUpdateSuccessCallback.run();
            }

            // Close the window
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            showAlert("SQL Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        // Just close the window without saving
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Static method to open the popup
    public static void showUpdateResponseDialog(Reponse response, Runnable onUpdateSuccessCallback) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(UpdateResponseController.class.getResource("/gestionAvis/UpdateResponse.fxml"));
            BorderPane root = loader.load();

            // Get controller
            UpdateResponseController controller = loader.getController();
            controller.setData(response, onUpdateSuccessCallback);

            // Create stage
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Update Response");
            popup.setResizable(false);

            // Set scene
            Scene scene = new Scene(root);
            popup.setScene(scene);

            // Show popup
            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load update response dialog: " + e.getMessage());
            alert.showAndWait();
        }
    }
}