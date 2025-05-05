package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import services.UserService;
import services.EmailService;
import entities.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.UUID;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ResetPasswordController {
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField resetCodeField;
    
    private UserService userService;
    private EmailService emailService;
    
    @FXML
    public void initialize() {
        userService = new UserService();
        emailService = new EmailService();
        resetCodeField.setVisible(false);
    }
    
    @FXML
    private void handleSendResetLink() {
        String email = emailField.getText();
        
        if (email.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer votre adresse email");
            return;
        }
        
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                showAlert("Erreur", "Aucun compte n'est associé à cette adresse email");
                return;
            }
            
            String resetToken = UUID.randomUUID().toString();
            LocalDateTime expiresAt = LocalDateTime.now().plus(1, ChronoUnit.HOURS);
            
            userService.updateResetToken(user.getId(), resetToken, expiresAt);
            emailService.sendResetPasswordEmail(email, resetToken);
            
            showAlert("Succès", "Un email contenant le code de réinitialisation a été envoyé à votre adresse email");
            resetCodeField.setVisible(true);
            
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'envoi de l'email");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleResetPassword() {
        String email = emailField.getText();
        String resetCode = resetCodeField.getText();
        
        if (email.isEmpty() || resetCode.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs");
            return;
        }
        
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                showAlert("Erreur", "Aucun compte n'est associé à cette adresse email");
                return;
            }
            
            if (resetCode.equals(user.getResetToken())) {
                openResetPasswordForm(resetCode);
            } else {
                showAlert("Erreur", "Code de réinitialisation invalide");
            }
            
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la vérification du code");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }
    
    private void openResetPasswordForm(String token) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPasswordForm.fxml"));
            Parent root = loader.load();
            
            ResetPasswordFormController controller = loader.getController();
            controller.setResetToken(token);
            
            Stage stage = new Stage();
            stage.setTitle("Réinitialisation du mot de passe");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();
            
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de réinitialisation");
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 