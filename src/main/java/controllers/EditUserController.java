package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.UserService;
import entities.User;

import java.sql.SQLException;

public class EditUserController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private User user;
    private UserService userService;

    @FXML
    public void initialize() {
        userService = new UserService();
        roleComboBox.getItems().addAll("ADMIN", "PATIENT", "PROFESSIONNEL");
    }

    public void setUser(User user) {
        this.user = user;
        populateFields();
    }

    private void populateFields() {
        if (user != null) {
            nameField.setText(user.getNameUser());
            emailField.setText(user.getEmail());
            roleComboBox.setValue(user.getRole());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) {
            return;
        }

        try {
            updateUser();
            userService.updateOne(user);
            showSuccess("Modification réussie", "L'utilisateur a été modifié avec succès.");
            closeWindow();
        } catch (SQLException e) {
            showError("Erreur de modification", "Une erreur est survenue lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        // Reset styles
        resetFieldStyles();

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String role = roleComboBox.getValue();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation nom
        if (name.isEmpty()) {
            errors.append("Le nom est obligatoire.\n");
            markFieldError(nameField);
        } else if (!name.matches("^[\\p{L} .'-]+$")) {
            errors.append("Le nom contient des caractères invalides.\n");
            markFieldError(nameField);
        }

        // Validation email
        if (email.isEmpty()) {
            errors.append("L'email est obligatoire.\n");
            markFieldError(emailField);
        } else if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            errors.append("L'email n'est pas valide.\n");
            markFieldError(emailField);
        }

        // Rôle
        if (role == null || role.isEmpty()) {
            errors.append("Le rôle est obligatoire.\n");
            markFieldError(roleComboBox);
        }

        // Mot de passe (facultatif mais contrôlé si présent)
        if (!password.isEmpty()) {
            if (password.length() < 6) {
                errors.append("Le mot de passe doit contenir au moins 6 caractères.\n");
                markFieldError(passwordField);
            }
            if (!password.matches(".*\\d.*")) {
                errors.append("Le mot de passe doit contenir au moins un chiffre.\n");
                markFieldError(passwordField);
            }
            if (!password.matches(".*[A-Z].*")) {
                errors.append("Le mot de passe doit contenir au moins une majuscule.\n");
                markFieldError(passwordField);
            }
            if (!password.equals(confirmPassword)) {
                errors.append("Les mots de passe ne correspondent pas.\n");
                markFieldError(confirmPasswordField);
            }
        }

        if (errors.length() > 0) {
            showError("Erreur de validation", errors.toString());
            return false;
        }

        return true;
    }

    private void resetFieldStyles() {
        nameField.setStyle("");
        emailField.setStyle("");
        roleComboBox.setStyle("");
        passwordField.setStyle("");
        confirmPasswordField.setStyle("");
    }

    private void markFieldError(Control field) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
    }

    private void updateUser() {
        user.setNameUser(nameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setRole(roleComboBox.getValue());

        if (!passwordField.getText().isEmpty()) {
            user.setPassword(passwordField.getText());
        }
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
