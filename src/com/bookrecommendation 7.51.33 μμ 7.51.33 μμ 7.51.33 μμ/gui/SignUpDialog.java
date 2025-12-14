/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.bookrecommendation.gui;

import com.bookrecommendation.dao.UserDAO;
import com.bookrecommendation.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialog window for user sign-up.
 */
public class SignUpDialog extends JDialog {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton signUpButton;
    

    public SignUpDialog(JFrame parent) {
        super(parent, "Sign Up", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        // Input fields
        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        inputPanel.add(usernameField);

        
        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        // Sign-up button
        signUpButton = new JButton("Sign Up");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(signUpButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        signUpButton.addActionListener(this::handleSignUp);
        setVisible(true);
    }

        private void handleSignUp(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // ΜΟΝΟ username + password υποχρεωτικά
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username και password είναι υποχρεωτικά.",
                    "Missing information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();

        // Έλεγχος ΔΙΠΛΟΤΥΠΟΥ με βάση το username
        User existingUser = userDAO.getUserByUsername(username);
        if (existingUser != null) {
            JOptionPane.showMessageDialog(this,
                    "Το username χρησιμοποιείται ήδη.",
                    "Sign up error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Δημιουργία χρήστη και αποθήκευση στη DB
        User user = new User(username, password);  // ΔΕΝ ζητάμε email
        userDAO.addUser(user);
        
        JOptionPane.showMessageDialog(this,
                "Sign up ολοκληρώθηκε. Καλωσήρθες " + username + "!");

        // ΑΜΕΣΗ ΜΕΤΑΒΑΣΗ ΣΤΟ MAIN PAGE
        MainPage mainPage = new MainPage();   // ή new MainPage(user) αν έχεις τέτοιο constructor
        mainPage.setLocationRelativeTo(this);
        mainPage.setVisible(true);

        // Κλείσε το αρχικό παράθυρο (MainJFrame), αν ήταν parent
        if (getParent() instanceof MainJFrame) {
            ((MainJFrame) getParent()).dispose();
        }

        // Κλείσε και το dialog
        dispose();
    }
}