/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.bookrecommendation.gui;

  

import com.bookrecommendation.dao.LogDAO;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class LogFile extends javax.swing.JFrame {

    private MainPage parent;
    private final LogDAO logDAO = new LogDAO();
    private DefaultTableModel logTableModel;
    private List<LogDAO.LogEntry> currentLogs;

    public LogFile() { this(null); }

    public LogFile(MainPage parent) {
        this.parent = parent;
        initComponents();
        initCustomLogic();

        // Μην σκοτώνεις όλη την εφαρμογή όταν κλείσει αυτό το window
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initCustomLogic() {
        makeButtonInvisible(jButton1); // refresh
        makeButtonInvisible(jButton2); // back

        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        logTableModel = new DefaultTableModel(
                new Object[]{"ID", "TIME", "USER", "TYPE", "DETAILS"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        ordersTable.setModel(logTableModel);

        styleTableHeader();
        styleOrdersTable();

        jButton1.addActionListener(e -> refreshLogs());
        jButton2.addActionListener(e -> back());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                back();
            }
        });

        refreshLogs();
    }

    private void refreshLogs() {
        if (logTableModel == null) return;

        logTableModel.setRowCount(0);
        currentLogs = logDAO.getAllLogs();

        if (currentLogs == null || currentLogs.isEmpty()) return;

        for (LogDAO.LogEntry l : currentLogs) {
            logTableModel.addRow(new Object[]{
                    l.getId(),
                    l.getTime() != null ? l.getTime().toString() : "",
                    l.getUsername() != null ? l.getUsername() : "",
                    l.getType() != null ? l.getType() : "",
                    l.getDetails() != null ? l.getDetails() : ""
            });
        }
    }

    private void back() {
        if (parent != null) {
            parent.setLocationRelativeTo(this);
            parent.setVisible(true);
        } else {
            try {
                MainPage start = new MainPage();
                start.setLocationRelativeTo(this);
                start.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Δεν μπόρεσε να ανοίξει το MainPage.");
            }
        }
        this.dispose();
    }

    private void makeButtonInvisible(javax.swing.JButton b) {
        b.setText("");
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
    }

    private void styleTableHeader() {
        JTableHeader header = ordersTable.getTableHeader();
        header.setFont(new Font("Tahoma", Font.BOLD, 14));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        header.setBackground(new Color(240,200,210));
        header.setForeground(Color.BLACK);
        header.setOpaque(true);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
    }

    private void styleOrdersTable() {
        ordersTable.setDefaultEditor(Object.class, null);

        ordersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    setBackground(new Color(51, 153, 255));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(230, 230, 230));
                    setForeground(Color.BLACK);
                }

                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });

        ordersTable.setShowGrid(true);
        ordersTable.setGridColor(Color.LIGHT_GRAY);
        ordersTable.setIntercellSpacing(new Dimension(1, 1));
        ordersTable.getTableHeader().setReorderingAllowed(false);

        ordersTable.setRowHeight(28);
        ordersTable.setFillsViewportHeight(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ordersTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setText("jButton1");
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 640, 130, 50));

        jButton2.setText("jButton2");
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 20, 50, 40));

        ordersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(ordersTable);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 120, 1080, 470));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bookrecommendation/images/LogFile.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1210, 740));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LogFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LogFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LogFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LogFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LogFile().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable ordersTable;
    // End of variables declaration//GEN-END:variables
}
