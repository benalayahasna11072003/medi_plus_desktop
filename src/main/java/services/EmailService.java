package services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.io.UnsupportedEncodingException;

public class EmailService {
    private static final String FROM_EMAIL = "jmalyessine114@gmail.com";
    private static final String PASSWORD = "onjw zfqp jpzo kksp";
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";

    public void sendResetPasswordEmail(String toEmail, String resetToken) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse email ne peut pas être vide");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.ssl.trust", HOST);
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.debug", "true"); // Active le mode debug pour voir les logs

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress(FROM_EMAIL, "MediPlus Support"));
            } catch (UnsupportedEncodingException e) {
                message.setFrom(new InternetAddress(FROM_EMAIL));
                System.err.println("Erreur d'encodage pour le nom d'expéditeur : " + e.getMessage());
            }
            
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Réinitialisation de votre mot de passe MediPlus");

            String content = "Bonjour,\n\n"
                    + "Vous avez demandé la réinitialisation de votre mot de passe.\n\n"
                    + "Pour réinitialiser votre mot de passe, veuillez suivre ces étapes :\n"
                    + "1. Ouvrez l'application MediPlus\n"
                    + "2. Cliquez sur 'Mot de passe oublié'\n"
                    + "3. Entrez votre email\n"
                    + "4. Utilisez le code suivant pour réinitialiser votre mot de passe :\n\n"
                    + "Code de réinitialisation : " + resetToken + "\n\n"
                    + "Ce code expirera dans 1 heure.\n\n"
                    + "Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.\n\n"
                    + "Pour des raisons de sécurité, ne partagez jamais ce code avec qui que ce soit.\n\n"
                    + "Cordialement,\n"
                    + "L'équipe MediPlus";

            message.setText(content);

            // Envoyer l'email de manière synchrone pour mieux voir les erreurs
            Transport.send(message);
            System.out.println("Email envoyé avec succès à " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }
} 