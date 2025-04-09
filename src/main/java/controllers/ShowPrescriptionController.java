package controllers;

import entities.Prescription;
import entities.Prescription;
import entities.Reponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.SUser;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class ShowPrescriptionController {
    public Label descriptionValue;






    private Prescription prescription;


    public void setData(Prescription prescription) {
        this.prescription = prescription;

        populateData();
    }

    private void populateData() {
        // Set values in UI components
        descriptionValue.setText(prescription.getDescription());



    }



    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Static method to open the popup
    public static void showPrescriptionDetails(Prescription prescription) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(ShowPrescriptionController.class.getResource("/ShowPrescriptionsDetails.fxml"));
            BorderPane root = loader.load();

            // Get controller
            ShowPrescriptionController controller = loader.getController();
            controller.setData(prescription);

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