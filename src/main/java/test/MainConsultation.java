package test;


import entities.Consultation;

import entities.Prescription;
import services.ConsultationService;
import services.PrescriptionService;
import services.UserService;


import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class MainConsultation {
    public static void main(String[] args) {
        ConsultationService consultationService = new ConsultationService();
        PrescriptionService prescriptionService = new PrescriptionService();
        UserService userService = new UserService();
        try {
            Consultation consultation = new Consultation();
            consultation.setUser(userService.findById(10));
            consultation.setProfessionnel(userService.findById(10));
            //consultation.setRendezVous(consultationService.findByRendezVousById(1));
            consultation.setDateConsultation(LocalDate.now());
            consultation.setReason("reason");
            consultationService.insertOne(consultation);

            List<Consultation> consultations = consultationService.selectAll();
            System.out.println("list consultation : "+consultations);

            consultation=consultations.getFirst();






            consultation.setReason("updated consult");
            consultationService.updateOne(consultation);

            System.out.println("consultations after update : "+consultationService.selectAll());


            Prescription prescription = new Prescription();
            prescription.setConsultation(consultation);
            prescription.setDescription("prescription");
            prescription.setCreatedAt(LocalDate.now());

            prescriptionService.insertOne(prescription);

            List<Prescription> prescriptions = prescriptionService.selectAll();
            prescription= prescriptions.getFirst();
            System.out.println("list presc : "+prescriptions);
            prescription.setDescription("updated prescription");
            prescriptionService.updateOne(prescription);
            System.out.println("list presc after update : "+prescriptionService.selectAll());
            prescriptionService.deleteOne(prescription);
            System.out.println("list presc after delete : "+prescriptionService.selectAll());


            consultationService.deleteOne(consultation);

            System.out.println("list consult after delete : "+consultationService.selectAll());
        } catch (SQLException e) {
            System.err.println("Erreur: "+e.getMessage());
        }
    }
}
