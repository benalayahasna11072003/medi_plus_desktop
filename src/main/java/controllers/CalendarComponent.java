package controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.MonthView;
import entities.Consultation;
import entities.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import services.gestionConsultation.ConsultationService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarComponent extends BorderPane {

    private final MonthView monthView;
    private final Calendar professionalCalendar;
    private final CalendarSource calendarSource;
    private User professional;
    private final ConsultationService consultationService;
    private LocalDate selectedDate;
    private final List<LocalDate> unavailableDates;

    public CalendarComponent() {

        // In CalendarComponent constructor
        this.getStylesheets().add(getClass().getResource("/gestionConcultation/calendar-styles.css").toExternalForm());
        this.consultationService = new ConsultationService();
        this.selectedDate = LocalDate.now();
        this.unavailableDates = new ArrayList<>();

        // Create a month view
        monthView = new MonthView();
        monthView.setShowToday(true);
        monthView.setShowWeekNumbers(true);

        // Disable toolbar buttons (day, week, month, year)
       // monthView.setShowToolBar(false);

        // Set sizing
        monthView.setPrefHeight(500);
        monthView.setPrefWidth(800);

        // Create header
        Label headerLabel = new Label("Select Consultation Date");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        headerLabel.setAlignment(Pos.CENTER);

        VBox topContainer = new VBox(10);
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setPadding(new Insets(10));
        topContainer.getChildren().add(headerLabel);


        // Create calendar for professional
        professionalCalendar = new Calendar("Consultations");
        professionalCalendar.setStyle(Calendar.Style.STYLE1);
        // Set up calendar source
        calendarSource = new CalendarSource("Consultation Calendar");

        calendarSource.getCalendars().add(professionalCalendar);

        // Add calendar to view
        monthView.getCalendarSources().add(calendarSource);

        // Add date selection listener
        monthView.setOnMouseClicked(event -> {
            selectedDate = monthView.getDate();
            System.out.println("Date selected: " + selectedDate); // For debugging
        });

        setTop(topContainer);
        setCenter(monthView);

        // Apply CSS for custom styling
        getStyleClass().add("calendar-component");

    }

    public void setProfessional(User professional) {
        this.professional = professional;
        if (professional != null) {
            professionalCalendar.setName(professional.getNameUser() + "'s Calendar");
            loadProfessionalConsultations();
        }
    }

    public void loadProfessionalConsultations() {
        if (professional == null) {
            return;
        }

        // Clear existing entries and unavailable dates
        professionalCalendar.clear();
        unavailableDates.clear();

        try {
            // Get all consultations for the professional
            List<Consultation> consultations = consultationService.findByProfessional(professional.getId());

            for (Consultation consultation : consultations) {
                LocalDate date = consultation.getDateConsultation();
                unavailableDates.add(date);
                System.out.println(unavailableDates);

                // Create entry for the consultation
                Entry<String> entry = new Entry<>(consultation.getReason());

                // Set full-day entry for simplicity since we're only tracking dates
                entry.setFullDay(true);
                entry.changeStartDate(date);
                entry.changeEndDate(date);

                entry.setLocation("Consultation #" + consultation.getId());
                professionalCalendar.addEntry(entry);
            }
        } catch (SQLException e) {
            System.err.println("Error loading consultations: " + e.getMessage());
        }
    }

    public boolean isProfessionalAvailable(LocalDate date) {
        return !unavailableDates.contains(date);
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setDate(LocalDate date) {
        monthView.setDate(date);
        selectedDate = date;
    }
}