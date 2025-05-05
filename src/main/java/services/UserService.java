package services;

import entities.User;
import utils.JDBConnection;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IUserService {
    private Connection cnx = JDBConnection.getInstance().getCnx();
    private static final int SALT_LENGTH = 16;

    public UserService() {
        try {
            // Check if salt column exists
            ResultSet rs = cnx.getMetaData().getColumns(null, null, "id_user", "salt");
            if (!rs.next()) {
                // Add salt column if it doesn't exist
                String alterTable = "ALTER TABLE id_user ADD COLUMN salt VARBINARY(16) NOT NULL AFTER password";
                PreparedStatement ps = cnx.prepareStatement(alterTable);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error checking/adding salt column: " + e.getMessage());
        }
    }

    private String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    @Override
    public void insertOne(User user) throws SQLException {
        String req = "INSERT INTO `id_user`(`name_user`, `email`, `password`, `salt`, `role`) VALUES (?, ?, ?, ?, ?)";
        byte[] salt = generateSalt();
        String hashedPassword = hashPassword(user.getPassword(), salt);
        
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, user.getNameUser());
        ps.setString(2, user.getEmail());
        ps.setString(3, hashedPassword);
        ps.setBytes(4, salt);
        ps.setString(5, user.getRole());
        ps.executeUpdate();
    }

    @Override
    public void updateOne(User user) throws SQLException {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // Si le mot de passe est modifié, générer un nouveau salt et hasher
            String req = "UPDATE `id_user` SET `name_user`= ?, `email`= ?, `password`= ?, `salt`= ?, `role`= ? WHERE `id`= ?";
            byte[] salt = generateSalt();
            String hashedPassword = hashPassword(user.getPassword(), salt);
            
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, user.getNameUser());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashedPassword);
            ps.setBytes(4, salt);
            ps.setString(5, user.getRole());
            ps.setInt(6, user.getId());
            ps.executeUpdate();
        } else {
            // Si le mot de passe n'est pas modifié, ne pas toucher au password et salt
            String req = "UPDATE `id_user` SET `name_user`= ?, `email`= ?, `role`= ? WHERE `id`= ?";
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, user.getNameUser());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole());
            ps.setInt(4, user.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteOne(User user) throws SQLException {
        String req = "DELETE FROM `id_user` WHERE `id` = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, user.getId());
        ps.executeUpdate();
    }

    @Override
    public List<User> selectAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM `id_user`";
        PreparedStatement ps = cnx.prepareStatement(req);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            users.add(mapResultSetToUser(rs));
        }
        return users;
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM id_user WHERE email = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return mapResultSetToUser(rs);
        }
        return null;
    }

    @Override
    public User findById(int id) throws SQLException {
        String query = "SELECT * FROM id_user WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return mapResultSetToUser(rs);
        }
        return null;
    }

    @Override
    public User login(String email, String password) throws SQLException {
        String query = "SELECT * FROM id_user WHERE email = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            byte[] salt = rs.getBytes("salt");
            String hashedPassword = hashPassword(password, salt);
            
            if (hashedPassword.equals(rs.getString("password"))) {
                return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNameUser(rs.getString("name_user"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        
        // Gestion des champs nullable
        String resetToken = rs.getString("reset_token");
        if (resetToken != null) {
            user.setResetToken(resetToken);
            java.sql.Timestamp timestamp = rs.getTimestamp("reset_token_expires_at");
            if (timestamp != null) {
                user.setResetTokenExpiresAt(timestamp.toLocalDateTime());
            }
        }
        
        return user;
    }

    public void updateResetToken(int userId, String token, LocalDateTime expiresAt) throws SQLException {
        String query = "UPDATE id_user SET reset_token = ?, reset_token_expires_at = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, token);
        ps.setObject(2, expiresAt);
        ps.setInt(3, userId);
        ps.executeUpdate();
    }

    public void clearResetToken(int userId) throws SQLException {
        String query = "UPDATE id_user SET reset_token = NULL, reset_token_expires_at = NULL WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, userId);
        ps.executeUpdate();
    }

    public boolean updatePasswordWithToken(String token, String newPassword) {
        String sql = "UPDATE id_user SET password = ?, salt = ?, reset_token = NULL, reset_token_expires_at = NULL " +
                    "WHERE reset_token = ? AND reset_token_expires_at > NOW()";
        
        try (Connection conn = JDBConnection.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            byte[] salt = generateSalt();
            String hashedPassword = hashPassword(newPassword, salt);
            
            ps.setString(1, hashedPassword);
            ps.setBytes(2, salt);
            ps.setString(3, token);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du mot de passe : " + e.getMessage());
            return false;
        }
    }
}
