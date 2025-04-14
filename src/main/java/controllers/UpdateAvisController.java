package controllers;

import entities.Avis;
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
import services.AvisService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.sql.Date;

public class UpdateAvisController {

    @FXML
    private TextField commentaireField;

    @FXML
    private TextField noteField;

    //@FXML
    //private DatePicker datePicker;

    @FXML
    private Button annulerButton;

    @FXML
    private Button enregistrerButton;

    private Avis avis;
    private Runnable onUpdateSuccessCallback;

    public void setData(Avis avis, Runnable onUpdateSuccessCallback) {
        this.avis = avis;
        this.onUpdateSuccessCallback = onUpdateSuccessCallback;

        // Set current values in the fields
        commentaireField.setText(avis.getCommentaire());
        noteField.setText(String.valueOf(avis.getNote()));

        // Convert Date to LocalDate for DatePicker
       /* if (avis.getDateAvis() != null) {
            LocalDate localDate = avis.getDateAvis().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            datePicker.setValue(localDate);
        } else {
            datePicker.setValue(LocalDate.now());
        }*/
    }

    @FXML
    private void handleSave() {
        try {
            // Validate input
            String commentaire = commentaireField.getText().trim();
            if (commentaire.isEmpty()) {
                showAlert("Erreur", "Le commentaire ne peut pas être vide!");
                return;
            }else if(avis.getCommentaire().length()<3){
                showAlert("Error", "Comment must at least 3 characters.");
                return;
            }

            int note;
            try {
                note = Integer.parseInt(noteField.getText().trim());
                if (note < 0 || note > 5) {
                    showAlert("Erreur", "La note doit être entre 0 et 5!");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "La note doit être un nombre!");
                return;
            }

           /* LocalDate selectedDate = datePicker.getValue();
            if (selectedDate == null) {
                showAlert("Erreur", "Veuillez sélectionner une date!");
                return;
            }*/

            // Update the avis object
            avis.setCommentaire(commentaire);
            avis.setNote(note);

            // Convert LocalDate to Date
         //   Date date = java.sql.Date.valueOf(LocalDate.now());
         //   avis.setDateAvis(date);

            // Save to database
            AvisService avisService = new AvisService();
            avisService.updateOne(avis);

            // Execute callback to refresh the data in the parent view
            if (onUpdateSuccessCallback != null) {
                onUpdateSuccessCallback.run();
            }

            // Close the window
            Stage stage = (Stage) enregistrerButton.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            showAlert("Erreur SQL", e.getMessage());
            System.out.println(e.getMessage());;
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
    public static void showUpdateAvisDialog(Avis avis, Runnable onUpdateSuccessCallback) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(UpdateAvisController.class.getResource("/UpdateAvis.fxml"));
            BorderPane root = loader.load();

            // Get controller
            UpdateAvisController controller = loader.getController();
            controller.setData(avis, onUpdateSuccessCallback);

            // Create stage
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Modifier l'avis");
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
            alert.setContentText("Échec du chargement du dialogue de modification d'avis: " + e.getMessage());
            alert.showAndWait();
        }
    }
}