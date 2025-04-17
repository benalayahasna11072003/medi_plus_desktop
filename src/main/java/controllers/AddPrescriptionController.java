package controllers;

import entities.Consultation;
import entities.Prescription;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import services.PrescriptionService;
import services.UserService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AddPrescriptionController implements Initializable {

    private Consultation consultation;
    private Prescription prescription;

    @FXML
    private Button enregistrerButton;

    @FXML
    private TextField prescField;

    private final PrescriptionService prescriptionService = new PrescriptionService();
    private Runnable onUpdateSuccessCallback;






///////////

    // List of prescription items (modify as needed)
    private final List<String> prescriptionItems = Arrays.asList(
            "Amoxicillin", "Azithromycin", "Atorvastatin", "Aspirine",
            "Bupropion", "Benazepril", "Budesonide",
            "Ciprofloxacin", "Cephalexin", "Citalopram",
            "Doliprane", "Dafalgan", "Dolirhume",
            "Efferalgan", "Eludril", "Esomeprazole",
            "Forlax", "Fluconazole", "Furosemide",
            "Gaviscon", "Glucophage", "Glucotrol"
    );

    // Popup for autocomplete suggestions
    private Popup autoCompletePopup;
    private ListView<String> suggestionList;


    // Track the word currently being typed and its position
    private int currentWordStart = 0;
    private int currentWordEnd = 0;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupAutoComplete();
    }

    private void setupAutoComplete() {

        // Create popup and list view for suggestions
        autoCompletePopup = new Popup();
        suggestionList = new ListView<>();
        suggestionList.setPrefWidth(300);
        suggestionList.setPrefHeight(200);
        autoCompletePopup.getContent().add(suggestionList);

        // Set up filtering logic with delayed initialization
        prescField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                autoCompletePopup.hide();
                return;
            }
            try {
                // Find the current word being typed
                String text = prescField.getText();
                int caretPosition = prescField.getCaretPosition();

                // Find the start of the current word (from caret position backward)
                currentWordStart = caretPosition;
                while (currentWordStart > 0 && !Character.isWhitespace(text.charAt(currentWordStart - 1))) {
                    currentWordStart--;
                }

                // Find the end of the current word (from caret position forward)
                currentWordEnd = caretPosition;
                while (currentWordEnd < text.length() && !Character.isWhitespace(text.charAt(currentWordEnd))) {
                    currentWordEnd++;
                }

                // Extract the current word
                String currentWord = "";
                if (currentWordStart < currentWordEnd) {
                    currentWord = text.substring(currentWordStart, currentWordEnd);
                }

                // Only show suggestions if current word is not empty
                if (!currentWord.isEmpty()) {
                    // Filter items based on current word
                    String lowerCaseInput = currentWord.toLowerCase();
                    List<String> filteredItems = prescriptionItems.stream()
                            .filter(item -> item.toLowerCase().contains(lowerCaseInput))
                            .collect(Collectors.toList());

                    // Update and show popup if we have matches
                    if (!filteredItems.isEmpty()) {
                        suggestionList.setItems(FXCollections.observableArrayList(filteredItems));

                        if (!autoCompletePopup.isShowing() && prescField.getScene() != null) {
                            // Only show popup if field is in scene
                            try {
                                // Use scene coordinates instead of screen coordinates
                                double x = prescField.localToScene(0, 0).getX();
                                double y = prescField.localToScene(0, 0).getY() + prescField.getHeight();

                                // Convert to screen coordinates if field is already in scene
                                if (prescField.getScene().getWindow() != null) {
                                    double screenX = prescField.getScene().getWindow().getX() + x;
                                    double screenY = prescField.getScene().getWindow().getY() + y;
                                    autoCompletePopup.show(prescField.getScene().getWindow(), screenX, screenY);
                                }
                            } catch (Exception e) {
                                // If there's any issue with positioning, don't show the popup yet
                            }
                        }
                    } else {
                        autoCompletePopup.hide();
                    }
                } else {
                    autoCompletePopup.hide();
                }
            }catch (Exception e){
                System.err.println("Error in autocomplete: " + e.getMessage());
                autoCompletePopup.hide();
            }
        });

        // Handle selection from the suggestion list
        suggestionList.setOnMouseClicked(event -> {
            String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                insertSelectedItem(selectedItem);
            }
        });

        // Handle keyboard navigation
        prescField.setOnKeyPressed(event -> {
            if (autoCompletePopup.isShowing()) {
                if (event.getCode() == KeyCode.DOWN) {
                    suggestionList.requestFocus();
                    suggestionList.getSelectionModel().selectFirst();
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    autoCompletePopup.hide();
                }
            }
        });

        suggestionList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    insertSelectedItem(selectedItem);
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                autoCompletePopup.hide();
                prescField.requestFocus();
            }
        });

        // Hide popup when field loses focus
        prescField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !suggestionList.isFocused()) {
                autoCompletePopup.hide();
            }
        });

        // Add a listener for when the field is added to scene
        prescField.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // Scene is now available, we can safely use it for positioning
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    // Window is now available, autocomplete will work properly
                });
            }
        });
    }
    /**
     * Insert the selected item into the text field, replacing only the current word
     */
    private void insertSelectedItem(String selectedItem) {
        String currentText = prescField.getText();

        // Build the new text with the selected item replacing the current word
        StringBuilder newText = new StringBuilder(currentText);
        newText.replace(currentWordStart, currentWordEnd, selectedItem);

        // Update text field with the new text
        prescField.setText(newText.toString());

        // Calculate new caret position after the inserted word plus a space
        int newPosition = currentWordStart + selectedItem.length();
        if (newPosition == prescField.getText().length() || prescField.getText().charAt(newPosition) != ' ') {
            // Add a space after the word if we're at the end or the next char isn't already a space
            prescField.setText(prescField.getText().substring(0, newPosition) + " "
                    + (newPosition < prescField.getText().length() ? prescField.getText().substring(newPosition) : ""));
            newPosition++; // Move past the space
        }

        // Set the caret position after the inserted word and space
        prescField.requestFocus();
        prescField.positionCaret(newPosition);

        autoCompletePopup.hide();
    }


////////////










    @FXML
    void handleListConsultation(ActionEvent event) {

    }

    @FXML
    void insertPrescription(ActionEvent event) {

        try {


            // Validate input fields
            if (prescField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez saisir une description pour la prescription");
                return;
            }else if (prescField.getText().length()<4){
                showAlert("Erreur", "La description doit contenir au moins 4 caractères.");
                return;
            }
            if (prescription != null) {
                // Save to database
                prescription.setDescription(prescField.getText());
                prescriptionService.updateOne(prescription);
            } else {
                if (!prescField.getText().isEmpty()) {
                    Prescription prescription1 = new Prescription();
                    prescription1.setConsultation(consultation);
                    prescription1.setDescription(prescField.getText());
                    prescription1.setCreatedAt(LocalDate.now());

                    prescriptionService.insertOne(prescription1);
                }
            }
            // Show success message
            showAlert("Succès", "La prescription a été mise à entregistré avec succès");

            // Execute the callback to refresh the parent view
            if (onUpdateSuccessCallback != null) {
                onUpdateSuccessCallback.run();
            }

            // Close the window
            Stage stage = (Stage) enregistrerButton.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de la mise à jour: " + e.getMessage());
        }

    }

    public static void showConsultationDetails(Consultation consultation, Runnable onUpdateSuccessCallback) throws SQLException{
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(AddPrescriptionController.class.getResource("/AddPrescription.fxml"));
            BorderPane root = loader.load();

            // Get controller
            AddPrescriptionController controller = loader.getController();
            controller.setData(consultation, onUpdateSuccessCallback);

            // Create stage
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Prescription Details");
            popup.setResizable(false);

            // Set scene
            Scene scene = new Scene(root);
            popup.setScene(scene);

            // Show popup
            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Échec du chargement des détails de l'avis: " + e.getMessage());
            alert.showAndWait();
        }

    }

    private void setData(Consultation consultation, Runnable onUpdateSuccessCallback) throws SQLException{

        this.onUpdateSuccessCallback = onUpdateSuccessCallback;
        this.consultation = consultation;
        Prescription prescription1 =prescriptionService.getPrescriptionsByConsultationId(consultation.getId()).stream().findFirst().orElse(null);
        if (prescription1!=null) {
            prescription = prescription1;
            prescField.setText(prescription.getDescription());
        }


    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }



}
