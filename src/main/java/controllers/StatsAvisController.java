package controllers;

import entities.Avis;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import services.gestionAvis.AvisService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.SUser;

import java.sql.SQLException;
import java.util.List;

public class StatsAvisController {

    @FXML
    private Label averageRatingLabel;

    @FXML
    private Label totalRatingsLabel;

    @FXML
    private VBox starsBox;

    @FXML
    private HBox starIconsBox;

    private AvisService avisService = new AvisService(); // Your service class

    @FXML
    public void initialize() {
        loadStats();
    }

    int currentProfessionalId = SUser.getUser().getId() ;
    private void loadStats() {
        try {
            System.out.println(currentProfessionalId);
            System.out.println("-*******5626544554354525");
            List<Avis> avisList = avisService.recupererPourProfessional(currentProfessionalId);

            int[] ratingCounts = new int[5]; // Index 0 = 1★, ..., Index 4 = 5★
            int totalRatings = avisList.size();
            int sumRatings = 0;

            for (Avis a : avisList) {
                int note = a.getNote(); // expected to be from 1 to 5
                if (note >= 1 && note <= 5) {
                    ratingCounts[note - 1]++;
                    sumRatings += note;
                }
            }

            double average = totalRatings > 0 ? (double) sumRatings / totalRatings : 0.0;

            averageRatingLabel.setText(String.format("%.1f", average));
            totalRatingsLabel.setText(totalRatings + " ratings");

            starsBox.getChildren().clear(); // in case it's reloaded multiple times

            for (int i = 5; i >= 1; i--) {
                HBox starRow = new HBox(10);
                starRow.setStyle("-fx-alignment: center-left;");

                Label starLabel = new Label(i + "");
                starLabel.setPrefWidth(20);

                ProgressBar bar = new ProgressBar();
                bar.setProgress(totalRatings > 0 ? (double) ratingCounts[i - 1] / totalRatings : 0);
                bar.setPrefWidth(300);
                bar.setStyle("-fx-accent: #4285F4;"); // Google Play blue color

                starRow.getChildren().addAll(starLabel, bar);
                starsBox.getChildren().add(starRow);
            }

            // Update star icons based on average
            starIconsBox.getChildren().clear();

            // Extract the whole number part of the average
            int fullStars = (int) average;

            // Determine if there should be a half star
            boolean hasHalfStar = (average - fullStars) >= 0.5;

            // Now create the star display
            for (int i = 1; i <= 5; i++) {
                if (i <= fullStars) {
                    Label star = new Label("★"); // full star
                    star.setStyle("-fx-font-size: 24px; -fx-text-fill: gold;");
                    starIconsBox.getChildren().add(star);
                } else if (i == fullStars + 1 && hasHalfStar) {
                    // Create a half-star using a clipped full star
                    StackPane halfStarContainer = new StackPane();

                    Label emptyStar = new Label("☆");
                    emptyStar.setStyle("-fx-font-size: 24px; -fx-text-fill: #d3d3d3;");

                    Label fullStar = new Label("★");
                    fullStar.setStyle("-fx-font-size: 24px; -fx-text-fill: gold;");

                    // Create a clip rectangle that only shows half of the star
                    Rectangle clip = new Rectangle();
                    clip.setWidth(12); // Half the width of the star
                    clip.setHeight(24);
                    fullStar.setClip(clip);

                    halfStarContainer.getChildren().addAll(emptyStar, fullStar);
                    starIconsBox.getChildren().add(halfStarContainer);
                } else {
                    Label star = new Label("☆"); // empty star
                    star.setStyle("-fx-font-size: 24px; -fx-text-fill: #d3d3d3;");
                    starIconsBox.getChildren().add(star);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            averageRatingLabel.setText("Error");
            totalRatingsLabel.setText("Failed to load data");
        }
    }
}