package test;

import services.UserService;

import entities.Roles;
import entities.User;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

       /* UserService us = new UserService();
        try {

            RendezVous rdv = new RendezVous();
            rdv.setStatusRdv("en attente");
            rdv.setUser(us.findByEmail("user@gmail.com"));
            rdv.setProfessional(SUser.getUser());
            rdv.setDateRdv(LocalDate.now());
            RendezVousService rds = new RendezVousService();
            for(int i =0 ; i<10; i++){
            rds.insertOne(rdv);}*/
        try {
            UserService userService = new UserService();
            User user = new User();
            user.setRole(Roles.professionnel);
            user.setNameUser("Professionel");
            user.setPassword("123456");
            user.setEmail("pro@gmail.com");

            userService.insertOne(user);

        } catch (SQLException e) {
            System.err.println("Erreur: "+e.getMessage());
        }
    }
}