package controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.UserService;
import services.GoogleAuthService;
import services.GoogleAuthService.GoogleUserInfo;
import javafx.application.Platform;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Desktop;
import java.net.URI;

public class loginControlleur {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Button googleLoginButton;

    private final UserService userService;
    private final GoogleAuthService googleAuthService;
    private int failedAttempts = 0;
    private final int MAX_ATTEMPTS = 3;
    private Timer lockoutTimer;

    public loginControlleur() {
        this.userService = new UserService();
        try {
            this.googleAuthService = new GoogleAuthService();
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du GoogleAuthService: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Impossible d'initialiser le service Google", e);
        }
    }

    @FXML
    public void initialize() {
        try {
            System.out.println("Initialisation du contrôleur de login...");
            if (forgotPasswordLink != null) {
                forgotPasswordLink.setOnAction(this::handleForgotPassword);
            } else {
                System.err.println("forgotPasswordLink est null");
            }
            
            if (googleLoginButton != null) {
                googleLoginButton.setOnAction(this::handleGoogleLogin);
            } else {
                System.err.println("googleLoginButton est null");
            }
            
            System.out.println("Initialisation terminée avec succès");
        } catch (Exception e) {
            System.err.println("Erreur détaillée lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        if (isLocked()) {
            showAlert("Compte verrouillé", "Trop de tentatives échouées. Veuillez réessayer dans 5 minutes.");
            return;
        }

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs");
            return;
        }

        try {
            User user = userService.login(email, password);
            if (user != null) {
                resetFailedAttempts();
                openMainWindow(user);
                // Fermer la fenêtre de connexion
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.close();
            } else {
                handleFailedLogin();
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la connexion: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la connexion à la base de données");
        } catch (IOException e) {
            System.err.println("Erreur IO lors de l'ouverture de la fenêtre: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre principale");
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Une erreur inattendue est survenue");
        }
    }

    private boolean isLocked() {
        return failedAttempts >= MAX_ATTEMPTS;
    }

    private void handleFailedLogin() {
        failedAttempts++;
        if (failedAttempts >= MAX_ATTEMPTS) {
            lockAccount();
            showAlert("Compte verrouillé", "Trop de tentatives échouées. Veuillez réessayer dans 5 minutes.");
        } else {
            showAlert("Erreur", "Email ou mot de passe incorrect. Tentatives restantes: " + (MAX_ATTEMPTS - failedAttempts));
        }
    }

    private void resetFailedAttempts() {
        failedAttempts = 0;
        if (lockoutTimer != null) {
            lockoutTimer.cancel();
            lockoutTimer = null;
        }
    }

    private void lockAccount() {
        disableLoginFields();
        if (lockoutTimer != null) {
            lockoutTimer.cancel();
        }
        lockoutTimer = new Timer();
        lockoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    enableLoginFields();
                    resetFailedAttempts();
                });
            }
        }, 5 * 60 * 1000); // 5 minutes
    }

    @FXML
    private void handleGoogleLogin(ActionEvent event) {
        try {
            if (googleAuthService == null) {
                System.err.println("GoogleAuthService n'est pas initialisé");
                showAlert("Erreur", "Service Google non initialisé");
                return;
            }
            
            // Désactiver le bouton pendant l'authentification
            googleLoginButton.setDisable(true);
            
            // Créer un thread séparé pour l'authentification
            new Thread(() -> {
                try {
                    System.out.println("Démarrage de l'authentification Google...");
                    String idToken = googleAuthService.startGoogleLogin();
                    
                    if (idToken != null) {
                        // Retourner sur le thread JavaFX
                        Platform.runLater(() -> {
                            try {
                                GoogleUserInfo googleUser = googleAuthService.verifyToken(idToken);
                                if (googleUser != null) {
                                    handleGoogleUserInfo(googleUser);
                                } else {
                                    showAlert("Erreur", "Impossible de vérifier l'identité Google");
                                }
                            } catch (Exception e) {
                                System.err.println("Erreur lors de la vérification du token: " + e.getMessage());
                                showAlert("Erreur", "Erreur lors de la vérification de l'identité");
                            } finally {
                                googleLoginButton.setDisable(false);
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            showAlert("Erreur", "L'authentification Google a échoué");
                            googleLoginButton.setDisable(false);
                        });
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        System.err.println("Erreur lors de l'authentification Google: " + e.getMessage());
                        e.printStackTrace();
                        showAlert("Erreur", "Erreur lors de l'authentification Google");
                        googleLoginButton.setDisable(false);
                    });
                }
            }).start();
            
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage de l'authentification: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du démarrage de l'authentification");
            googleLoginButton.setDisable(false);
        }
    }

    private void handleGoogleUserInfo(GoogleUserInfo googleUser) {
        try {
            User user = userService.findByEmail(googleUser.getEmail());
            
            if (user == null) {
                // Créer un nouvel utilisateur
                user = new User();
                user.setEmail(googleUser.getEmail());
                user.setNameUser(googleUser.getName());
                user.setRole("PATIENT"); // Rôle par défaut
                userService.insertOne(user);
            }
            
            openMainWindow(user);
            // Fermer la fenêtre de connexion
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement des informations utilisateur: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la création du compte");
        }
    }

    private void disableLoginFields() {
        emailField.setDisable(true);
        passwordField.setDisable(true);
        googleLoginButton.setDisable(true);
    }

    private void enableLoginFields() {
        emailField.setDisable(false);
        passwordField.setDisable(false);
        googleLoginButton.setDisable(false);
    }

    private void openMainWindow(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
        Parent root = loader.load();

        MainWindowController controller = loader.getController();
        controller.setUser(user);

        Stage stage = new Stage();
        stage.setTitle("MediPlus - " + user.getNameUser());
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

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Réinitialisation du mot de passe");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de la fenêtre de réinitialisation: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de réinitialisation");
        }
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUp.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Inscription");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de la fenêtre d'inscription: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'inscription");
        }
    }
}
