package test;

import entities.*;
import services.RendezVousService;
import services.UserService;
import utils.SUser;

import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        UserService us = new UserService();
        try {

            RendezVous rdv = new RendezVous();
            rdv.setStatusRdv("en attente");
            rdv.setUser(us.findByEmail("user@gmail.com"));
            rdv.setProfessional(SUser.getUser());
            rdv.setDateRdv(LocalDate.now());
            RendezVousService rds = new RendezVousService();
            for(int i =0 ; i<10; i++){
            rds.insertOne(rdv);}
        } catch (SQLException e) {
            System.err.println("Erreur: "+e.getMessage());
        }
    }
}