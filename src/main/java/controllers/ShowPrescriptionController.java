package controllers;

import entities.Prescription;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ShowPrescriptionController {
    public Label descriptionValue;
    public Label prescriptionDetailsLabel;


    private Prescription prescription;


    public void setData(Prescription prescription) {
        this.prescription = prescription;
        String patientName = prescription.getConsultation().getUser().getNameUser();
        prescriptionDetailsLabel.setText("Détails de l’ordonnance de "+patientName+" :");
        populateData();
    }

    private void populateData() {
        // Set values in UI components
        descriptionValue.setText(prescription.getDescription());



    }





    // Static method to open the popup
    public static void showPrescriptionDetails(Prescription prescription) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(ShowPrescriptionController.class.getResource("/gestionConcultation/ShowPrescriptionsDetails.fxml"));
            BorderPane root = loader.load();

            // Get controller
            ShowPrescriptionController controller = loader.getController();
            controller.setData(prescription);

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
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Échec du chargement des détails de l'avis: " + e.getMessage());
            alert.showAndWait();
        }

    }
    /*
        private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }*/
}