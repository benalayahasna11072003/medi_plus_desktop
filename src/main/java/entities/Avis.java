package entities;
import java.sql.Date;

public class Avis {

    private int ref;
    private int note;
    private String commentaire = "";
    private Date dateAvis;
    private User user;
    private User professional;

    // Getters and Setters
    public int getRef() {
        return ref;
    }

    public int getNote() {
        return note;
    }

    public void setRef(int ref) {
        this.ref = ref;
    }

    public void setNote(int note) {
        this.note = note;
    }


    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire != null ? commentaire : "";
    }

    public Date getDateAvis() {
        return dateAvis;
    }

    public void setDateAvis(Date dateAvis) {
        this.dateAvis = dateAvis;
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

    @Override
    public String toString() {
        return "Avis{" +
                "ref=" + ref +
                ", note=" + note +
                ", commentaire='" + commentaire + '\'' +
                ", dateAvis=" + dateAvis +
                ", user=" + user +
                ", professional=" + professional +
                '}';
    }
}
