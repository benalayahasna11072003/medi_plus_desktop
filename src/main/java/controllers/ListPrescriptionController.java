package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.*;
import entities.Prescription;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import services.gestionConsultation.PrescriptionService;
import utils.SUser;

import java.sql.SQLException;

public class ListPrescriptionController extends NavigateurController {


    private final PrescriptionService prescriptionService = new PrescriptionService();
    public ListView<Prescription> prescriptionListView;


    @FXML
    public void initialize() {
        prescriptionListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Prescription prescription, boolean empty) {
                super.updateItem(prescription, empty);

                if (empty || prescription == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox rowBox = new HBox(10);
                    rowBox.setPadding(new Insets(5));
                    rowBox.prefWidthProperty().bind(prescriptionListView.widthProperty().subtract(30));

                    // Style for alternating row colors
                    if (getIndex() % 2 == 0) {
                        rowBox.setStyle("-fx-background-color: #f0f0f0;");
                    } else {
                        rowBox.setStyle("-fx-background-color: #ffffff;");
                    }

                    // Header row
                    if (prescription.getId() == -1) {
                        // Header styling
                        rowBox.setStyle("-fx-background-color: #212529;");

                        // Create header labels
                        String[] headers = {"Date", "Professional", "Patient", "Actions"};
                        for (String header : headers) {
                            Label headerLabel = createDynamicWidthLabel(header, true);
                            headerLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                            rowBox.getChildren().add(headerLabel);
                        }
                    } else {
                        // Data row
                        Label dateLabel = createDynamicWidthLabel(prescription.getCreatedAt().toString(), false);
                        Label statutLabel = createDynamicWidthLabel(prescription.getConsultation().getProfessionnel().getNameUser(), false);
                        Label professionalLabel = createDynamicWidthLabel(prescription.getConsultation().getUser().getNameUser(), false);

                        // Create action buttons with dynamic sizing
                        HBox actionButtons = new HBox(5);
                        Button viewBtn = createActionButton(new FontAwesomeIconView(FontAwesomeIcon.EYE));
                        viewBtn.setOnAction(event -> {
                            openPrescriptionsDetailsPopup(prescription);
                        });

                        actionButtons.getChildren().add(viewBtn);

                        if (SUser.getUser().getRole().equals(Roles.professionnel)) {
                            Button deletPrescriptionBtn = createActionButton(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
                            deletPrescriptionBtn.setOnAction(event -> {
                                handleDeletePrescription(prescription);
                            });
                            actionButtons.getChildren().add(deletPrescriptionBtn);
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

            private void handleDeletePrescription(Prescription prescription) {
                // Show confirmation dialog
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Supprimer Prescription");
                alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette Prescription?");
                alert.setContentText("Cette action ne peut pas être annulée.");

                alert.showAndWait().ifPresent(result -> {
                    if (result == ButtonType.OK) {
                        try {
                            // Delete from database
                            prescriptionService.deleteOne(prescription);

                            // Refresh the list
                            prescriptionListView.setItems(getPrescriptionWithHeader());

                            showAlert("Succès", "La prescription a été supprimée avec succès");
                        } catch (SQLException e) {
                            showAlert("Erreur", "Échec de la suppression: " + e.getMessage());
                        }
                    }
                });
            }

            private void handleShowPrescription(Prescription prescription) {/*
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddConsultationForm.fxml"));
                    Parent root = loader.load();

                    // Get controller and set the rendez-vous
                    AddConsultationController controller = loader.getController();
                    controller.initData(prescription);

                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Créer une Consultation");
                    stage.setScene(new Scene(root));
                    stage.showAndWait();

                    // Refresh the list after creating consultation
                    rendezVousListView.setItems(getPrescriptionWithHeader());

                } catch (IOException | SQLException e) {
                    showAlert("Error", "Failed to open consultation creation form: " + e.getMessage());
                }*/
            }

            private void openPrescriptionsDetailsPopup(Prescription prescription) {


                // Create and show the popup using the FXML controller
                ShowPrescriptionController.showPrescriptionDetails(prescription);
            }


            // Helper method to create labels with dynamic width and text wrapping
            private Label createDynamicWidthLabel(String text, boolean isHeader) {
                Label label = new Label(text);

                // Configure label properties for wrapping and dynamic width
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setPrefWidth(0);

                // Bind label width to a fraction of the ListView width
                label.prefWidthProperty().bind(Bindings.divide(prescriptionListView.widthProperty(), 4));

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
                button.prefWidthProperty().bind(Bindings.divide(prescriptionListView.widthProperty(), 12));

                return button;
            }
        });

        prescriptionListView.setStyle(
                "-fx-border-color: white; " +
                        "-fx-border-width: 0px; " +
                        "-fx-border-radius: 0px;"
        );

        try {
            prescriptionListView.setItems(getPrescriptionWithHeader());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            showAlert("Erreur", "Les données du rendez-vous ne peuvent pas être charger: " + e.getMessage());
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