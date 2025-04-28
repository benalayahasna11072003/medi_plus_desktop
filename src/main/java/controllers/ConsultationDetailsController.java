package controllers;

import entities.Consultation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class ConsultationDetailsController {

    public Label consultationDetailsLabel;
    @FXML
    private Label patientValue;
    @FXML
    private Label raisonValue;
    @FXML
    private Label professionalValue;
    @FXML
    private Label dateValue;



    private Consultation consultation;


    public void setData(Consultation consultation) {
        this.consultation = consultation;
        String patientName = consultation.getUser().getNameUser();
        consultationDetailsLabel.setText("Détails de la consultation de "+patientName+" :");
        populateData();
    }

    private void populateData() {
        // Set values in UI components
        patientValue.setText(consultation.getUser().getNameUser());
        raisonValue.setText(consultation.getReason());
        professionalValue.setText(consultation.getProfessionnel().getNameUser());

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateValue.setText(consultation.getDateConsultation().toString());//.format(formatter));


    }




    // Static method to open the popup
    public static void showConsultationDetails(Consultation consultation) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(ConsultationDetailsController.class.getResource("/gestionConcultation/ConsultationDetailsPopup.fxml"));
            BorderPane root = loader.load();

            // Get controller
            ConsultationDetailsController controller = loader.getController();
            controller.setData(consultation);

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
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Échec du chargement des détails de l'avis: " + e.getMessage());
            alert.showAndWait();
        }

    }

    /*private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }*/

}