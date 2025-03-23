package entities;
public class Specialite {

    private int id;
    private String nom;
    private String description;
    private int dureeConsultation;
    private Float tarif;
    private Bloc idBloc;
    private String statut;

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

    public int getDureeConsultation() {
        return dureeConsultation;
    }

    public void setDureeConsultation(int dureeConsultation) {
        this.dureeConsultation = dureeConsultation;
    }

    public Float getTarif() {
        return tarif;
    }

    public void setTarif(Float tarif) {
        this.tarif = tarif;
    }

    public Bloc getIdBloc() {
        return idBloc;
    }

    public void setIdBloc(Bloc idBloc) {
        this.idBloc = idBloc;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
