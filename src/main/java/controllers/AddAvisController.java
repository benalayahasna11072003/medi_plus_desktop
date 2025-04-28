package controllers;

import entities.Avis;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.controlsfx.control.Rating;
import services.gestionAvis.AvisService;
import services.UserService;
import services.gestionAvis.MailAvisService;
import utils.SUser;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class AddAvisController extends NavigateurController {

    private final AvisService avisService = new AvisService();
    private final UserService userSerivce = new UserService();

    @FXML
    private TextArea commentField;

    @FXML
    private Rating starRating;

    @FXML
    private Label ratingValueLabel;

    @FXML
    private ComboBox<String> professionalCombo;

    @FXML
    void insertAvis(ActionEvent event) {
        try {
            int note = (int) starRating.getRating();

            if (note < 1) {
                showAlert("Erreur", "Veuillez sélectionner une note (entre 1 et 5 étoiles).");
                return;
            }

            Avis avis = new Avis();

            avis.setCommentaire(commentField.getText());
            // Use current system date
            avis.setDateAvis(Date.valueOf(LocalDate.now()));
            avis.setNote(note);
            avis.setUser(SUser.getUser());
            avis.setProfessional(userSerivce.findByEmail(professionalCombo.getValue()));

            if (avis.getCommentaire().equals("")) {
                showAlert("Erreur", "Le commentaire ne doit pas être vide.");
                return;
            } else if (avis.getCommentaire().length() < 3) {
                showAlert("Erreur", "Le commentaire doit contenir au moins 3 caractères.");
                return;
            }

            if (avis.getProfessional() == null) {
                showAlert("Erreur", "Vous devez choisir un professionnel.");
                return;
            }

            if (containsBadWords(avis.getCommentaire())) {
                showAlert("Contenu inapproprié", "Votre commentaire contient des mots inappropriés. Veuillez modifier votre texte.");
                return;
            }


            avisService.insertOne(avis);
            String doctEmail = avis.getProfessional().getEmail();
            String doctName = avis.getProfessional().getNameUser();
            String patientName = SUser.getUser().getNameUser();
            int noteM = avis.getNote();
            String comment = avis.getCommentaire();
            for (int i = 0; i < 3; i++) {
                try {
                    MailAvisService.sendAvisEmail(doctEmail, doctName, patientName, noteM, comment);
                    break; // succès : on quitte la boucle
                } catch (MessagingException | IOException e) {
                    if (i < 2) {
                        showAlert("Erreur d'envoi de l'email", "Une erreur est survenue lors de l'envoi du mail. Nouvelle tentative...");
                    } else {
                        showAlert("Échec de l'envoi de l'email", "Toutes les tentatives ont échoué. Veuillez réessayer plus tard.");
                    }
                    e.printStackTrace();
                }
            }
            showAlert("Succès", "Avis ajouté avec succès !");
            handleListAvis(event);
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de l'ajout de l'avis: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'ajout de l'avis: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        // Initialize the ComboBox with professionals
        try {
            professionalCombo.getItems().addAll(
                    avisService.selectAllProfessional().stream()
                            .map(User::getEmail)
                            .collect(Collectors.toList())
            );
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        // Configure the Rating control
        starRating.setMax(5);
        starRating.setPartialRating(false); // Only allow whole stars

        // Update rating value label when star rating changes
        starRating.ratingProperty().addListener((obs, oldVal, newVal) -> {
            ratingValueLabel.setText(newVal.intValue() + "/5");
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}