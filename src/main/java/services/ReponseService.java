package services;

import entities.Avis;
import entities.Reponse;
import entities.User;
import utils.JDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReponseService implements ICrud<Reponse> {
    private final UserService userService = new UserService();
    private final AvisService avisService = new AvisService();
    private Connection cnx = JDBConnection.getInstance().getCnx();
    @Override
    public void insertOne(Reponse reponse) throws SQLException {
        String req = "INSERT INTO `reponse`( `id_avis`, `professional_id`, `reponse`,`date_reponse`, `madeBy_id`) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, reponse.getAvis().getRef());
        ps.setInt(2, reponse.getProfessional().getId());
        ps.setString(3, reponse.getReponse());
        ps.setDate(4, reponse.getDateReponse());
        ps.setInt(5, reponse.getMadeBy().getId());
        ps.executeUpdate();
    }

    @Override
    public void updateOne(Reponse reponse) throws SQLException {
        String req = "UPDATE `reponse` SET `id_avis`=?, `professional_id`=?, `reponse`=?, `date_reponse`=?, `madeBy_id`=? WHERE `id`=?";

        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, reponse.getAvis().getRef());
        ps.setInt(2, reponse.getProfessional().getId());
        ps.setString(3, reponse.getReponse());
        ps.setDate(4, reponse.getDateReponse());
        ps.setInt(5, reponse.getMadeBy().getId());
        ps.setInt(6, reponse.getId());

        ps.executeUpdate();
    }

    @Override
    public void deleteOne(Reponse reponse) throws SQLException {
        String query = "DELETE FROM reponse WHERE id = ?";
        PreparedStatement ps;
        ps = cnx.prepareStatement(query);
        ps.setInt(1, reponse.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Reponse> selectAll() throws SQLException {
        List<Reponse> reponses = new ArrayList<>();
        String query = "SELECT * FROM reponse";
        PreparedStatement ps;

        ps = cnx.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int idUser = rs.getInt("madeBy_id");
            User user = userService.findById(idUser);

            int idAvis = rs.getInt("id_avis");
            Avis avis = avisService.findByRef(idAvis);

            int idProfessional = rs.getInt("professional_id");
            User professional = userService.findById(idProfessional);

            Reponse reponse = new Reponse();

            reponse.setId(rs.getInt(("id")));
            reponse.setAvis(avis);
            reponse.setProfessional(professional);
            reponse.setReponse(rs.getString("reponse"));
            reponse.setDateReponse(rs.getDate("date_reponse"));
            reponse.setMadeBy(user);

            // Set other properties
            reponses.add(reponse);
        }

        return reponses;
    }

}
