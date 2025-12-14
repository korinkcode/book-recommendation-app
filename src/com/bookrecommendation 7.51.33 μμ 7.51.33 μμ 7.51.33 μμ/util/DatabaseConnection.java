/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.bookrecommendation.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections.
 */
public class DatabaseConnection {
      // Βάλε εδώ ΑΚΡΙΒΩΣ τα στοιχεία που δουλεύουν στο pgAdmin
    // ΜΟΡΦΗ:
    // jdbc:postgresql://HOST:PORT/DBNAME
    // και αν χρειάζεται SSL:
    // jdbc:postgresql://HOST:PORT/DBNAME?sslmode=require

    private static final String DATABASE_URL =
            "jdbc:postgresql://dblabs.iee.ihu.gr:5432/aikakara12"; // ΠΡΟΣΑΡΜΟΣΕ ΤΟ
    private static final String USERNAME = "aikakara12";      // ΠΡΟΣΑΡΜΟΣΕ
    private static final String PASSWORD = "2004korina";      // ΠΡΟΣΑΡΜΟΣΕ

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }

    // ΜΙΚΡΟ TEST
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connected OK: " + conn.getMetaData().getURL());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}