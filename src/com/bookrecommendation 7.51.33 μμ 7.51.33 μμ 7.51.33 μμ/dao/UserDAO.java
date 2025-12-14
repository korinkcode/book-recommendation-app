package com.bookrecommendation.dao;

import com.bookrecommendation.model.User;
import com.bookrecommendation.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User-related database operations.
 */
public class UserDAO {

    public User getUserByUsername(String username) {
        String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String uname = rs.getString("username");
                    String passwordHash = rs.getString("password_hash");
                    return new User(id, uname, passwordHash);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // δεν βρέθηκε user
    }

    public void addUser(User user) {
        // ΔΕΝ έχουμε πλέον email στη βάση
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Μικρό DTO για την σελίδα UsersPage (id, username, created_at).
     */
    public static class UserAdminView {
        private final int id;
        private final String username;
        private final String passwordHash;
        private final Timestamp lastLogin;

        public UserAdminView(int id, String username, String passwordHash, Timestamp lastLogin) {
            this.id = id;
            this.username = username;
            this.passwordHash = passwordHash;
            this.lastLogin = lastLogin;
        }

        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getPasswordHash() { return passwordHash; }
        public Timestamp getLastLogin() { return lastLogin; }
    }


    /**
     * Επιστρέφει όλους τους users για την UsersPage.
     */
   public List<UserAdminView> getAllUsersAdmin() {
    List<UserAdminView> users = new ArrayList<>();

    String sql =
        "SELECT id, username, password_hash, last_login " +
        "FROM users ORDER BY id ASC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String uname = rs.getString("username");
            String passHash = rs.getString("password_hash");
            Timestamp lastLogin = rs.getTimestamp("last_login"); // ✅

            users.add(
                new UserAdminView(id, uname, passHash, lastLogin)
            );
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return users;
}

    
    public void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Διαγράφει user με βάση το id.
     */
    public void deleteUserById(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
