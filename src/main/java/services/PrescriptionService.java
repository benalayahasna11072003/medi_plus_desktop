package services;

import entities.Prescription;
import utils.JDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionService implements ICrud<Prescription>{
    ConsultationService consultationService = new ConsultationService();
    private Connection cnx = JDBConnection.getInstance().getCnx();
    @Override
    public void insertOne(Prescription prescription) throws SQLException {
        String req = "INSERT INTO `prescription`( `id_consultation`, `description`, `created_at`) VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);

        ps.setInt(1, prescription.getConsultation().getId());
        ps.setString(2, prescription.getDescription());
        ps.setDate(3, java.sql.Date.valueOf(prescription.getCreatedAt()));

        ps.executeUpdate();
    }

    @Override
    public void updateOne(Prescription prescription) throws SQLException {
        String req = "UPDATE `prescription` SET `id_consultation`=?, `description`=?, `created_at`=? WHERE `id`=?";

        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, prescription.getConsultation().getId());
        ps.setString(2, prescription.getDescription());
        ps.setDate(3, java.sql.Date.valueOf(prescription.getCreatedAt()));
        ps.setInt(4, prescription.getId());


        ps.executeUpdate();
    }

    @Override
    public void deleteOne(Prescription prescription) throws SQLException {
        String query = "DELETE FROM prescription WHERE id = ?";
        PreparedStatement ps;
        ps = cnx.prepareStatement(query);
        ps.setInt(1, prescription.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Prescription> selectAll() throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String query = "SELECT * FROM prescription";
        PreparedStatement ps;

        ps = cnx.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            Prescription prescription = new Prescription();

            prescription.setId(rs.getInt(("id")));
            prescription.setConsultation(consultationService.findById(rs.getInt(("id_consultation"))));

            prescription.setCreatedAt(rs.getDate("created_at").toLocalDate());
            prescription.setDescription(rs.getString("description"));

            // Set other properties
            prescriptions.add(prescription);
        }
        return prescriptions;
    }
}
