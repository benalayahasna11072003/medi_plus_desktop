package controllers;

import entities.Consultation;
import entities.Consultation;
import entities.RendezVous;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.ConsultationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.sql.Date;

public class EditConsultationController {


    @FXML
    private TextField reasonField;

    @FXML
    private DatePicker dateField;
    @FXML
    private Button annulerButton;

    @FXML
    private Button enregistrerButton;

    private Consultation consultation ;
    private Runnable onUpdateSuccessCallback;

    private final ConsultationService consultationService = new ConsultationService();


    @FXML
    private void handleSave() {
        try {
            // Validate input fields
            if (reasonField.getText().isEmpty()) {
                showAlert("Error", "Veuillez saisir une raison pour la consultation");
                return;
            }

            if (dateField.getValue() == null) {
                showAlert("Error", "Veuillez sélectionner une date");
                return;
            }

            // Update consultation object with new values
            consultation.setReason(reasonField.getText());
            consultation.setDateConsultation(dateField.getValue());

            // Save to database
            consultationService.updateOne(consultation);

            // Show success message
            showAlert("Succès", "La consultation a été mise à jour avec succès");

            // Execute the callback to refresh the parent view
            if (onUpdateSuccessCallback != null) {
                onUpdateSuccessCallback.run();
            }

            // Close the window
            Stage stage = (Stage) enregistrerButton.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de la mise à jour: " + e.getMessage());
        }
    }

    public void setData(Consultation consultation, Runnable onUpdateSuccessCallback) {
        this.consultation = consultation;
        this.onUpdateSuccessCallback = onUpdateSuccessCallback;

        // Set current values in the fields
        reasonField.setText(consultation.getReason());

        // Convert Date to LocalDate for DatePicker
        if (consultation.getDateConsultation() != null) {
            LocalDate localDate = consultation.getDateConsultation();

            dateField.setValue(localDate);
        } else {
            dateField.setValue(LocalDate.now());
        }
    }


    @FXML
    private void handleCancel() {
        // Just close the window without saving
        Stage stage = (Stage) annulerButton.getScene().getWindow();
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
    public static void showUpdateConsultationDialog(Consultation consultation, Runnable onUpdateSuccessCallback) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(EditConsultationController.class.getResource("/UpdateConsultation.fxml"));
            BorderPane root = loader.load();

            // Get controller
            EditConsultationController controller = loader.getController();
            controller.setData(consultation, onUpdateSuccessCallback);

            // Create stage
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Modifier l'consultation");
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
            alert.setContentText("Échec du chargement du dialogue de modification de consultation: " + e.getMessage());
            alert.showAndWait();
        }
    }
}