package controllers;

import entities.Avis;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;
import services.gestionAvis.AvisService;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateAvisController extends NavigateurController {

    @FXML
    private TextArea commentaireField;

    @FXML
    private Rating starRating;

    @FXML
    private Label ratingValueLabel;

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
        starRating.setRating(avis.getNote());
        ratingValueLabel.setText(avis.getNote() + "/5");
    }

    @FXML
    public void initialize() {
        // Configure the Rating control
        starRating.setMax(5);
        starRating.setPartialRating(false); // Only allow whole stars

        // Update rating value label when star rating changes
        starRating.ratingProperty().addListener((obs, oldVal, newVal) -> {
            ratingValueLabel.setText(newVal.intValue() + "/5");
        });
    }

    @FXML
    private void handleSave() {
        try {
            // Validate input
            String commentaire = commentaireField.getText().trim();
            if (commentaire.isEmpty()) {
                showAlert("Erreur", "Le commentaire ne peut pas être vide!");
                return;
            } else if (commentaire.length() < 3) {
                showAlert("Erreur", "Le commentaire doit contenir au moins 3 caractères.");
                return;
            }

            if (containsBadWords(commentaire)) {
                showAlert("Contenu inapproprié", "Votre commentaire contient des mots inappropriés. Veuillez modifier votre texte.");
                return;
            }

            int note = (int) starRating.getRating();
            if (note < 1) {
                showAlert("Erreur", "Veuillez sélectionner une note (entre 1 et 5 étoiles).");
                return;
            }

            // Update the avis object
            avis.setCommentaire(commentaire);
            avis.setNote(note);

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
            System.out.println(e.getMessage());
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
            FXMLLoader loader = new FXMLLoader(UpdateAvisController.class.getResource("/gestionAvis/UpdateAvis.fxml"));
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