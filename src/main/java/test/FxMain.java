package test;

import entities.Avis;
import entities.Roles;
import entities.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.AvisService;
import services.UserService;

import java.sql.SQLException;

public class FxMain extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListConsultationView.fxml"));

        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("List Consultation");
        stage.setScene(scene);

        stage.show();
    }
}