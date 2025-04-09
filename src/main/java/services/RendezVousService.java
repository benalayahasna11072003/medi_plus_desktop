package services;

import entities.Consultation;
import entities.RendezVous;
import entities.User;
import utils.JDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RendezVousService implements ICrud<RendezVous>{


    private final UserService userService = new UserService();
    private Connection cnx = JDBConnection.getInstance().getCnx();
    private final ConsultationService consultationService = new ConsultationService();
    public void insertOne(RendezVous rendezVous) throws SQLException {
        String query = "INSERT INTO rendez_vous (date_rdv, status_rdv, id_user, professional_id) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(query);

        ps.setDate(1, java.sql.Date.valueOf(rendezVous.getDateRdv()));
        ps.setString(2, rendezVous.getStatusRdv());
        ps.setInt(3, rendezVous.getUser().getId());
        ps.setInt(4, rendezVous.getProfessional().getId());

        ps.executeUpdate();
    }


    @Override
    public void updateOne(RendezVous rendezVous) throws SQLException {

    }

    @Override
    public void deleteOne(RendezVous rendezVous) throws SQLException {

    }

    @Override
    public List<RendezVous> selectAll() throws SQLException {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String query = "SELECT * FROM rendez_vous";
        PreparedStatement ps = cnx.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            RendezVous rdv = new RendezVous();
            rdv.setId(rs.getInt("id"));
            rdv.setDateRdv(rs.getDate("date_rdv").toLocalDate());
            rdv.setStatusRdv(rs.getString("status_rdv"));

            int idUser = rs.getInt("id_user");
            User user = userService.findById(idUser);
            rdv.setUser(user);

            int idProfessional = rs.getInt("professional_id");
            User professional = userService.findById(idProfessional);
            rdv.setProfessional(professional);

            int consultationId = rs.getInt("consultation_id");
            Consultation consultation = consultationService.findById(consultationId);
            rdv.setConsultation(consultation);

            rendezVousList.add(rdv);
        }

        return rendezVousList;
    }

}
