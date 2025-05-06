package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.Consultation;
import entities.Roles;
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
import services.gestionConsultation.ConsultationService;
import utils.SUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListConsultationController extends NavigateurController {

    private final ConsultationService consultationService = new ConsultationService();

    @FXML
    private ListView<Consultation> consultationsListView;

    @FXML
    public void initialize() {
        consultationsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Consultation consultation, boolean empty) {
                super.updateItem(consultation, empty);

                if (empty || consultation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox rowBox = new HBox(10);
                    rowBox.setPadding(new Insets(5));
                    rowBox.prefWidthProperty().bind(consultationsListView.widthProperty().subtract(30));

                    // Style for alternating row colors
                    if (getIndex() % 2 == 0) {
                        rowBox.setStyle("-fx-background-color: #f0f0f0;");
                    } else {
                        rowBox.setStyle("-fx-background-color: #ffffff;");
                    }

                    // Header row
                    if (consultation.getId() == -1) {
                        // Header styling
                        rowBox.setStyle("-fx-background-color: #212529;");

                        // Create header labels
                        String[] headers = {"Date", "Patient", "Raison", "Actions"};
                        for (String header : headers) {
                            Label headerLabel = createDynamicWidthLabel(header, true);
                            headerLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                            rowBox.getChildren().add(headerLabel);
                        }
                    } else {
                        // Data row
                        Label dateLabel = createDynamicWidthLabel(consultation.getDateConsultation().toString(), false);
                        Label patientLabel = createDynamicWidthLabel(consultation.getUser().getNameUser(), false);
                        Label reasonLabel = createDynamicWidthLabel(consultation.getReason(), false);

                        // Create action buttons
                        HBox actionButtons = new HBox(5);
                        // Add Prescription button
                        Button prescriptionBtn = createActionButton(new FontAwesomeIconView(FontAwesomeIcon.FILE_TEXT_ALT));
                        prescriptionBtn.setOnAction(event -> {
                            try {
                                openAddPrescriptionPopup(consultation);
                            } catch (SQLException e) {
                                showAlert("Erreur", "Failed to load review details: " + e.getMessage());
                            }
                        });
                        // Add Prescription button
                        Button showBtn = createActionButton(new FontAwesomeIconView(FontAwesomeIcon.EYE));
                        showBtn.setOnAction(event -> {
                            try {
                                openConsultationDetailsPopup(consultation);
                            } catch (SQLException e) {
                                showAlert("Erreur", "Failed to load review details: " + e.getMessage());
                            }
                        });

                        // Edit button
                        Button editBtn = createActionButton(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
                        editBtn.setOnAction(event -> handleEditConsultation(consultation));

                        // Update button
                        Button deleteBtn = createActionButton(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
                        deleteBtn.setOnAction(event -> handleDeleteConsultation(consultation));
                        if (SUser.getUser().getRole().equals(Roles.professionnel)) {
                            actionButtons.getChildren().addAll(showBtn, prescriptionBtn);
                        } else {
                            actionButtons.getChildren().addAll(showBtn, editBtn, deleteBtn);
                        }
                        // Add to row
                        rowBox.getChildren().addAll(
                                dateLabel,
                                patientLabel,
                                reasonLabel,
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
                label.prefWidthProperty().bind(Bindings.divide(consultationsListView.widthProperty(), 4));

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
                button.prefWidthProperty().bind(Bindings.divide(consultationsListView.widthProperty(), 12));

                return button;
            }
        });

        consultationsListView.setStyle(
                "-fx-border-color: white; " +
                        "-fx-border-width: 0px; " +
                        "-fx-border-radius: 0px;"
        );

        try {
            consultationsListView.setItems(getConsultationsWithHeader());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            showAlert("Erreur", "Failed to load consultations: " + e.getMessage());
        }
    }

    private ObservableList<Consultation> getConsultationsWithHeader() throws SQLException {
        ObservableList<Consultation> consultations = FXCollections.observableArrayList();

        // Add a header row using a placeholder object
        Consultation header = new Consultation();
        header.setId(-1); // Use -1 to detect header row
        consultations.add(header);

        // Add actual consultations
        List<Consultation> consultationList = consultationService.selectAll();
        if (SUser.getUser().getRole().equals(Roles.patient)) {
            consultations.addAll(consultationList.stream().filter(consultation -> consultation.getUser().getId() == SUser.getUser().getId()).toList());
        } else {
            consultations.addAll(consultationList.stream().filter(consultation -> consultation.getProfessionnel().getId() == SUser.getUser().getId()).toList());
        }
        return consultations;
    }

    private void openConsultationDetailsPopup(Consultation consultation) throws SQLException {


        // Create and show the popup using the FXML controller
        ConsultationDetailsController.showConsultationDetails(consultation);
    }
    private void openAddPrescriptionPopup(Consultation consultation) throws SQLException {
        // Create and show the popup using the FXML controller
        Runnable refreshCallback = () -> {
            try {
                // Refresh the list after edit
                consultationsListView.setItems(getConsultationsWithHeader());
            } catch (SQLException e) {
                showAlert("Erreur", "Échec du rafraîchissement des consultations.: " + e.getMessage());
            }
        };

        AddPrescriptionController.showConsultationDetails(consultation, refreshCallback);
    }

    private void handleAddPrescription(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddPrescriptionPopup.fxml"));
            Parent root = loader.load();

            // Get controller and set the consultation
            AddPrescriptionController controller = loader.getController();
            //controller.setConsultation(consultation);!!!!!!!!!!!!!!!!!!!!!!!!!!!

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter Prescription");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Erreur", "Échec du chargement du formulaire de prescription: " + e.getMessage());
        }
    }

    private void handleEditConsultation(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestionConcultation/EditConsultationForm.fxml"));
            Parent root = loader.load();

            // Get controller and set the consultation
            EditConsultationController controller = loader.getController();
            controller.setData(consultation, () -> {
                try {
                    // Refresh the list after edit
                    consultationsListView.setItems(getConsultationsWithHeader());
                } catch (SQLException e) {
                    showAlert("Erreur", "Échec du rafraîchissement des consultations: " + e.getMessage());
                }
            });
            /*controller.setRefreshCallback(() -> {
                try {
                    // Refresh the list after edit
                    consultationsListView.setItems(getConsultationsWithHeader());
                } catch (SQLException e) {
                    showAlert("Erreur", "Failed to refresh consultations: " + e.getMessage());
                }
            });*/

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier Consultation");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Erreur", "Échec du chargement du formulaire de modification: " + e.getMessage());
        }
    }

    private void handleUpdateConsultation(Consultation consultation) {
        try {
            // Refresh the consultation data from database
            Consultation updatedConsultation = consultationService.findById(consultation.getId());

            // Update the list
            int index = consultationsListView.getItems().indexOf(consultation);
            if (index >= 0) {
                consultationsListView.getItems().set(index, updatedConsultation);
                consultationsListView.refresh();
            }

            showAlert("Succès", "Consultation mise à jour avec succès.");
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de la mise à jour de la consultation: " + e.getMessage());
        }
    }

    // Handle delete response action
// Handle delete consultation action
    private void handleDeleteConsultation(Consultation consultation) {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer Consultation");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette consultation?");
        alert.setContentText("Cette action ne peut pas être annulée.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    // Delete from database
                    consultationService.deleteOne(consultation);

                    // Refresh the list
                    consultationsListView.setItems(getConsultationsWithHeader());

                    showAlert("Succès", "La consultation a été supprimée avec succès");
                } catch (SQLException e) {
                    showAlert("Erreur", "Échec de la suppression: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleListConsultation() {
        // Already on the list consultation view, do nothing or refresh
        try {
            consultationsListView.setItems(getConsultationsWithHeader());
        } catch (SQLException e) {
            showAlert("Erreur", "Échec du rafraîchissement des consultations: " + e.getMessage());
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