package test;

import services.AvisService;
import utils.JDBConnection;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        JDBConnection cnx = JDBConnection.getInstance();
        try {
            AvisService avisService = new AvisService();

            System.out.println(avisService.selectAll());


        } catch (SQLException e) {
            System.err.println("Erreur: "+e.getMessage());
        }
    }
}
