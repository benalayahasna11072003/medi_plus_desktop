package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.SplitPane;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import services.BlocService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class AdminController {
    private final BlocService service=new BlocService();


    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Button btnBloc;
    @FXML
    private Button btnSpecialite;

    @FXML
    private Text welcomeText;

    @FXML
    void initialize() throws SQLException {
        welcomeText.setText("Welcome to MediPlus!");
        btnBloc.setOnAction(event -> loadAllBlocs());
        btnSpecialite.setOnAction(event -> loadSpecialite());
    }

    @FXML
    private void loadAllBlocs() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Blocs.fxml"));
            Node blocView = loader.load();
            mainBorderPane.setCenter(blocView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void loadSpecialite() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Specialite.fxml"));
            Node specialiteView = loader.load();
            mainBorderPane.setCenter(specialiteView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void loadHomepage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/specialiteFront.fxml"));
            Parent specialiteRoot = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(specialiteRoot);
            scene.getStylesheets().add(getClass().getResource("/stylefront.css").toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

