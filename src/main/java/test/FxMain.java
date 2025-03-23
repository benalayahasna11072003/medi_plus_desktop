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
        UserService userService = new UserService();
        User user = new User();
        user.setNameUser("test");
        user.setEmail("test@gmail.com");
        user.setPassword("123456789");
        user.setRole(Roles.ROLE_USER);
        AvisService avisService = new AvisService();
        Avis avis =new Avis();
        avis.setCommentaire("first comment");
        try{
            userService.insertOne(user);
            User user1 = userService.findByEmail(user.getEmail());
            avis.setCommentaire("new comment");
            avis.setUser(user1);
            avisService.insertOne(avis);
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Avis.fxml"));

        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("Avis");
        stage.setScene(scene);

        stage.show();
    }
}