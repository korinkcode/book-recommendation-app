/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

package com.bookrecommendation.gui;

import com.bookrecommendation.util.DatabaseConnection;
import com.bookrecommendation.dao.OrderDAO;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;


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
public class NewOrder extends javax.swing.JFrame {
    private OrdersPage parent;

    private DefaultTableModel booksTableModel;

    /**
     * Creates new form NewJFrame
     */
    public NewOrder() {
        initComponents();
        initCustomLogic();
    }
    
    public NewOrder(OrdersPage parent) {
        this(); // καλεί τον default constructor
        this.parent = parent;
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
          //omorfos pin4
        styleTableHeader();  // η μέθοδος που μορφοποιεί τις επικεφαλίδες
       styleOrdersTable(); 
        // Zebra style + grid lines
        styleOrdersTable();
    }

    private void initCustomLogic() {
        makeButtonInvisible(jButton1); // Confirm
        makeButtonInvisible(jButton2); // Back

        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        setupBooksTable();
        loadUsersIntoCombo();
        loadBooksFromDatabase();
        updateTotal(); // αρχικό 0.00 €
        
        jButton1.addActionListener(evt -> jButton1ActionPerformed(evt));
        jButton2.addActionListener(evt -> jButton2ActionPerformed(evt));
    }
    
    private void makeButtonInvisible(javax.swing.JButton b) {
        b.setText("");
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
    }
    
    private void setupBooksTable() {
        booksTableModel = new DefaultTableModel(
                new Object[]{"ID", "Book", "Bookstore", "Price (€)", "Quantity"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // ΜΟΝΟ το Quantity editable (τελευταία στήλη)
                return column == 4;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 4) {
                    return Integer.class;
                } else if (columnIndex == 3) {
                    return Double.class;
                } else {
                    return String.class;
                }
            }
        };

        tblBooks.setModel(booksTableModel);

        // Κρύβουμε την στήλη ID από το UI, αλλά παραμένει στο model
        if (tblBooks.getColumnModel().getColumnCount() > 0) {
            tblBooks.getColumnModel().getColumn(0).setMinWidth(0);
            tblBooks.getColumnModel().getColumn(0).setMaxWidth(0);
            tblBooks.getColumnModel().getColumn(0).setWidth(0);
        }

        booksTableModel.addTableModelListener(e -> updateTotal());
    }

    
    private void loadUsersIntoCombo() {
        cmbUser.removeAllItems();

        String sql = "SELECT username FROM users ORDER BY username";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                cmbUser.addItem(rs.getString("username"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Σφάλμα στη φόρτωση χρηστών:\n" + e.getMessage(),
                    "Database error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void loadBooksFromDatabase() {
        booksTableModel.setRowCount(0);

        String sql = "SELECT id, title, price, bookstore " +
             "FROM books " +
             "WHERE is_deleted = FALSE AND stock > 0 " +
             "ORDER BY title";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                double price = rs.getDouble("price");
                if (rs.wasNull()) {
                    price = 0.0;
                }
                String bookstore = rs.getString("bookstore");

                // Σειρά στηλών: ID, Book, Bookstore, Price, Quantity
                booksTableModel.addRow(new Object[]{id, title, bookstore, price, 0});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Σφάλμα στη φόρτωση βιβλίων:\n" + e.getMessage(),
                    "Database error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    
    private void updateTotal() {
        double total = 0.0;

        for (int i = 0; i < booksTableModel.getRowCount(); i++) {
            Object priceObj = booksTableModel.getValueAt(i, 3); // Price (€)
            Object qtyObj   = booksTableModel.getValueAt(i, 4); // Quantity

            if (priceObj == null || qtyObj == null) {
                continue;
            }

            double price;
            int qty;

            try {
                price = (priceObj instanceof Number)
                        ? ((Number) priceObj).doubleValue()
                        : Double.parseDouble(priceObj.toString());

                qty = (qtyObj instanceof Number)
                        ? ((Number) qtyObj).intValue()
                        : Integer.parseInt(qtyObj.toString());
            } catch (NumberFormatException ex) {
                continue;
            }

            if (qty > 0) {
                total += price * qty;
            }
        }

        lblTotalValue.setText(String.format("%.2f €", total));
    }

    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // 1) Χρήστης
        String username = (String) cmbUser.getSelectedItem();
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Επίλεξε χρήστη από το combo box.");
            return;
        }

        List<OrderDAO.OrderItemInput> items = new ArrayList<>();

        // 2) Διάβασμα βιβλίων από τον πίνακα
        for (int row = 0; row < booksTableModel.getRowCount(); row++) {
            // Quantity στη στήλη 4
            Object qtyObj = booksTableModel.getValueAt(row, 4);
            int qty = 0;
            if (qtyObj instanceof Number) {
                qty = ((Number) qtyObj).intValue();
            } else if (qtyObj != null) {
                try {
                    qty = Integer.parseInt(qtyObj.toString());
                } catch (NumberFormatException ignored) {}
            }

            if (qty <= 0) {
                continue; // αγνόησε βιβλία με 0 ή λάθος quantity
            }

            // ID στη στήλη 0
            Object idObj = booksTableModel.getValueAt(row, 0);
            if (!(idObj instanceof Number)) {
                continue;
            }
            int bookId = ((Number) idObj).intValue();

            // Price στη στήλη 3
            Object priceObj = booksTableModel.getValueAt(row, 3);
            double price = 0.0;
            if (priceObj instanceof Number) {
                price = ((Number) priceObj).doubleValue();
            } else if (priceObj != null) {
                try {
                    price = Double.parseDouble(priceObj.toString());
                } catch (NumberFormatException ignored) {}
            }

            items.add(new OrderDAO.OrderItemInput(bookId, qty, price));
        }

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Επίλεξε τουλάχιστον ένα βιβλίο με quantity > 0.");
            return;
        }

        // 3) Αποθήκευση στη βάση
        OrderDAO dao = new OrderDAO();
        try {
            dao.createOrderWithItems(username, items);
            JOptionPane.showMessageDialog(this, "Η παραγγελία αποθηκεύτηκε επιτυχώς.");

            if (parent != null) {
                parent.refresh();  // ΕΔΩ ενημερώνεται ο πίνακας στο OrdersPage
            }

            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Σφάλμα κατά την αποθήκευση της παραγγελίας:\n" + ex.getMessage(),
                    "Database error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
                                        
                                        


    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        dispose();
    }                                        


    private void back() {
        dispose();
    }
    
    private static class OrderItem {
    int bookId;
    int quantity;
    double unitPrice;

    OrderItem(int bookId, int quantity, double unitPrice) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
    //omorgos pinakas1
     private void styleTableHeader() {
    JTableHeader header = tblBooks.getTableHeader();

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
        tblBooks.setDefaultEditor(Object.class, null); // read-only

        // Zebra renderer
       tblBooks.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        tblBooks.setShowGrid(true);
        tblBooks.setGridColor(Color.LIGHT_GRAY);
        tblBooks.setIntercellSpacing(new Dimension(1, 1));
        tblBooks.getTableHeader().setReorderingAllowed(false);

       tblBooks.setRowHeight(28);
       tblBooks.setFillsViewportHeight(true);

        // Αν ο πίνακας ήταν κενός, βάζουμε κάποιες κενές σειρές για οπτικό zebra
        DefaultTableModel model = (DefaultTableModel) tblBooks.getModel();
        if (model.getRowCount() == 0) {
            for (int i = 0; i < 10; i++) {
                model.addRow(new Object[]{null, null, null, null, null, null});
            }
        }
    }

  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox2 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cmbUser = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBooks = new javax.swing.JTable();
        lblTotalValue = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setText("jButton1");
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 580, 90, 40));

        jButton2.setText("jButton2");
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, 40, 30));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("STIX Two Text", 3, 20)); // NOI18N
        jLabel2.setText("Select  User :");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 80, 160, 30));

        cmbUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbUserActionPerformed(evt);
            }
        });
        getContentPane().add(cmbUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 80, 180, -1));

        tblBooks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Books", "Price €", "Quantity"
            }
        ));
        jScrollPane1.setViewportView(tblBooks);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(42, 170, 660, 340));

        lblTotalValue.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        lblTotalValue.setText("0 €");
        getContentPane().add(lblTotalValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 550, 80, 30));

        jLabel3.setFont(new java.awt.Font("Hiragino Mincho ProN", 3, 18)); // NOI18N
        jLabel3.setText("Total :");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 550, 60, 40));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bookrecommendation/images/new order.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 750, 670));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbUserActionPerformed

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
            java.util.logging.Logger.getLogger(NewOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
         java.awt.EventQueue.invokeLater(() -> {
        /* Create and display the form */
        OrdersPage ordersPage = new OrdersPage();   // πρώτα το OrdersPage
        NewOrder newOrder = new NewOrder(ordersPage);  // περάστε το στο NewOrder
        newOrder.setVisible(true);
    });
}
  


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cmbUser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTotalValue;
    private javax.swing.JTable tblBooks;
    // End of variables declaration//GEN-END:variables

}