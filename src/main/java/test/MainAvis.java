package test;

import entities.Avis;
import entities.Reponse;
import services.gestionAvis.AvisService;
import services.gestionAvis.ReponseService;
import services.UserService;

import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

public class MainAvis {
    public static void main(String[] args) {

        AvisService avisService = new AvisService();
        UserService userService = new UserService();
        ReponseService reponseService = new ReponseService();
        try {
            Avis avis = new Avis();
            avis.setUser(userService.findById(10));
            avis.setProfessional(userService.findById(10));
            avis.setNote(10);
            avis.setCommentaire("cmnt");
            avis.setDateAvis(Date.valueOf("2000-01-01"));

            avisService.insertOne(avis);

            List<Avis> aviss = avisService.selectAll();
            System.out.println("list avis : "+aviss);
            Avis updatedAvis = aviss.getFirst();
            updatedAvis.setCommentaire("updated Avis");

            avisService.updateOne(updatedAvis);


            aviss = avisService.selectAll();
            System.out.println("aviss after update : "+aviss);

            Reponse reponse = new Reponse();
            reponse.setAvis(updatedAvis);
            reponse.setProfessional(userService.findById(10));
            reponse.setReponse("reponse 1");
            reponse.setDateReponse(Date.valueOf("2000-01-05"));
            reponse.setMadeBy(userService.findById(10));

            reponseService.insertOne(reponse);
            List<Reponse> reponses = reponseService.selectAll();
            System.out.println("list of response : "+ reponses);
            Reponse updatedResponse = reponses.getFirst();
            updatedResponse.setReponse("updated reponse");
            reponseService.updateOne(updatedResponse);
            reponses = reponseService.selectAll();
            System.out.println("list response after update : "+reponses);
            reponseService.deleteOne(updatedResponse);
            reponses=reponseService.selectAll();
            System.out.println("list response after delete : "+reponses);



            avisService.deleteOne(updatedAvis);

            aviss = avisService.selectAll();
            System.out.println("avis after delete : "+aviss);
        } catch (SQLException e) {
            System.err.println("Erreur: "+e.getMessage());
        }
    }
}
