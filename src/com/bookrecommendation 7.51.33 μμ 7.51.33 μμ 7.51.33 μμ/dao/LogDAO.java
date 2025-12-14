/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookrecommendation.dao;

import com.bookrecommendation.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO για την ανάγνωση του Log File από τη βάση.
 *
 * Προϋπόθεση στη DB:
 *   table log_file (id, log_time, username, action_type, details)
 */
public class LogDAO {

    public static class LogEntry {
        private final long id;
        private final Timestamp time;
        private final String username;
        private final String type;
        private final String details;

        public LogEntry(long id, Timestamp time, String username, String type, String details) {
            this.id = id;
            this.time = time;
            this.username = username;
            this.type = type;
            this.details = details;
        }

        public long getId() { return id; }
        public Timestamp getTime() { return time; }
        public String getUsername() { return username; }
        public String getType() { return type; }
        public String getDetails() { return details; }
    }

    /** Επιστρέφει τα logs (πιο πρόσφατα πρώτα). */
    public List<LogEntry> getAllLogs() {
        List<LogEntry> logs = new ArrayList<>();

        String sql =
                "SELECT id, log_time, username, action_type, details " +
                "FROM log_file " +
                "ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                logs.add(new LogEntry(
                        rs.getLong("id"),
                        rs.getTimestamp("log_time"),
                        rs.getString("username"),
                        rs.getString("action_type"),
                        rs.getString("details")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }
}