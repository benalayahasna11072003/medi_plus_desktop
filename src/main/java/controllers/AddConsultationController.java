package controllers;

import entities.Consultation;
import entities.RendezVous;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import services.gestionConsultation.ConsultationService;
import services.UserService;
import utils.SUser;

import java.sql.SQLException;
import java.time.LocalDate;

public class AddConsultationController extends NavigateurController {

    public Button enregistrerButton;
    private RendezVous rendezVous;

    private final ConsultationService consultationService = new ConsultationService();
    private final UserService userSerivce = new UserService();

    @FXML
    private TextField reasonField;

    @FXML
    private VBox calendarContainer;

    // Add our custom calendar component
    private CalendarComponent calendarComponent;

    //@FXML
  //  private Label aiRecommendationLabel;

    @FXML
    public void initialize() {
        // Initialize the calendar component
        calendarComponent = new CalendarComponent();

        // Add it to the container
        if (calendarContainer != null) {
            calendarContainer.getChildren().add(calendarComponent);
        }

        // Add listener to reason field for real-time AI recommendations
        reasonField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 10 && rendezVous != null && rendezVous.getProfessional() != null) {
                // Only update recommendation when we have enough text to analyze
                updateAIRecommendation(newValue);
            }
        });
    }

    private void updateAIRecommendation(String reason) {
        // Update calendar with AI recommendations
        calendarComponent.updateAIRecommendation(reason);

        // Show user feedback about the AI recommendation
      /*  if (calendarComponent.getCurrentRecommendation() != null) {
            //aiRecommendationLabel.setText(calendarComponent.getCurrentRecommendation().getExplanation());
            //aiRecommendationLabel.setVisible(true);
        }*/
    }

    @FXML
    void insertConsultation(ActionEvent event) {
        try {
            Consultation consultation = new Consultation();

            consultation.setReason(reasonField.getText());
            LocalDate selectedDate = calendarComponent.getSelectedDate();
            consultation.setDateConsultation(selectedDate);
            consultation.setRendezVous(rendezVous);

            consultation.setUser(SUser.getUser());
            consultation.setProfessionnel(rendezVous.getProfessional());

            if (consultation.getReason().equals("")) {
                showAlert("Erreur", "La reason ne doit pas être vide.");
                return;
            } else if (reasonField.getText().length() < 4) {
                showAlert("Erreur", "La reason doit contenir au moins 4 caractères.");
                return;
            }

            if (consultation.getDateConsultation() == null) {
                showAlert("Erreur", "La date de rendez-vous ne doit pas être vide.");
                return;
            }

            // Check if professional is available on selected date
            if (!calendarComponent.isProfessionalAvailable(selectedDate)) {
                showAlert("Erreur", "Le professionnel n'est pas disponible à cette date.");
                return;
            }
            if(selectedDate.isBefore(LocalDate.now())){
                showAlert("Erreur", "Veuillez choisir une date à partir de demain.");
                return;
            }
            if ( selectedDate.isEqual(LocalDate.now()))
            {
                showAlert("Erreur", "La date d'aujourd'hui n'est pas autorisée. Choisissez à partir de demain.");
                return;
            }


            // Get the AI recommendation to store with the consultation
            if (calendarComponent.getCurrentRecommendation() != null) {
                String aiNotes = "AI Analysis: " +
                        calendarComponent.getCurrentRecommendation().getUrgencyLevel() + ", " +
                        calendarComponent.getCurrentRecommendation().getExplanation();
                //consultation.setNotes(aiNotes);
            }

            consultationService.insertOne(consultation);
            showAlert("Succès", "Consultation planifiée avec succès!");

            // Close the window
            Stage stage = (Stage) enregistrerButton.getScene().getWindow();
            stage.close();
            handleListConsultation(event);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La note doit être un nombre valide.");
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de l'ajout de la consultation: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'ajout de la consultation: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void initData(RendezVous rendezVous) {
        this.rendezVous = rendezVous;
        if (rendezVous.getProfessional() != null && calendarComponent != null) {
            calendarComponent.setProfessional(rendezVous.getProfessional());
        }
    }
}