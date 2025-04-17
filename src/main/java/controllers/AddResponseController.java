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

    private final Set<String> badWords = new HashSet<>(Arrays.asList(
            // Mild insults
            "idiot", "stupid", "dumb", "moron", "loser", "fool", "clown", "jerk",
            "weirdo", "creep", "lame", "brat", "trash", "pathetic", "sucker", "dope",

            // Negative adjectives
            "ugly", "gross", "disgusting", "worthless", "useless", "annoying", "lazy",
            "boring", "nonsense", "ridiculous", "terrible", "horrible", "awful", "toxic",

            // Dismissive or aggressive phrases
            "shut up", "get lost", "go away", "leave me alone", "screw off", "no one cares",
            "who asked", "what a joke", "you wish", "you're nothing", "no one likes you",

            // Condescending slang
            "cringe", "simp", "snowflake", "boomer", "karen", "neckbeard", "tryhard",
            "basic", "poser", "beta", "wannabe", "crybaby", "manchild",

            // Light online trolling words
            "rekt", "owned", "pwned", "git gud", "ez", "trash talk", "camping", "ragequit",
            "feed", "noob", "scrub", "griefer", "toxic player", "sweaty", "bot",

            // Tone-based or manipulative
            "fake", "liar", "cheater", "backstabber", "two-faced", "jealous", "clingy",
            "attention seeker", "overreacting", "overdramatic", "control freak", "delusional",
            "manipulative", "selfish", "vain", "insecure", "obsessed", "immature",

            // Emotionally aggressive or sarcastic
            "hate you", "i wish you would disappear", "you're a problem", "you're hopeless",
            "nobody wants you", "you ruin everything", "why are you like this", "what's wrong with you"
    ));

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
    private boolean containsBadWords(String text) {
        String textLower = text.toLowerCase();

        for (String badWord : badWords) {
            String pattern = "(?i).*\\b" + Pattern.quote(badWord.toLowerCase()) + "\\b.*";
            if (textLower.matches(pattern) || textLower.contains(badWord)) {
                return true;
            }
        }
        for (String badWord : BadWords.badWords) {
            String pattern = "(?i).*\\b" + Pattern.quote(badWord.toLowerCase()) + "\\b.*";
            if (textLower.matches(pattern) || textLower.contains(badWord)) {
                return true;
            }
        }

        return false;
    }

}