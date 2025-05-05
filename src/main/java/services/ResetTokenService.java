package services;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;
import utils.JDBConnection;

public class ResetTokenService {
    private static final int TOKEN_LENGTH = 32;
    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCKOUT_MINUTES = 30;
    private Connection cnx = JDBConnection.getInstance().getCnx();

    public String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[TOKEN_LENGTH];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public void saveResetAttempt(String email) throws SQLException {
        String sql = "INSERT INTO reset_attempts (email, attempt_time) VALUES (?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setObject(2, LocalDateTime.now());
            ps.executeUpdate();
        }
    }

    public boolean isLockedOut(String email) throws SQLException {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(LOCKOUT_MINUTES);
        String sql = "SELECT COUNT(*) FROM reset_attempts WHERE email = ? AND attempt_time > ?";
        
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setObject(2, cutoff);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int attempts = rs.getInt(1);
                return attempts >= MAX_ATTEMPTS;
            }
        }
        return false;
    }

    public void clearOldAttempts() throws SQLException {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(LOCKOUT_MINUTES);
        String sql = "DELETE FROM reset_attempts WHERE attempt_time < ?";
        
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setObject(1, cutoff);
            ps.executeUpdate();
        }
    }

    public boolean verifyToken(String token, String email) throws SQLException {
        if (isLockedOut(email)) {
            return false;
        }

        String sql = "SELECT id FROM id_user WHERE email = ? AND reset_token = ? AND reset_token_expires_at > NOW()";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, token);
            
            ResultSet rs = ps.executeQuery();
            boolean isValid = rs.next();
            
            if (!isValid) {
                saveResetAttempt(email);
            }
            
            return isValid;
        }
    }

    public void updateResetToken(int userId, String token) throws SQLException {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        String sql = "UPDATE id_user SET reset_token = ?, reset_token_expires_at = ? WHERE id = ?";
        
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setObject(2, expiresAt);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    public void clearResetToken(int userId) throws SQLException {
        String sql = "UPDATE id_user SET reset_token = NULL, reset_token_expires_at = NULL WHERE id = ?";
        
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
} 