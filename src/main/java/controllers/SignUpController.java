package controllers;

import entities.Roles;
import entities.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class SignUpController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<Roles> roleComboBox;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        // Initialiser la ComboBox avec les rôles disponibles
        roleComboBox.setItems(FXCollections.observableArrayList(Roles.values()));
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        Roles role = roleComboBox.getValue();

        // Validation des champs
        StringBuilder errors = new StringBuilder();

        if (name.isEmpty()) {
            errors.append("Le nom est requis.\n");
        }
        if (email.isEmpty()) {
            errors.append("L'email est requis.\n");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.append("L'email n'est pas valide.\n");
        }
        if (role == null) {
            errors.append("Le rôle est requis.\n");
        }
        if (password.isEmpty()) {
            errors.append("Le mot de passe est requis.\n");
        } else if (password.length() < 8) {
            errors.append("Le mot de passe doit contenir au moins 8 caractères.\n");
        }
        if (!password.equals(confirmPassword)) {
            errors.append("Les mots de passe ne correspondent pas.\n");
        }

        if (errors.length() > 0) {
            showAlert("Erreur de validation", errors.toString());
            return;
        }

        // Création du nouvel utilisateur
        User newUser = new User();
        newUser.setNameUser(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(String.valueOf(role));

        try {
            // Vérifier si l'email est déjà utilisé
            if (userService.findByEmail(email) != null) {
                showAlert("Erreur", "Cet email est déjà utilisé.");
                return;
            }

            // Ajouter l'utilisateur
            userService.insertOne(newUser);
            showAlert("Succès", "Inscription réussie !");

            // Fermer la fenêtre d'inscription
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();

            // Ouvrir la fenêtre de connexion
            openLoginWindow();

        } catch (SQLException e) {
            showAlert("Erreur SQL", "Erreur lors de l'inscription : " + e.getMessage());
        } catch (IOException e) {
            showAlert("Erreur d'affichage", "Impossible d'ouvrir la fenêtre de connexion : " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            // Fermer la fenêtre d'inscription
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();

            // Ouvrir la fenêtre de connexion
            openLoginWindow();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de connexion : " + e.getMessage());
        }
    }

    private void openLoginWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Connexion");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
