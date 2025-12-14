    package com.bookrecommendation.gui;

import com.bookrecommendation.dao.OrderDAO;
import com.bookrecommendation.model.OrderOverview;

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
public class MyOrdersPage extends javax.swing.JFrame {
    private MainPage parent;
    private String username;
    private OrderDAO orderDAO = new OrderDAO();
    private DefaultTableModel ordersTableModel;


    /**
     * Creates new form MyOrdersPage
     */
    /** Creates new form MyOrdersPage */
    public MyOrdersPage() {
        this(null, null);
    }

    public MyOrdersPage(MainPage parent, String username) {
        this.parent = parent;
        this.username = username;
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
        
        
        makeButtonInvisible(jButton1); // Back
        // Κουμπί back
       
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(e -> back());

        // Στήσιμο πίνακα
        ordersTableModel = new DefaultTableModel(
                 new Object[]{"Order ID", "User", "Quantity", "Total €", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable.setModel(ordersTableModel);
        ordersTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        // Φόρτωση παραγγελιών για τον συγκεκριμένο user
        if (username != null && !username.trim().isEmpty()) {
            refresh();
        }
    }

    private void back() {
        if (parent != null) {
            parent.setLocationRelativeTo(this);
            parent.setVisible(true);
        }
        this.dispose();
    }

    private void refresh() {
        if (ordersTableModel == null || username == null || username.trim().isEmpty()) {
            return;
        }

        ordersTableModel.setRowCount(0);
        List<OrderOverview> orders = orderDAO.getOrdersOverviewForUser(username);

        for (OrderOverview order : orders) {
            Object[] row = new Object[]{
                    order.getId(),
                    order.getUsername(),
                    order.getTotalBooks(),
                    String.format("%.2f €", order.getTotalAmount()),
                    order.getCreatedAt()   // ή order.getCreatedAt().toString()
            };
            ordersTableModel.addRow(row);
        }
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
        jScrollPane1 = new javax.swing.JScrollPane();
        ordersTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setText("jButton1");
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 20, 60, 40));

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
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(ordersTable);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, 1120, 330));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bookrecommendation/images/my orders.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1210, 730));

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
            java.util.logging.Logger.getLogger(MyOrdersPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MyOrdersPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MyOrdersPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MyOrdersPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MyOrdersPage().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable ordersTable;
    // End of variables declaration//GEN-END:variables
}
