package controllers;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainWindowController {

    @FXML
    private Label userInfoLabel;

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
        updateUserInfo();
    }

    private void updateUserInfo() {
        if (currentUser != null) {
            userInfoLabel.setText("Connecté en tant que : " + currentUser.getNameUser() + 
                                " (" + currentUser.getRole() + ")");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Fermer la fenêtre principale
            Stage stage = (Stage) userInfoLabel.getScene().getWindow();
            stage.close();
            
            // Ouvrir la fenêtre de connexion
            openLoginWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUserManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserManagement.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Gestion des utilisateurs");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openLoginWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        
        Stage stage = new Stage();
        stage.setTitle("Connexion - MediPlus");
        stage.setScene(new Scene(root));
        stage.show();
    }
} 