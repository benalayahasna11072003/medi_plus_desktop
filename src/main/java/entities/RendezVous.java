package entities;

import java.time.LocalDate;

public class RendezVous {

    private int id;
    private LocalDate dateRdv;
    private String statusRdv;
    private User user;
    private User professional;
    private Consultation consultation;

    public RendezVous() {
        this.statusRdv = "en attente";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateRdv() {
        return dateRdv;
    }

    public void setDateRdv(LocalDate dateRdv) {
        this.dateRdv = dateRdv;
    }

    public String getStatusRdv() {
        return statusRdv;
    }

    public void setStatusRdv(String statusRdv) {
        this.statusRdv = statusRdv;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getProfessional() {
        return professional;
    }

    public void setProfessional(User professional) {
        this.professional = professional;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    // Methods to change status
    public void acceptRdv() {
        this.statusRdv = "accepté";
    }

    public void rejectRdv() {
        this.statusRdv = "refusé";
    }

    @Override
    public String toString() {
        return "RendezVous{" +
                "id=" + id +
                ", dateRdv=" + dateRdv +
                ", statusRdv='" + statusRdv + '\'' +
                ", user=" + user +
                ", professional=" + professional +
                ", consultation=" + consultation +
                '}';
    }
}
