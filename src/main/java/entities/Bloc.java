package entities;

import java.util.ArrayList;
import java.util.List;

public class Bloc {

    private int id;
    private String nom;
    private String description;
    private String statut;
    private List<Specialite> statutSpec = new ArrayList<>();

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public List<Specialite> getStatutSpec() {
        return statutSpec;
    }

    public void addStatutSpec(Specialite specialite) {
        if (!statutSpec.contains(specialite)) {
            statutSpec.add(specialite);
            specialite.setIdBloc(this);
        }
    }

    public void removeStatutSpec(Specialite specialite) {
        if (statutSpec.remove(specialite)) {
            if (specialite.getIdBloc() == this) {
                specialite.setIdBloc(null);
            }
        }
    }

    @Override
    public String toString() {
        return "Bloc{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", statutSpec=" + statutSpec +
                '}';
    }
}
