package test;

import entities.Avis;
import services.AvisService;
import utils.JDBConnection;

import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        JDBConnection cnx = JDBConnection.getInstance();
        try {
            AvisService avisService = new AvisService();
            List<Avis> aviss = avisService.selectAll();
            System.out.println(aviss);

            Avis avis = aviss.getFirst();

            avisService.insertOne(avis);
            System.out.println("_________________");
            avis.setDateAvis(Date.valueOf("2000-01-01"));
            avisService.updateOne(avis);
            System.out.println("_________________");

            aviss = avisService.selectAll();
            System.out.println(aviss);

            avisService.deleteOne(avis);

            aviss = avisService.selectAll();
            System.out.println(aviss);
        } catch (SQLException e) {
            System.err.println("Erreur: "+e.getMessage());
        }
    }
}