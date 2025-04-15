package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.RendezVous;
import entities.Roles;
import entities.User;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.ConsultationService;
import services.RendezVousService;
import utils.SUser;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ListRendezVousController extends NavigateurController {

    private final RendezVousService rendezVousService = new RendezVousService();



    @FXML
    public void initialize() {
        rendezVousListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(RendezVous rendezVous, boolean empty) {
                super.updateItem(rendezVous, empty);

                if (empty || rendezVous == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox rowBox = new HBox(10);
                    rowBox.setPadding(new Insets(5));
                    rowBox.prefWidthProperty().bind(rendezVousListView.widthProperty().subtract(30));

                    // Style for alternating row colors
                    if (getIndex() % 2 == 0) {
                        rowBox.setStyle("-fx-background-color: #f0f0f0;");
                    } else {
                        rowBox.setStyle("-fx-background-color: #ffffff;");
                    }

                    // Header row
                    if (rendezVous.getId() == -1) {
                        // Header styling
                        rowBox.setStyle("-fx-background-color: #212529;");

                        // Create header labels
                        String[] headers = {"Date", "Statut", "Professional", "Actions"};
                        for (String header : headers) {
                            Label headerLabel = createDynamicWidthLabel(header, true);
                            headerLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                            rowBox.getChildren().add(headerLabel);
                        }
                    }
                    else {
                        // Data row
                        Label dateLabel = createDynamicWidthLabel(rendezVous.getDateRdv().toString(), false);
                        Label statutLabel = createDynamicWidthLabel(rendezVous.getStatusRdv(), false);
                        Label professionalLabel = createDynamicWidthLabel(rendezVous.getProfessional().getNameUser(), false);

                        // Create action buttons with dynamic sizing
                        HBox actionButtons = new HBox(5);
                        Button viewBtn = createActionButton(new FontAwesomeIconView(FontAwesomeIcon.EYE));


                        actionButtons.getChildren().add(viewBtn);

                        // If user is a professional and status is "accepted", add create consultation button
                        if (SUser.getUser().getRole().equals(Roles.ROLE_PATIENT) &&
                                "accepted".equalsIgnoreCase(rendezVous.getStatusRdv())) {
                            Button createConsultationBtn = createActionButton(new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE));
                            createConsultationBtn.setOnAction(event -> {
                                handleCreateConsultation(rendezVous);
                            });
                            actionButtons.getChildren().add(createConsultationBtn);
                        }

                        // Add to row
                        rowBox.getChildren().addAll(
                                dateLabel,
                                statutLabel,
                                professionalLabel,
                                actionButtons
                        );
                    }

                    setGraphic(rowBox);
                    setOnMouseEntered(event -> setStyle("-fx-background-color: transparent;"));
                    setOnMouseExited(event -> setStyle("-fx-background-color: transparent;"));
                }
            }

            // Helper method to create labels with dynamic width and text wrapping
            private Label createDynamicWidthLabel(String text, boolean isHeader) {
                Label label = new Label(text);

                // Configure label properties for wrapping and dynamic width
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setPrefWidth(0);

                // Bind label width to a fraction of the ListView width
                label.prefWidthProperty().bind(Bindings.divide(rendezVousListView.widthProperty(), 4));

                // Style labels to allow wrapping
                label.setStyle(
                        "-fx-text-alignment: left; " +
                                "-fx-wrap-text: true; " +
                                (isHeader ? "-fx-text-fill: white; -fx-font-weight: bold;" : "-fx-text-fill: black;")
                );

                // Add tooltip for full text
                Tooltip tooltip = new Tooltip(text);
                label.setTooltip(tooltip);

                return label;
            }

            // Helper method to create action buttons
            private Button createActionButton(FontAwesomeIconView icon) {
                Button button = new Button();
                icon.setSize("10");
                icon.setFill(Color.WHITE);
                button.setGraphic(icon);
                button.setStyle("-fx-background-color: #1A76D1;");

                button.setOnMouseEntered(event -> {
                    button.setStyle("-fx-background-color: black; -fx-background-radius: 4px; -fx-cursor: hand;");
                });

                button.setOnMouseExited(event -> {
                    button.setStyle("-fx-background-color: #1A76D1; -fx-background-radius: 4px; -fx-cursor: hand;");
                });

                // Bind button widths to dynamic column width
                button.prefWidthProperty().bind(Bindings.divide(rendezVousListView.widthProperty(), 12));

                return button;
            }
        });

        rendezVousListView.setStyle(
                "-fx-border-color: white; " +
                        "-fx-border-width: 0px; " +
                        "-fx-border-radius: 0px;"
        );

        try {
            rendezVousListView.setItems(getRendezVousWithHeader());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            showAlert("Erreur", "Les données du rendez-vous ne peuvent pas être charger: " + e.getMessage());
        }
    }



    private void handleCreateConsultation(RendezVous rendezVous) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddConsultationForm.fxml"));
            Parent root = loader.load();

            // Get controller and set the rendez-vous
            AddConsultationController controller = loader.getController();
            controller.initData(rendezVous);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Créer une Consultation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh the list after creating consultation
            rendezVousListView.setItems(getRendezVousWithHeader());

        } catch (IOException | SQLException e) {
            showAlert("Erreur", "Échec de l'ouverture du formulaire de création de consultation: " + e.getMessage());
        }
    }




    @FXML
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}