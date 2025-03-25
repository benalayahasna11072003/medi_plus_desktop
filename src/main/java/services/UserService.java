package services;

import entities.Roles;
import entities.User;
import utils.JDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserService implements ICrud<User> {
    private Connection cnx = JDBConnection.getInstance().getCnx();

    @Override
    public void insertOne(User user) throws SQLException {
        //the entitie name is 'User' in java but in mysql is 'id_user', be careful into other methodes!!!!!!
        String req = "INSERT INTO `id_user`(`name_user`, `email`, `password`, `role`) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, user.getNameUser());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getRole().name());
        ps.executeUpdate();
    }

    @Override
    public void updateOne(User user) throws SQLException {

    }

    @Override
    public void deleteOne(User user) throws SQLException {

    }

    @Override
    public List<User> selectAll() throws SQLException {
        return null;
    }

    public User findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM id_user WHERE email = ?";

        PreparedStatement ps;
        ps = cnx.prepareStatement(query);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRole(Roles.valueOf(rs.getString("role").trim().toUpperCase()));


            return user;
        } else {
            return null; // No user found with the given email
        }

    }
    public User findById(int id) throws SQLException {
        String query = "SELECT * FROM id_user WHERE id = ?";

        PreparedStatement ps;
        ps = cnx.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            User user = new User();

            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRole(Roles.valueOf(rs.getString("role").trim().toUpperCase()));

            return user;
        } else {
            return null; // No user found with the given email
        }

    }
}
