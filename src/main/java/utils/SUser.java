package utils;

import entities.Roles;
import entities.User;
import services.UserService;

import java.sql.*;

public class SUser {

    private static final String nameUser = "test user";
    private static final String email = "pro@gmail.com";
    private static final String password = "123123";
    private static final Roles role = Roles.ROLE_PATIENT;
    private static final int id = 0;


    private static User user;



    public static User getUser() {

        if (user==null || user.getId() == 0) {
            user = new User();
            user.setNameUser(nameUser);
            user.setRole(role);
            user.setEmail(email);
            user.setPassword(password);
            setUser();
        }
        return user;
    }


    private static void setUser() {
        try {
            System.out.println("______________________________");
            String query = "SELECT * FROM id_user WHERE email = ?";

            Connection cnx = JDBConnection.getInstance().getCnx();
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

                user.setId(rs.getInt("id"));
                user.setRole(Roles.valueOf(rs.getString("role").trim().toUpperCase()));


            } else {

                UserService userService = new UserService();
                userService.insertOne(user);
                setUser();
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());

        }
    }
}