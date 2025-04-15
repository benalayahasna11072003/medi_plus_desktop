package controllers;

import entities.Consultation;
import entities.RendezVous;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ConsultationService;
import services.UserService;
import utils.SUser;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class AddConsultationController extends NavigateurController {

    public Button enregistrerButton;
    private  RendezVous rendezVous ;

    private final ConsultationService consultationService = new ConsultationService();
    private final UserService userSerivce = new UserService();


    @FXML
    private TextField reasonField;

    @FXML
    private DatePicker dateField;





    @FXML
    void insertConsultation(ActionEvent event) {
        try {
            Consultation consultation = new Consultation();

            consultation.setReason(reasonField.getText());
            consultation.setDateConsultation(dateField.getValue());
            consultation.setRendezVous(rendezVous);

            //System.out.println(userSerivce.findByEmail(professionalCombo.getValue()));
            consultation.setUser(SUser.getUser());
            consultation.setProfessionnel(rendezVous.getProfessional());
            if (consultation.getReason().equals("")){
                showAlert("Erreur", "La reason ne doit pas être vide.");
                return;
            }
            else if (reasonField.getText().length()<4){
                showAlert("Erreur", "La reason doit contenir au moins 4 caractères.");
                return;
            }
            if (consultation.getDateConsultation()==null){
                showAlert("Erreur", "La date de rendez-vous ne doit pas être vide.");
                return;
            }
            consultationService.insertOne(consultation); // Assuming you have an insert method in ConsultationService
            showAlert("Succès", "Avis ajouté avec succès!");
            // Close the window
            Stage stage = (Stage) enregistrerButton.getScene().getWindow();
            stage.close();
            handleListConsultation(event);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La note doit être un nombre valide.");
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de l'ajout de l'avis: " + e.getMessage());

        }
        catch (Exception e){
            showAlert("Erreur", "Échec de l'ajout de l'avis: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

 /*   @FXML
    public void initialize() {
        // Initialize the ComboBox with some sample professionals
        try {
            professionalCombo.getItems().addAll(
                    consultationService.selectAll().stream()
                            .map(User::getEmail) // Extract email from each User object
                            .collect(Collectors.toList()) // Collect into a list
            );

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }*/

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void initData(RendezVous rendezVous) {
        this.rendezVous = rendezVous;

    }


}