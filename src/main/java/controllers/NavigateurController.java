package controllers;

import entities.Prescription;
import entities.RendezVous;
import entities.Roles;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.PrescriptionService;
import services.RendezVousService;
import utils.BadWords;
import utils.SUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class NavigateurController {

    public final Set<String> badWords = new HashSet<>(Arrays.asList(
            // Mild insults
            "idiot", "stupid", "dumb", "moron", "loser", "fool", "clown", "jerk",
            "weirdo", "creep", "lame", "brat", "trash", "pathetic", "sucker", "dope",

            // Negative adjectives
            "ugly", "gross", "disgusting", "worthless", "useless", "annoying", "lazy",
            "boring", "nonsense", "ridiculous", "terrible", "horrible", "awful", "toxic",

            // Dismissive or aggressive phrases
            "shut up", "get lost", "go away", "leave me alone", "screw off", "no one cares",
            "who asked", "what a joke", "you wish", "you're nothing", "no one likes you",

            // Condescending slang
            "cringe", "simp", "snowflake", "boomer", "karen", "neckbeard", "tryhard",
            "basic", "poser", "beta", "wannabe", "crybaby", "manchild",

            // Light online trolling words
            "rekt", "owned", "pwned", "git gud", "ez", "trash talk", "camping", "ragequit",
            "feed", "noob", "scrub", "griefer", "toxic player", "sweaty", "bot",

            // Tone-based or manipulative
            "fake", "liar", "cheater", "backstabber", "two-faced", "jealous", "clingy",
            "attention seeker", "overreacting", "overdramatic", "control freak", "delusional",
            "manipulative", "selfish", "vain", "insecure", "obsessed", "immature",

            // Emotionally aggressive or sarcastic
            "hate you", "i wish you would disappear", "you're a problem", "you're hopeless",
            "nobody wants you", "you ruin everything", "why are you like this", "what's wrong with you"
    ));
    @FXML
    public ListView<RendezVous> rendezVousListView;

    private final RendezVousService rendezVousService = new RendezVousService();
    private final PrescriptionService prescriptionService = new PrescriptionService();


    @FXML
    public void handleListAvis(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListAvis.fxml"));
            Parent root = loader.load();

            Stage stage;
            if (event.getSource() instanceof MenuItem) {
                stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            } else {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


    @FXML
    public void handleNewAvis(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreateAvis.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }


    @FXML
    public void handleListConsultation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListConsultationView.fxml"));
            Parent root = loader.load();

            Stage stage;
            if (event.getSource() instanceof MenuItem) {
                stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            } else {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @FXML
    public void handleNewRendezVous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddRendezVousView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nouveau Rendez-vous");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh list after adding
            try {
                rendezVousListView.setItems(getRendezVousWithHeader());
            } catch (SQLException e) {
                showAlert("Erreur", "Les données du rendez-vous ne peuvent pas être charger: " + e.getMessage());
            }
        } catch (IOException e) {
            showAlert("Erreur", "Échec de l'ouverture du formulaire de nouveau rendez-vous: " + e.getMessage());
        }
    }


    public ObservableList<Prescription> getPrescriptionWithHeader() throws SQLException {
        ObservableList<Prescription> prescriptionList = FXCollections.observableArrayList();

        // Add a header row using a placeholder object
        Prescription header = new Prescription();
        header.setId(-1); // Use -1 to detect header row
        prescriptionList.add(header);

        List<Prescription> prescriptionsItems;

        // Filter appointments based on user role
        if (SUser.getUser().getRole().equals(Roles.professionnel)) {
            // If professional, only show appointments for this professional
            prescriptionsItems = prescriptionService.selectAll().stream()
                    .filter(prescription -> prescription.getConsultation().getProfessionnel().getId() == SUser.getUser().getId())
                    .toList();
        } else {
            // If patient, show all appointments for this patient
            prescriptionsItems = prescriptionService.selectAll().stream()
                    .filter(prescription -> prescription.getConsultation().getUser().getId() == SUser.getUser().getId())
                    .toList();
        }

        prescriptionList.addAll(prescriptionsItems);
        return prescriptionList;
    }

    public ObservableList<RendezVous> getRendezVousWithHeader() throws SQLException {
        ObservableList<RendezVous> rendezVousList = FXCollections.observableArrayList();

        // Add a header row using a placeholder object
        RendezVous header = new RendezVous();
        header.setId(-1); // Use -1 to detect header row
        rendezVousList.add(header);

        List<RendezVous> rendezVousItems;

        // Filter appointments based on user role
        if (SUser.getUser().getRole().equals(Roles.professionnel)) {
            // If professional, only show appointments for this professional
            rendezVousItems = rendezVousService.selectAll().stream()
                    .filter(rdv -> rdv.getProfessional().getId() == SUser.getUser().getId())
                    .toList();
        } else {
            // If patient, show all appointments for this patient
            rendezVousItems = rendezVousService.selectAll().stream()
                    .filter(rdv -> rdv.getUser().getId() == SUser.getUser().getId())
                    .toList();
        }

        rendezVousList.addAll(rendezVousItems);
        return rendezVousList;
    }
    @FXML
    public void handleListPrescription(ActionEvent event) {
        // Already on this page, maybe refresh the list
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListPrescriptions.fxml"));
            Parent root = loader.load();

            Stage stage;
            if (event.getSource() instanceof MenuItem) {
                stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            } else {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    @FXML
    public void handleListRendezVous(ActionEvent event) {
        // Already on this page, maybe refresh the list
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRdv.fxml"));
            Parent root = loader.load();

            Stage stage;
            if (event.getSource() instanceof MenuItem) {
                stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            } else {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
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


    public boolean containsBadWords(String text) {
        String textLower = text.toLowerCase();

        for (String badWord : badWords) {
            String pattern = "(?i).*\\b" + Pattern.quote(badWord.toLowerCase()) + "\\b.*";
            if (textLower.matches(pattern) || textLower.contains(badWord)) {
                return true;
            }
        }
        for (String badWord : BadWords.badWords) {
            String pattern = "(?i).*\\b" + Pattern.quote(badWord.toLowerCase()) + "\\b.*";
            if (textLower.matches(pattern) || textLower.contains(badWord)) {
                return true;
            }
        }

        return false;
    }

}

