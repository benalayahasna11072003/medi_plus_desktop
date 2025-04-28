package controllers;

import entities.Avis;
import entities.Reponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;
import services.gestionAvis.ReponseService;
import utils.SUser;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddResponseController extends NavigateurController {
    private Avis avis;
    private final ReponseService reponseService = new ReponseService();


    @FXML
    private Button backButton;

    @FXML
    private Label commentValue;

    @FXML
    private Label dateValue;

    @FXML
    private Label patientValue;

    @FXML
    private Label professionalValue;

    @FXML
    private Label ratingValue;
    @FXML private Rating starRating;

    @FXML
    private TextArea responseTextArea;

    @FXML
    private Button submitButton;
    @FXML
    public void initialize() {
        // Configure the Rating control to be non-editable
        starRating.setPartialRating(false);
        starRating.setDisable(true); // Make it read-only
    }


    public void setAvis(Avis avis) {
        this.avis = avis;
        patientValue.setText(avis.getUser().getNameUser());
        professionalValue.setText(avis.getProfessional().getNameUser());
        dateValue.setText(avis.getDateAvis().toString());
        commentValue.setText(avis.getCommentaire());
        //ratingValue.setText(String.valueOf(avis.getNote()));
        starRating.setRating(avis.getNote());
        ratingValue.setText(avis.getNote() + "/5");
    }
    @FXML
    void handleBackButton(ActionEvent event) {
        closePopup();
    }

    @FXML
    void handleSubmitButton(ActionEvent event) {
        String replyText = responseTextArea.getText().trim();
        if (!replyText.isEmpty()) {

            Reponse reponse = new Reponse();
            if(replyText.length()<3){
                showAlert("Erreur", "La réponse doit contenir au moins 3 caractères.");
                return;

            }

            // Check for bad words
            if (containsBadWords(replyText)) {
                showAlert("Contenu inapproprié", "Votre réponse contient des mots inappropriés. Veuillez modifier votre texte.");
                return;
            }


            reponse.setReponse(replyText);
            reponse.setDateReponse(Date.valueOf(LocalDate.now()));
            reponse.setProfessional(avis.getProfessional());
            reponse.setMadeBy(SUser.getUser());
            reponse.setAvis(avis);
            try {
                reponseService.insertOne(reponse);
                closePopup();
            } catch (SQLException e){
                showAlert("Erreur SQL:", e.getMessage());
            }

        }
        else{
            showAlert("Erreur", "La réponse ne peut pas être vide !");
        }
    }


    private void closePopup() {
        Stage stage = (Stage) responseTextArea.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}