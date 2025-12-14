/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookrecommendation.model;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 *
 * @author korinakaratzioti
 */
/**
 
Model class representing an application user.*/
public class User {

    private int id;
    private final String username;
    private final String passwordHash;

    // Constructor για SIGN UP (παίρνει plain password και το κάνει hash)
    public User(String username, String plainPassword) {
        this.username = username;
        this.passwordHash = hashPassword(plainPassword);
    }

    // Constructor για DAO (διαβάζουμε ήδη hashed password από τη βάση)
    public User(int id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // Getters / setters
    public int getId() {
        return id;
    }

    public void setId(int id) {  // για να βάζουμε το generated id μετά το INSERT
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    // Έλεγχος κωδικού στο Login
    public boolean verifyPassword(String plainPassword) {
        return this.passwordHash.equals(hashPassword(plainPassword));
    }

    // Hashing με SHA-256 + Base64
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error initializing password hashing algorithm.", e);
        }
    }

    @Override
    public String toString() {
        return username;
    }
}
