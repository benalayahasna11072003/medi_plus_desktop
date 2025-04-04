package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigateurController {

    @FXML
    void handleListAvis(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListAvis.fxml"));
            Parent root = loader.load();

            Stage stage;
            if (event.getSource() instanceof MenuItem) {
                stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            } else {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


    @FXML
    void handleNewAvis(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreateAvis.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

}
