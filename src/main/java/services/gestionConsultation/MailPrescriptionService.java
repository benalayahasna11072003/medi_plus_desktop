package services.gestionConsultation;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class MailPrescriptionService {
    private static final String FROM_EMAIL = "rahmaelaid6@gmail.com"; // Change this to your email
    private static final String PASSWORD = "odrzandcvdrzissc"; // Change this to your email password


    public static void sendPrescriptionEmail(String recipientEmail, String patientName, String doctorName,
                                             String prescriptionDetails, String pdfFilePath) throws MessagingException, IOException {
        // Set up mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

        // Set up authentication
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        };

        // Create a session
        Session session = Session.getInstance(properties, authenticator);
        session.setDebug(true);

        // Create MimeMessage
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        message.setSubject("Nouvelle ordonnance médicale");

        // Create HTML content for the email body
        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, Helvetica, sans-serif; color: #333333; line-height: 1.6; }"
                + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
                + ".header { margin-bottom: 20px; }"
                + ".prescription-details { margin: 20px 0; padding-left: 20px; border-left: 3px solid #0066CC; }"
                + ".footer { margin-top: 30px; font-style: italic; color: #666666; }"
                + ".signature { font-style: italic; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<p>Bonjour <strong>" + patientName + "</strong>,</p>"
                + "</div>"
                + "<p>Votre médecin, <strong>" + doctorName + "</strong>, vous a prescrit une nouvelle ordonnance.</p>"
                + "<div class='prescription-details'>"
                + "<h3>Détails de la prescription :</h3>"
                + "<p>" + prescriptionDetails + "</p>"
                + "</div>"
                + "<p>Pour toute question, n'hésitez pas à contacter votre médecin.</p>"
                + "<div class='footer'>"
                + "<p>Cordialement,</p>"
                + "<p class='signature'>L'équipe Mediplus</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";



        // Create body part for the HTML content
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html; charset=utf-8");

        // Create body part for the attachment
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(new File(pdfFilePath));
        attachmentPart.setFileName("prescription.pdf");

        // Create Multipart and add parts
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(htmlPart);
        multipart.addBodyPart(attachmentPart);

        // Set content
        message.setContent(multipart);

        // Send the message
        Transport.send(message);
        System.out.println("Prescription email sent successfully to: " + recipientEmail);
    }


}
