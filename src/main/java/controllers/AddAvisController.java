package controllers;

import entities.Avis;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.AvisService;
import services.UserService;
import utils.SUser;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class AddAvisController extends NavigateurController {

    private final AvisService avisService = new AvisService();
    private final UserService userSerivce = new UserService();


    @FXML
    private TextArea commentField;

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField noteField;

    @FXML
    private ComboBox<String> professionalCombo;

    @FXML
    void insertAvis(ActionEvent event) {
        try {
            int note = Integer.parseInt(noteField.getText());
            Avis avis = new Avis();

            avis.setCommentaire(commentField.getText());
            avis.setDateAvis(java.sql.Date.valueOf(dateField.getValue()));
            if(note >5 || note <1){
                showAlert("Error", "Note must be between 1 and 5.");
                return;
            }
            avis.setNote(note);
            System.out.println(userSerivce.findByEmail(professionalCombo.getValue()));
            avis.setUser(SUser.getUser());
            avis.setProfessional(userSerivce.findByEmail(professionalCombo.getValue()));
            if (avis.getCommentaire().equals("")){
                showAlert("Error", "Comment must be not blank.");
                return;
            }else if(avis.getCommentaire().length()<3){
                showAlert("Error", "Comment must at least 3 characters.");
                return;
            }

            if (avis.getProfessional()==null){
                showAlert("Error", "you must choose a professional.");
                return;
            }
            avisService.insertOne(avis); // Assuming you have an insert method in AvisService
            showAlert("Success", "Review added successfully!");
            handleListAvis(event);
        } catch (NumberFormatException e) {
            showAlert("Error", "Note must be a valid number.");
        } catch (SQLException e) {
            showAlert("Error", "Failed to add review: " + e.getMessage());

        }
        catch (Exception e){
            showAlert("Error", "Failed to add review: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        // Initialize the ComboBox with some sample professionals
        try {
            professionalCombo.getItems().addAll(
                    avisService.selectAllProfessional().stream()
                            .map(User::getEmail) // Extract email from each User object
                            .collect(Collectors.toList()) // Collect into a list
            );

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
