package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import services.UserService;
import java.util.regex.Pattern;

public class ResetPasswordFormController {
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    private String resetToken;
    private UserService userService;
    
    public void initialize() {
        userService = new UserService();
    }
    
    public void setResetToken(String token) {
        this.resetToken = token;
    }
    
    @FXML
    private void handleResetPassword() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas");
            return;
        }
        
        if (!isValidPassword(password)) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre");
            return;
        }
        
        try {
            boolean success = userService.updatePasswordWithToken(resetToken, password);
            if (success) {
                showAlert("Succès", "Votre mot de passe a été réinitialisé avec succès");
                closeWindow();
            } else {
                showAlert("Erreur", "Le lien de réinitialisation est invalide ou a expiré");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la réinitialisation du mot de passe");
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return Pattern.matches(passwordPattern, password);
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.close();
    }
} 