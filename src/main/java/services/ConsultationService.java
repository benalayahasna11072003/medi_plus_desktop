package services;

import entities.Consultation;
import entities.Prescription;
import entities.RendezVous;
import entities.User;
import utils.JDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsultationService implements ICrud<Consultation>{

    UserService userService = new UserService();
    private Connection cnx = JDBConnection.getInstance().getCnx();
    @Override
    public void insertOne(Consultation consultation) throws SQLException {
        //String req = "INSERT INTO `consultation`( `id_user`, `id_professionnel`, `rendez_vous_id`,`date_consultation`, `reason`) VALUES (?, ?, ?, ?, ?)";
        String req = "INSERT INTO `consultation`( `id_user`, `id_professionnel`,`date_consultation`, `reason`) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);

        ps.setInt(1, consultation.getUser().getId());
        ps.setInt(2, consultation.getProfessionnel().getId());
        //ps.setInt(3, consultation.getRendezVous().getId()||null);
        ps.setDate(3, java.sql.Date.valueOf(consultation.getDateConsultation()));
        ps.setString(4, consultation.getReason());

        ps.executeUpdate();
    }

    @Override
    public void updateOne(Consultation consultation) throws SQLException {
        // String req = "UPDATE `consultation` SET `id_user`=?, `id_professionnel`=?, `rendez_vous_id`=?, `date_consultation`=?, `reason`=? WHERE `id`=?";
        String req = "UPDATE `consultation` SET `id_user`=?, `id_professionnel`=?, `date_consultation`=?, `reason`=? WHERE `id`=?";

        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, consultation.getUser().getId());
        ps.setInt(2, consultation.getProfessionnel().getId());
        //ps.setInt(3, consultation.getRendezVous().getId());
        ps.setDate(3, java.sql.Date.valueOf(consultation.getDateConsultation()));
        ps.setString(4, consultation.getReason());
        ps.setInt(5, consultation.getId());

        ps.executeUpdate();
    }

    @Override
    public void deleteOne(Consultation consultation) throws SQLException {
        String query = "DELETE FROM consultation WHERE id = ?";
        PreparedStatement ps;
        ps = cnx.prepareStatement(query);
        ps.setInt(1, consultation.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Consultation> selectAll() throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        String query = "SELECT * FROM consultation";


        PreparedStatement ps;

        ps = cnx.prepareStatement(query);
        ResultSet rs = ps.executeQuery();


        while (rs.next()) {


            int idUser = rs.getInt("id_user");

            User user = userService.findById(idUser);

            int idProfessional = rs.getInt("id_professionnel");
            User professional = userService.findById(idProfessional);

            int rendezVousId = rs.getInt("rendez_vous_id");
            RendezVous rendezVous = findByRendezVousById(rendezVousId);

            Consultation consultation = new Consultation();

            consultation.setId(rs.getInt(("id")));
            consultation.setUser(user);
            consultation.setProfessionnel(professional);
            consultation.setRendezVous(rendezVous);
            consultation.setDateConsultation(rs.getDate("date_consultation").toLocalDate());
            consultation.setReason(rs.getString("reason"));

            // Set other properties
            consultations.add(consultation);
        }
        return consultations;
    }

    public RendezVous findByRendezVousById(int id) throws SQLException {
        String query = "SELECT * FROM rendez_vous WHERE id = ?";

        PreparedStatement ps;
        ps = cnx.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int idUser = rs.getInt("id_user");
            User user = userService.findById(idUser);

            int idProfessional = rs.getInt("professional_id");
            User professional = userService.findById(idProfessional);

            int idConsulatation = rs.getInt("consultation_id");
            Consultation consultation = findById(idConsulatation);

            RendezVous rendezVous = new RendezVous();

            rendezVous.setConsultation(consultation);
            rendezVous.setUser(user);
            rendezVous.setProfessional(professional);
            rendezVous.setDateRdv(rs.getDate("date_rdv").toLocalDate());
            rendezVous.setStatusRdv(rs.getString("status_rdv"));
            rendezVous.setId(id);
            return rendezVous;
        } else {
            return null; // No rendez vous found with the given id
        }
    }

    public Consultation findById(int id) throws SQLException {
        String query = "SELECT * FROM consultation WHERE id = ?";

        PreparedStatement ps;
        ps = cnx.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int idUser = rs.getInt("id_user");
            User user = userService.findById(idUser);

            int idProfessional = rs.getInt("id_professionnel");
            User professional = userService.findById(idProfessional);

            int rendezVousId = rs.getInt("rendez_vous_id");
            RendezVous rendezVous = findByRendezVousById(rendezVousId);

            Consultation consultation = new Consultation();

            consultation.setId(rs.getInt(("id")));
            consultation.setUser(user);
            consultation.setProfessionnel(professional);
            consultation.setRendezVous(rendezVous);
            consultation.setDateConsultation(rs.getDate("date_consultation").toLocalDate());
            consultation.setReason(rs.getString("reason"));

            return consultation;
        } else {
            return null; // No user found with the given email
        }

    }

}
