package entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Consultation {

    private int id;
    private LocalDate dateConsultation;
    //one to many
    private User user;
    //one to many
    private User professionnel;
    //one to one
    private RendezVous rendezVous;
    //many to one
    private List<Prescription> prescriptions = new ArrayList<>();
    private String reason;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(LocalDate dateConsultation) {
        this.dateConsultation = dateConsultation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getProfessionnel() {
        return professionnel;
    }

    public void setProfessionnel(User professionnel) {
        this.professionnel = professionnel;
    }

    public RendezVous getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(RendezVous rendezVous) {
        this.rendezVous = rendezVous;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void addPrescription(Prescription prescription) {
        if (!prescriptions.contains(prescription)) {
            prescriptions.add(prescription);
            prescription.setConsultation(this);
        }
    }

    public void removePrescription(Prescription prescription) {
        if (prescriptions.remove(prescription)) {
            if (prescription.getConsultation() == this) {
                prescription.setConsultation(null);
            }
        }
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", dateConsultation=" + dateConsultation +
                ", user=" + user +
                ", professionnel=" + professionnel +
                ", rendezVous=" + rendezVous +
                ", prescriptions=" + prescriptions +
                ", reason='" + reason + '\'' +
                '}';
    }
}
