package controllers;

import entities.Reponse;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UpdateResponseDialogController {

    private Reponse response;
    private boolean confirmed = false;
    private String updatedText;

    public UpdateResponseDialogController(Reponse response) {
        this.response = response;
        this.updatedText = response.getReponse();
    }

    public boolean showDialog() {
        // Create dialog stage
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Update Response");
        dialog.setResizable(false);

        // Create layout
        BorderPane root = new BorderPane();

        // Header
        Label headerLabel = new Label("Update Your Response");
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-padding: 10px; -fx-font-weight: bold;");
        headerLabel.setPrefHeight(40);
        headerLabel.setPrefWidth(Double.MAX_VALUE);

        // Create a container for the header with blue background
        HBox headerBox = new HBox(headerLabel);
        headerBox.setPrefHeight(50);
        headerBox.setStyle("-fx-background-color: #007bff;");
        headerBox.setPrefWidth(Double.MAX_VALUE);
        HBox.setHgrow(headerLabel, Priority.ALWAYS);

        root.setTop(headerBox);

        // Content area
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        Label responseLabel = new Label("Your Response");
        TextField responseField = new TextField(response.getReponse());
        responseField.setPrefWidth(Double.MAX_VALUE);

        contentBox.getChildren().addAll(responseLabel, responseField);
        root.setCenter(contentBox);

        // Buttons
        Button saveButton = new Button("Save Response");
        saveButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        saveButton.setPrefWidth(150);
        saveButton.setOnAction(e -> {
            updatedText = responseField.getText();
            confirmed = true;
            dialog.close();
        });

        Button cancelButton = new Button("Back to Reviews");
        cancelButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        cancelButton.setPrefWidth(150);
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(20, saveButton, cancelButton);
        buttonBox.setPadding(new Insets(0, 20, 20, 20));
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        root.setBottom(buttonBox);

        // Show dialog
        Scene scene = new Scene(root, 600, 250);
        dialog.setScene(scene);
        dialog.showAndWait();

        return confirmed;
    }

    public String getUpdatedText() {
        return updatedText;
    }
}