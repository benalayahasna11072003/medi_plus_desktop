package test;

import entities.Roles;
import entities.User;
import services.UserService;

import java.sql.SQLException;

public class LoginTest {
    public static void main(String[] args) {
        UserService userService = new UserService();
        
        // Test avec un utilisateur existant
        try {
            // Créer un utilisateur de test
            User testUser = new User();
            testUser.setNameUser("Test User");
            testUser.setEmail("test@example.com");
            testUser.setPassword("password123");
            testUser.setRole(String.valueOf(Roles.ROLE_PATIENT));
            
            // Insérer l'utilisateur de test
            userService.insertOne(testUser);
            
            // Tester le login
            User loggedInUser = userService.login("test@example.com", "password123");
            
            if (loggedInUser != null) {
                System.out.println("Login réussi!");
                System.out.println("Nom: " + loggedInUser.getNameUser());
                System.out.println("Email: " + loggedInUser.getEmail());
                System.out.println("Rôle: " + loggedInUser.getRole());
            } else {
                System.out.println("Login échoué: Email ou mot de passe incorrect");
            }
            
            // Tester avec des identifiants incorrects
            User failedLogin = userService.login("test@example.com", "wrongpassword");
            if (failedLogin == null) {
                System.out.println("Test avec mot de passe incorrect réussi: Login refusé comme attendu");
            }
            
        } catch (SQLException e) {
            System.out.println("Erreur lors du test de login: " + e.getMessage());
        }
    }
} 