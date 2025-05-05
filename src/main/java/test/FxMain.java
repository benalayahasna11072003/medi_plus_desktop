package test;

import entities.Roles;
import entities.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.UserService;

import java.sql.SQLException;

public class FxMain extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        UserService userService = new UserService();
        User user = new User();
        user.setNameUser("test");
        user.setEmail("test@gmail.com");
        user.setPassword("123456789");
        user.setRole(String.valueOf(Roles.ROLE_PATIENT));
        
        try {
            userService.insertOne(user);
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}