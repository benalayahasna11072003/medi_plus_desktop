package controllers;

import entities.Avis;
import entities.Reponse;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import services.ReponseService;
import utils.BadWords;
import utils.SUser;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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

    @FXML
    private TextArea responseTextArea;

    @FXML
    private Button submitButton;



    public void setAvis(Avis avis) {
        this.avis = avis;
        patientValue.setText(avis.getUser().getNameUser());
        professionalValue.setText(avis.getProfessional().getNameUser());
        dateValue.setText(avis.getDateAvis().toString());
        commentValue.setText(avis.getCommentaire());
        ratingValue.setText(String.valueOf(avis.getNote()));

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