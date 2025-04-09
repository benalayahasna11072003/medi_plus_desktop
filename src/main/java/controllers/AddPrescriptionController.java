package controllers;

import entities.Consultation;
import entities.Prescription;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.PrescriptionService;
import services.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddPrescriptionController {

    private Consultation consultation;
    private Prescription prescription;

    @FXML
    private Button enregistrerButton;

    @FXML
    private TextField prescField;

    private final PrescriptionService prescriptionService = new PrescriptionService();
    private Runnable onUpdateSuccessCallback;

    @FXML
    void handleListConsultation(ActionEvent event) {

    }

    @FXML
    void insertPrescription(ActionEvent event) {

        try {


            // Validate input fields
            if (prescField.getText().isEmpty()) {
                showAlert("Error", "Veuillez saisir une raison pour la consultation");
                return;
            }
            if (prescription != null) {
                // Save to database
                prescription.setDescription(prescField.getText());
                prescriptionService.updateOne(prescription);
            } else {
                if (!prescField.getText().isEmpty()) {
                    Prescription prescription1 = new Prescription();
                    prescription1.setConsultation(consultation);
                    prescription1.setDescription(prescField.getText());
                    prescription1.setCreatedAt(LocalDate.now());

                    prescriptionService.insertOne(prescription1);
                }
            }
            // Show success message
            showAlert("Succès", "La prescription a été mise à entregistré avec succès");

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

    public static void showConsultationDetails(Consultation consultation, Runnable onUpdateSuccessCallback) throws SQLException{
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(AddPrescriptionController.class.getResource("/AddPrescription.fxml"));
            BorderPane root = loader.load();

            // Get controller
            AddPrescriptionController controller = loader.getController();
            controller.setData(consultation, onUpdateSuccessCallback);

            // Create stage
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Prescription Details");
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

    private void setData(Consultation consultation, Runnable onUpdateSuccessCallback) throws SQLException{

        this.onUpdateSuccessCallback = onUpdateSuccessCallback;
        this.consultation = consultation;
        Prescription prescription1 =prescriptionService.getPrescriptionsByConsultationId(consultation.getId()).stream().findFirst().orElse(null);
        if (prescription1!=null) {
            prescription = prescription1;
            prescField.setText(prescription.getDescription());
        }


    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
