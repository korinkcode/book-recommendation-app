package com.bookrecommendation.gui;

import com.bookrecommendation.dao.UserDAO;
import com.bookrecommendation.model.User;

import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
import java.util.List;

//omorfos pin3
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


/**
 *
 * @author lepto
 */
public class UsersPage extends javax.swing.JFrame {
    
    private MainPage parent;
    private UserDAO userDAO = new UserDAO();
    private DefaultTableModel usersTableModel;
    private List<UserDAO.UserAdminView> currentUsers;

    // Undo state
    private String lastActionType; // "ADD" ή "DELETE"
    private User lastDeletedUser;
    private Integer lastAddedUserId;

    
    /**
     * Creates new form UsersPage
     */
        /** Creates new form UsersPage */
    public UsersPage() {
        this(null);
    }

    public UsersPage(MainPage parent) {
        this.parent = parent;
        initComponents();
        initCustomLogic();
        
         //omorfos pin4
        styleTableHeader();  // η μέθοδος που μορφοποιεί τις επικεφαλίδες
       styleOrdersTable(); 
        // Zebra style + grid lines
        styleOrdersTable();
        
        
    }
    
    private void makeButtonInvisible(javax.swing.JButton b) {
        b.setText("");
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
    }
    
    private void initCustomLogic() {
        makeButtonInvisible(jButton1); // Confirm
        makeButtonInvisible(jButton2); // Back
        makeButtonInvisible(jButton3); // Confirm
        makeButtonInvisible(jButton4); // Back
        // Στήσιμο πίνακα
        usersTableModel = new DefaultTableModel(
                new Object[]{"User ID", "Username", "Password", "Last Login"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTable1.setModel(usersTableModel);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        // Κουμπιά
        

        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButton1.addActionListener(e -> addUser());
        jButton2.addActionListener(e -> deleteUser());
        jButton3.addActionListener(e -> undoLastAction());
        jButton4.addActionListener(e -> back());

        refreshUsers();
    }

    private void refreshUsers() {
    if (usersTableModel == null) {
        return;
    }

    usersTableModel.setRowCount(0);
    currentUsers = userDAO.getAllUsersAdmin();

    for (UserDAO.UserAdminView u : currentUsers) {

        // 🔍 DEBUG – δες τι φέρνει το DAO
        System.out.println(
            "USER: " + u.getUsername() +
            " | lastLogin = " + u.getLastLogin()
        );

        Object[] row = new Object[]{
            u.getId(),
            u.getUsername(),
            u.getPasswordHash(),
            u.getLastLogin() != null ? u.getLastLogin().toString() : ""
        };

        usersTableModel.addRow(row);
    }
}


    private void addUser() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel panel = new JPanel(new java.awt.GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add user",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username και password είναι υποχρεωτικά.",
                    "Missing information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Έλεγχος αν υπάρχει ήδη
        User existing = userDAO.getUserByUsername(username);
        if (existing != null) {
            JOptionPane.showMessageDialog(this,
                    "Το username χρησιμοποιείται ήδη.",
                    "Add user error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User newUser = new User(username, password);
        userDAO.addUser(newUser); // γεμίζει και το id

        lastActionType = "ADD";
        lastAddedUserId = newUser.getId();
        lastDeletedUser = null;

        refreshUsers();
    }

     //omorgos pinakas1
     private void styleTableHeader() {
    JTableHeader header = jTable1.getTableHeader();

    // Font: έντονο και μεγαλύτερο
    header.setFont(new Font("Tahoma", Font.BOLD, 14));

    // Κεντράρισμα κειμένου
    ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

    // Χρώμα φόντου και γραμμής κεφαλίδας
    header.setBackground(new Color(240,200,210)); // ανοιχτό γκρι
    header.setForeground(Color.BLACK);
    header.setOpaque(true);
     //ordersTable.getTableHeader().setBackground(new java.awt.Color(240,200,210));
    // Ξεχωριστή γραμμή κάτω από το header
    header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
}
     //omorgos pinakas2
      private void styleOrdersTable() {
        jTable1.setDefaultEditor(Object.class, null); // read-only

        // Zebra renderer
       jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
         // Grid lines
        jTable1.setShowGrid(true);
        jTable1.setGridColor(Color.LIGHT_GRAY);
        jTable1.setIntercellSpacing(new Dimension(1, 1));
        jTable1.getTableHeader().setReorderingAllowed(false);

       jTable1.setRowHeight(28);
       jTable1.setFillsViewportHeight(true);

        // Αν ο πίνακας ήταν κενός, βάζουμε κάποιες κενές σειρές για οπτικό zebra
       
    }
    
    
    
    private void deleteUser() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Επέλεξε πρώτα έναν χρήστη.");
            return;
        }
        if (currentUsers == null || selectedRow >= currentUsers.size()) {
            JOptionPane.showMessageDialog(this, "Εσωτερικό σφάλμα: δεν βρέθηκε ο χρήστης.");
            return;
        }

        UserDAO.UserAdminView selected = currentUsers.get(selectedRow);

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Να διαγραφεί ο χρήστης '" + selected.getUsername() + "';",
                "Επιβεβαίωση διαγραφής",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        // Πάρε πλήρες User για UNDO (με password_hash)
        User fullUser = userDAO.getUserByUsername(selected.getUsername());

        userDAO.deleteUserById(selected.getId());

        lastActionType = "DELETE";
        lastDeletedUser = fullUser;
        lastAddedUserId = null;

        refreshUsers();
    }

    private void undoLastAction() {
        if (lastActionType == null) {
            JOptionPane.showMessageDialog(this, "Δεν υπάρχει κίνηση για αναίρεση.");
            return;
        }

        if ("ADD".equals(lastActionType)) {
            if (lastAddedUserId == null) {
                JOptionPane.showMessageDialog(this, "Δεν βρέθηκε χρήστης για αναίρεση προσθήκης.");
                return;
            }

            userDAO.deleteUserById(lastAddedUserId);

            lastActionType = null;
            lastAddedUserId = null;
            lastDeletedUser = null;

            refreshUsers();
        } else if ("DELETE".equals(lastActionType)) {
            if (lastDeletedUser == null) {
                JOptionPane.showMessageDialog(this, "Δεν βρέθηκε χρήστης για αναίρεση διαγραφής.");
                return;
            }

            userDAO.addUser(lastDeletedUser);

            lastActionType = null;
            lastAddedUserId = null;
            lastDeletedUser = null;

            refreshUsers();
        }
    }

    private void back() {
        if (parent != null) {
            parent.setLocationRelativeTo(this);
            parent.setVisible(true);
        }
        this.dispose();
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
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setText("jButton1");
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 660, 100, 40));

        jButton2.setText("jButton2");
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 660, 110, 40));

        jButton3.setText("jButton3");
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 660, 130, 40));

        jButton4.setText("jButton4");
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 30, 50, 40));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 180, 920, 320));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bookrecommendation/images/users.png"))); // NOI18N
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
            java.util.logging.Logger.getLogger(UsersPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UsersPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UsersPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UsersPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UsersPage().setVisible(true);
            }
        });
    }
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
