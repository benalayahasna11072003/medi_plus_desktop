package entities;
import java.sql.Date;

public class Reponse {

    private int id;
    private String reponse;
    private Date dateReponse;
    private Avis avis;
    private User professional;
    private User madeBy;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse != null ? reponse : "";
    }

    public Date getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(Date dateReponse) {
        this.dateReponse = dateReponse;
    }

    public Avis getAvis() {
        return avis;
    }

    public void setAvis(Avis avis) {
        this.avis = avis;
    }

    public User getProfessional() {
        return professional;
    }

    public void setProfessional(User professional) {
        this.professional = professional;
    }

    public User getMadeBy() {
        return madeBy;
    }

    public void setMadeBy(User madeBy) {
        this.madeBy = madeBy;
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "id=" + id +
                ", reponse='" + reponse + '\'' +
                ", dateReponse=" + dateReponse +
                ", avis=" + avis +
                ", professional=" + professional +
                ", madeBy=" + madeBy +
                '}';
    }
}
