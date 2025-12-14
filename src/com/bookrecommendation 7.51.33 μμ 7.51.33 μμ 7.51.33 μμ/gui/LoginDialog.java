package com.bookrecommendation.gui;

import com.bookrecommendation.dao.UserDAO;
import com.bookrecommendation.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog window for user login.
 * Prompts the user for username and password,
 * verifies the credentials, and sets the current user in the MainFrame on success.
 */
public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;

    public LoginDialog(JFrame parent) {
        super(parent, "Log In", true); // Modal dialog
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Create input panel for username and password
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        // Username Label and Field
        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        inputPanel.add(usernameField);

        // Password Label and Field
        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        // Create buttons
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());

        // Make the dialog visible
        this.setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Both username and password are required.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByUsername(username);

        if (user != null && user.verifyPassword(password)) {
            
            //Kanei Update to last logIn tou user
            userDAO.updateLastLogin(user.getId());

            // Login successful
            JOptionPane.showMessageDialog(this, "Login successful!");

            // Άνοιγμα MainPage ΜΕ τον user
            MainPage mainPage = new MainPage(user);
            mainPage.setLocationRelativeTo(this);
            mainPage.setVisible(true);

            // Κλείσε το αρχικό παράθυρο (MainJFrame) αν είναι parent
            if (getParent() instanceof MainJFrame) {
                ((MainJFrame) getParent()).dispose();
            }

            // Κλείσε το dialog
            this.dispose();
        }else {
            // Login failed
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
