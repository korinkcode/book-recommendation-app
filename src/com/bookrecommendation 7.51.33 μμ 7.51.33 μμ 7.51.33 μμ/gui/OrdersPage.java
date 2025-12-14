/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.bookrecommendation.gui;
import com.bookrecommendation.model.User;
import com.bookrecommendation.dao.OrderDAO;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import com.bookrecommendation.model.OrderOverview;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.util.List;
import javax.swing.JOptionPane;

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
public class OrdersPage extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */

    private MainPage parent;
    private OrderDAO orderDAO = new OrderDAO();
    private DefaultTableModel ordersTableModel;
    private JTable ordersTable;

    private List<OrderOverview> currentOrders;
    private String lastDeletedUsername;
    private List<OrderDAO.OrderItemInput> lastDeletedItems; 
    
    
    public OrdersPage(MainPage parent) {
        this.parent = parent;
        initComponents();
        initCustomLogic();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);   // Να εμφανίζεται στο κέντρο
        setVisible(true);   
        
         //omorfos pin4
        styleTableHeader();  // η μέθοδος που μορφοποιεί τις επικεφαλίδες
       styleOrdersTable(); 
        // Zebra style + grid lines
        styleOrdersTable();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
                if (OrdersPage.this.parent != null) {
                    OrdersPage.this.parent.setVisible(true);
                }
            }
        });

    }
    
    public OrdersPage(){
        this(null);
    }
    
    private void initCustomLogic() {
        
        makeButtonInvisible(jButton1);// new order
        makeButtonInvisible(jButton2); // delete 
        makeButtonInvisible(jButton3); // refresh
        makeButtonInvisible(jButton4);// back 
       
        
        // Hand cursor
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
       
        
        jButton1.addActionListener(e -> newOrder());
        jButton2.addActionListener(e -> delete());
        jButton3.addActionListener(e -> undoLastAction());
        jButton4.addActionListener(e -> back()); 
        
        
        initOrdersTable();
        refresh();

    }
    
        private void initOrdersTable() {
        String[] columnNames = {
                "Order ID",
                "User",
                "Quantity",
                "Total €",
                "Date"
        };

        ordersTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Δεν θέλουμε edit μέσα από την OrdersPage
                return false;
            }
        };

        ordersTable = new JTable(ordersTableModel);

        JScrollPane scrollPane = new JScrollPane(ordersTable);

        // Καθαρίζουμε τυχόν tabs και βάζουμε ένα με τον πίνακα παραγγελιών
        jTabbedPane1.removeAll();
        jTabbedPane1.addTab("Orders", scrollPane);
    }

    
    private void makeButtonInvisible(javax.swing.JButton b) {
        b.setText("");
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
    }
    
    private void newOrder() {
        NewOrder win = new NewOrder(this);
        win.setLocationRelativeTo(this);
        win.setVisible(true);
    }

    private void delete() {
        if (ordersTable == null || ordersTableModel == null) {
            return;
        }

        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Επέλεξε πρώτα μία παραγγελία.");
            return;
        }

        if (currentOrders == null || selectedRow >= currentOrders.size()) {
            JOptionPane.showMessageDialog(this, "Εσωτερικό σφάλμα: δεν βρέθηκε η επιλεγμένη παραγγελία.");
            return;
        }

        OrderOverview selectedOrder = currentOrders.get(selectedRow);
        int orderId = selectedOrder.getId();

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Να διαγραφεί η παραγγελία #" + orderId + ";",
                "Επιβεβαίωση διαγραφής",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Κρατάμε στοιχεία για UNDO
            OrderDAO.OrderDetails details = orderDAO.getOrderDetails(orderId);
            lastDeletedUsername = details.getUsername();
            lastDeletedItems = details.getItems();

            // Διαγραφή από DB
            orderDAO.deleteOrderById(orderId);

            // Επαναφόρτωση πίνακα
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Σφάλμα κατά τη διαγραφή της παραγγελίας:\n" + ex.getMessage(),
                    "Database error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

     
    void refresh() {
        if (ordersTableModel == null) {
            return;
        }

        // Καθαρίζουμε τον πίνακα
        ordersTableModel.setRowCount(0);

        // Φέρνουμε όλα τα orders από το DAO
        currentOrders = orderDAO.getAllOrdersOverview();

        for (OrderOverview order : currentOrders) {
            Object[] row = new Object[] {
                    order.getId(),
                    order.getUsername(),
                    order.getTotalBooks(),
                    String.format("%.2f €", order.getTotalAmount()),
                    order.getCreatedAt()
            };

            ordersTableModel.addRow(row);
        }
    }

    private void undoLastAction() {
        // UNDO ΤΕΛΕΥΤΑΙΑΣ ΔΙΑΓΡΑΦΗΣ
        if (lastDeletedUsername == null || lastDeletedItems == null || lastDeletedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Δεν υπάρχει κίνηση για αναίρεση.");
            return;
        }

        try {
            // Ξαναφτιάχνουμε την παραγγελία
            orderDAO.createOrderWithItems(lastDeletedUsername, lastDeletedItems);

            // Καθαρισμός buffer UNDO
            lastDeletedUsername = null;
            lastDeletedItems = null;

            // Επαναφόρτωση πίνακα
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Σφάλμα κατά την αναίρεση της τελευταίας κίνησης:\n" + ex.getMessage(),
                    "Database error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

     
     
     // ΤΙ ΚΑΝΕΙ ΤΟ ΒΕΛΑΚΙΙΙΙΙ
    private void back() {
        if (parent != null) {
            parent.setLocationRelativeTo(this);
            parent.setVisible(true);
        } else {
            // fallback αν άνοιξε standalone
            MainJFrame start = new MainJFrame();
            start.setLocationRelativeTo(this);
            start.setVisible(true);
        }
        this.dispose();
    }

    //omorgos pinakas1
     private void styleTableHeader() {
    JTableHeader header = ordersTable.getTableHeader();

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
        ordersTable.setDefaultEditor(Object.class, null); // read-only

        // Zebra renderer
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
         // Grid lines
        ordersTable.setShowGrid(true);
        ordersTable.setGridColor(Color.LIGHT_GRAY);
        ordersTable.setIntercellSpacing(new Dimension(1, 1));
        ordersTable.getTableHeader().setReorderingAllowed(false);

       ordersTable.setRowHeight(28);
       ordersTable.setFillsViewportHeight(true);

        // Αν ο πίνακας ήταν κενός, βάζουμε κάποιες κενές σειρές για οπτικό zebra
        DefaultTableModel model = (DefaultTableModel) ordersTable.getModel();
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

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setText("jButton1");
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 650, 140, 50));

        jButton2.setText("jButton2");
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 650, 110, 50));

        jButton3.setText("jButton3");
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 650, 130, 50));

        jButton4.setText("jButton4");
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 20, 50, 40));

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

        jTabbedPane1.addTab("tab1", jScrollPane1);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 171, 1030, 390));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bookrecommendation/images/orders.png"))); // NOI18N
        jLabel2.setText("jLabel2");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1210, 740));

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
            java.util.logging.Logger.getLogger(OrdersPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrdersPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrdersPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrdersPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OrdersPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
