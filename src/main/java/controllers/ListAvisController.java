package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.Avis;
import entities.Reponse;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.AvisService;
import services.ReponseService;
import utils.SUser;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ListAvisController extends NavigateurController {


    private final ReponseService reponseService = new ReponseService();
    private final AvisService avisService = new AvisService();

    @FXML
    private ListView<Avis> reviewsListView;

    @FXML
    public void initialize() {
        reviewsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Avis avis, boolean empty) {
                super.updateItem(avis, empty);

                if (empty || avis == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox rowBox = new HBox(10);
                    rowBox.setPadding(new Insets(5));
                    rowBox.prefWidthProperty().bind(reviewsListView.widthProperty().subtract(30));

                    // Style for alternating row colors
                    if (getIndex() % 2 == 0) {
                        rowBox.setStyle("-fx-background-color: #f0f0f0;");
                    } else {
                        rowBox.setStyle("-fx-background-color: #ffffff;");
                    }

                    // Header row
                    if (avis.getRef() == -1) {
                        // Header styling
                        rowBox.setStyle("-fx-background-color: #212529;");

                        // Create header labels
                        String[] headers = {"Patient", "Professional", "Note", "Date", "Comment", "Actions"};
                        for (String header : headers) {
                            Label headerLabel = createDynamicWidthLabel(header, true);
                            headerLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                            rowBox.getChildren().add(headerLabel);
                        }
                    } else {
                        // Data row
                        Label patientLabel = createDynamicWidthLabel(avis.getUser().getNameUser(), false);
                        Label professionalLabel = createDynamicWidthLabel(avis.getProfessional().getNameUser(), false);
                        Label noteLabel = createDynamicWidthLabel(String.valueOf(avis.getNote()), false);
                        Label dateLabel = createDynamicWidthLabel(avis.getDateAvis().toString(), false);
                        Label commentLabel = createDynamicWidthLabel(avis.getCommentaire(), false);

                        // Create action buttons with dynamic sizing
                        HBox actionButtons = new HBox(5);
                        Button actionBtn1 = createActionButton(new FontAwesomeIconView(FontAwesomeIcon.EYE));
                        Button actionBtn2;
                        if (SUser.getUser().getId() != avis.getUser().getId()) {
                            actionBtn2 = createActionButton(new FontAwesomeIconView(FontAwesomeIcon.REPLY));
                            actionBtn2.setOnAction(event -> {
                                try {
                                    openReplyPopup(avis);
                                } catch (SQLException e) {
                                    showAlert("Error", "Failed to load review details: " + e.getMessage());
                                }
                            });
                        } else {
                            actionBtn2 = createActionButton(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
                            //actionBtn2.setStyle("-fx-background-color: #1A76D1;");
                            actionBtn2.setOnAction(event -> {
                                // try {
                                handleUpdateAvis(avis);
                           /*     } catch (SQLException e) {
                                    showAlert("Error", "Failed to load review details: " + e.getMessage());
                                }*/
                            });
                        }

                        actionBtn1.setOnAction(event -> {
                            try {
                                openAvisDetailsPopup(avis);
                            } catch (SQLException e) {
                                showAlert("Error", "Failed to load review details: " + e.getMessage());
                            }
                        });


                        actionButtons.getChildren().addAll(actionBtn1, actionBtn2);

                        // Add to row
                        rowBox.getChildren().addAll(
                                patientLabel,
                                professionalLabel,
                                noteLabel,
                                dateLabel,
                                commentLabel,
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
                label.prefWidthProperty().bind(Bindings.divide(reviewsListView.widthProperty(), 6));

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
                button.prefWidthProperty().bind(Bindings.divide(reviewsListView.widthProperty(), 12));

                return button;
            }
        });

        reviewsListView.setStyle(
                "-fx-border-color: white; " +
                        "-fx-border-width: 0px; " +
                        "-fx-border-radius: 0px;"
        );

        try {
            reviewsListView.setItems(getSampleReviewsWithHeader());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void handleUpdateAvis(Avis avis) {
        UpdateAvisController.showUpdateAvisDialog(avis, () -> {
            // Refresh the avis data
            AvisService avisService = new AvisService();
            try {
                // Reload the avis object
                // Avis updatedAvis = avisService.findByRef(avis.getRef());
                //this.avis = updatedAvis;
                List<Avis> aviss = avisService.selectAll();
                reviewsListView.getItems().setAll(aviss);
                // Reload the responses
                // ReponseService reponseService = new ReponseService();
                //this.responses = reponseService.readByAvis(avis.getId());

                // Update the UI with new data
                //populateData();
            } catch (SQLException e) {
                showAlert("Erreur SQL", e.getMessage());
            }
        });
    }

    private void openReplyPopup(Avis avis) throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddResponsePopup.fxml"));
            Parent root = loader.load();

            // Get controller and set the review
            AddResponseController controller = loader.getController();
            controller.setAvis(avis);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Reply to Comment");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Failed to load reply popup: " + e.getMessage());
        }
    }

    private ObservableList<Avis> getSampleReviewsWithHeader() throws SQLException {
        ObservableList<Avis> aviss = FXCollections.observableArrayList();

        // Add a header row using a placeholder object
        Avis header = new Avis();
        header.setRef(-1); // Use -1 to detect header row
        aviss.add(header);

        aviss.addAll(avisService.selectAll());
        return aviss;
    }

    @FXML
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void openAvisDetailsPopup(Avis avis) throws SQLException {
        // Get responses for this review from the service
        List<Reponse> responses = reponseService.getResponsesByAvisId(avis.getRef());

        // If the service is not implemented yet, use some sample data
       /* if (responses == null || responses.isEmpty()) {
            responses = getSampleResponses(avis);
        }*/

        // Create and show the popup using the FXML controller
        AvisDetailsController.showAvisDetails(avis, responses);
    }
    /*
    private List<Reponse> getSampleResponses(Avis avis) {
        List<Reponse> responses = new ArrayList<>();

        // Create a sample user for the responder
        User responder = new User();
        responder.setNameUser("test2");

        // Create sample responses
        Reponse response1 = new Reponse();
        response1.setId(1);
        response1.setReponse("that's true");
        response1.setMadeBy(responder);
        response1.setDateReponse(LocalDate.parse("2025-03-26"));
        response1.setAvis(avis);

        Reponse response2 = new Reponse();
        response2.setId(2);
        response2.setContent("sec reponse");
        response2.setResponder(responder);
        response2.setDate(LocalDate.parse("2025-03-26"));
        response2.setAvis(avis);

        responses.add(response1);
        responses.add(response2);

        return responses;
    }*/
}