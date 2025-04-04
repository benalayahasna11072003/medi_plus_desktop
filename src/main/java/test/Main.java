package test;

import entities.Avis;
import entities.Roles;
import entities.User;
import services.AvisService;
import services.UserService;
import utils.JDBConnection;

import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            UserService userService = new UserService();
            User user = new User();
            user.setRole(Roles.ROLE_PROFESSIONAL);
            user.setNameUser("Professionel");
            user.setPassword("123456");
            user.setEmail("pro@gmail.com");

            userService.insertOne(user);
        } catch (SQLException e) {
            System.err.println("Erreur: "+e.getMessage());
        }
    }
}