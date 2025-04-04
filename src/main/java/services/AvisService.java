package services;

import entities.Avis;
import entities.Roles;
import entities.User;
import utils.JDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AvisService implements ICrud<Avis> {

    private final int staticCurrentUserId = 22;
    private final UserService userService = new UserService();
    private Connection cnx = JDBConnection.getInstance().getCnx();

    @Override
    public void insertOne(Avis avis) throws SQLException {
        String req = "INSERT INTO `avis`( `id_user`, `professional_id`, `note`,`commentaire`,`date_avis`) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, avis.getUser().getId());
        ps.setInt(2, avis.getProfessional().getId());
        ps.setInt(3, avis.getNote());
        ps.setString(4, avis.getCommentaire());
        ps.setDate(5, avis.getDateAvis());

        ps.executeUpdate();
    }

    @Override
    public void updateOne(Avis avis) throws SQLException {
        String req = "UPDATE `avis` SET `id_user`=?, `professional_id`=?, `note`=?, `commentaire`=?, `date_avis`=? WHERE `ref`=?";

        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, staticCurrentUserId);
        ps.setInt(2, avis.getProfessional().getId());
        ps.setInt(3, avis.getNote());
        ps.setString(4, avis.getCommentaire());
        ps.setDate(5, avis.getDateAvis());
        ps.setInt(6, avis.getRef());

        ps.executeUpdate();
    }


    @Override
    public void deleteOne(Avis avis) throws SQLException {
        String query = "DELETE FROM avis WHERE ref = ?";
        PreparedStatement ps;
        ps = cnx.prepareStatement(query);
        ps.setInt(1, avis.getRef());
        ps.executeUpdate();
    }

    @Override
    public List<Avis> selectAll() throws SQLException {
        List<Avis> aviss = new ArrayList<>();
        String query = "SELECT * FROM avis";
        PreparedStatement ps;

        ps = cnx.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Avis avis = new Avis();
            avis.setRef(rs.getInt(("ref")));
            avis.setNote(rs.getInt("note"));
            avis.setCommentaire(rs.getString("commentaire"));
            avis.setDateAvis(rs.getDate("date_avis"));

            int idUser = rs.getInt("id_user");
            User user = userService.findById(idUser);
            avis.setUser(user);

            int idProfessional = rs.getInt("professional_id");
            User professional = userService.findById(idProfessional);
            avis.setProfessional(professional);

            // Set other properties
            aviss.add(avis);
        }

        return aviss;
    }

    public Avis findByRef(int ref) throws SQLException {
        String query = "SELECT * FROM avis WHERE ref = ?";

        PreparedStatement ps;
        ps = cnx.prepareStatement(query);
        ps.setInt(1, ref);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Avis avis = new Avis();
            avis.setRef(rs.getInt(("ref")));
            avis.setNote(rs.getInt("note"));
            avis.setCommentaire(rs.getString("commentaire"));
            avis.setDateAvis(rs.getDate("date_avis"));

            int idUser = rs.getInt("id_user");
            User user = userService.findById(idUser);
            avis.setUser(user);

            int idProfessional = rs.getInt("professional_id");
            User professional = userService.findById(idProfessional);
            avis.setProfessional(professional);


            return avis;
        } else {
            return null; // No avis found with the given ref
        }
    }
    public List<User> selectAllProfessional() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM id_user";
        PreparedStatement ps;

        ps = cnx.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            try {
            if(Roles.valueOf(rs.getString("role").trim().toUpperCase()).equals(Roles.ROLE_PROFESSIONAL)) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNameUser(rs.getString("name_user"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(Roles.valueOf(rs.getString("role").trim().toUpperCase()));

                // Set other properties
                users.add(user);
            }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid role: " + rs.getString("role"));
            }
        }

        return users;
    }

}

