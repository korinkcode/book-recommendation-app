package com.bookrecommendation.gui;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static final String ERROR_MESSAGE = "System appearance could not be set.\nThe application will use default styling.";
    private static final String ERROR_TITLE = "Appearance Warning";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> initializeApplication());
    }

    private static void initializeApplication() {
        try {
            setSystemLookAndFeel();
            enableFontAntialiasing();
            
            // Set larger font for all components
            setGlobalFont(new Font("Arial", Font.PLAIN, 18)); // for example, 18pt

            createAndShowMainJFrame();
        } catch (Exception e) {
            handleLookAndFeelError(e);
            createAndShowMainJFrame();
        }
    }

    private static void setSystemLookAndFeel() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }

    private static void enableFontAntialiasing() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    }

    // New method to set global fonts
    private static void setGlobalFont(Font font) {
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("List.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("CheckBoxMenuItem.font", font);
        UIManager.put("RadioButtonMenuItem.font", font);
        UIManager.put("ToolTip.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("TextArea.font", font);
    }

    private static void createAndShowMainJFrame() {
        MainJFrame mainJFrame = new MainJFrame();
        mainJFrame.setLocationRelativeTo(null);
        mainJFrame.setVisible(true);
    }

    private static void handleLookAndFeelError(Exception e) {
        String errorMessage = "Failed to set system look and feel: " + e.getMessage();
        System.err.println(errorMessage);

        JOptionPane.showMessageDialog(null,
            ERROR_MESSAGE,
            ERROR_TITLE,
            JOptionPane.WARNING_MESSAGE);
    }
}