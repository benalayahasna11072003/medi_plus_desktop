package utils;

public class PasswordValidator {
    public static final int MIN_LENGTH = 8;
    
    public static boolean isValid(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return false;
        }
        
        // Doit contenir au moins un chiffre
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        
        // Doit contenir au moins une majuscule
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        
        // Doit contenir au moins une minuscule
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        
        // Doit contenir au moins un caractère spécial
        if (!password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{};:,.<>/?].*")) {
            return false;
        }
        
        return true;
    }
    
    public static String getRequirements() {
        return String.format(
            "Le mot de passe doit contenir au moins :\n" +
            "- %d caractères\n" +
            "- Une lettre majuscule\n" +
            "- Une lettre minuscule\n" +
            "- Un chiffre\n" +
            "- Un caractère spécial (!@#$%%^&*()-_=+[]{}:;,.<>/?)",
            MIN_LENGTH
        );
    }
} 