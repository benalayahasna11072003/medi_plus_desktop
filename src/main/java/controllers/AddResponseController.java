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
import utils.SUser;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddResponseController extends NavigateurController {
    private Avis avis;
    private final ReponseService reponseService = new ReponseService();

    public void setAvis(Avis avis) {
        this.avis = avis;
    }
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

    @FXML
    void handleBackButton(ActionEvent event) {
        closePopup();
    }

    @FXML
    void handleSubmitButton(ActionEvent event) {
        String replyText = responseTextArea.getText().trim();
        if (!replyText.isEmpty()) {
            Reponse reponse = new Reponse();
            reponse.setReponse(replyText);
            reponse.setDateReponse(Date.valueOf(LocalDate.now()));
            reponse.setProfessional(avis.getProfessional());
            reponse.setMadeBy(SUser.getUser());
            reponse.setAvis(avis);
            try {
                reponseService.insertOne(reponse);
                closePopup();
            } catch (SQLException e){
                showAlert("SQL Erreur", e.getMessage());
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

}
