package services.gestionAvis;

import entities.Avis;
import entities.Roles;
import entities.User;
import services.ICrud;
import services.UserService;
import utils.JDBConnection;
import utils.SUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvisService implements ICrud<Avis> {

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
        ps.setInt(1, SUser.getUser().getId());
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
            if(Roles.valueOf(rs.getString("role").trim()).equals(Roles.professionnel)) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNameUser(rs.getString("name_user"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(Roles.valueOf(rs.getString("role").trim()));

                // Set other properties
                users.add(user);
            }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid role: " + rs.getString("role"));
            }
        }

        return users;
    }


    /*public List<Avis> recuperer() throws SQLException {
        String sql = "SELECT * FROM avis";
        Statement statement = cnx.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        List<Avis> avisList = new ArrayList<>();

        while (rs.next()) {
            Avis a = new Avis();
            //a.setRef(rs.getInt("ref"));
            a.setNote(rs.getInt("note"));
            //a.setCommentaire(rs.getString("commentaire"));
            //a.setDateAvis(rs.getDate("date_avis"));

            // Assuming user and professional are stored as user IDs (int)
            int userId = rs.getInt("id_user");
            int professionalId = rs.getInt("professional_id");

            User user = userService.findById(userId);
            User professional = userService.findById(professionalId);

            a.setUser(user);
            a.setProfessional(professional);

            avisList.add(a);
        }
        System.out.println(avisList);
        return avisList;
    }*/
    public List<Avis> recupererPourProfessional(int professionalId) throws SQLException {
        String sql = "SELECT * FROM avis WHERE professional_id = ?";
        PreparedStatement statement = cnx.prepareStatement(sql);
        statement.setInt(1, professionalId);
        ResultSet rs = statement.executeQuery();
        List<Avis> avisList = new ArrayList<>();

        while (rs.next()) {
            Avis a = new Avis();
            a.setNote(rs.getInt("note"));

            int userId = rs.getInt("id_user");
            int profId = rs.getInt("professional_id");

            User user = userService.findById(userId);
            User professional = userService.findById(profId);

            a.setUser(user);
            a.setProfessional(professional);

            avisList.add(a);
        }
        System.out.println(avisList);
        return avisList;
    }








}

