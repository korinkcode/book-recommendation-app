/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.bookrecommendation.gui;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;

import com.bookrecommendation.dao.BookDAO;
import com.bookrecommendation.model.Book;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

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
public class BookPage extends javax.swing.JFrame {
    
    private MainPage parent;
    private DefaultTableModel tableModel;
    private final BookDAO bookDAO = new BookDAO();
    private List<Book> currentBooks = new ArrayList<>();
    private boolean isUpdatingFromCode = false;
    
    //Refresh button 
    private enum LastActionType { NONE, UPDATE, DELETE }

    private LastActionType lastAction = LastActionType.NONE;
    private Book lastBookSnapshot = null;
    
    // === HELPER METHOD ΓΙΑ ΑΝΤΙΓΡΑΦΗ ΒΙΒΛΙΟΥ ===
   
    public BookPage(MainPage parent) {
        this.parent = parent;
        initComponents();
        initCustomLogic();
        initTextFields();
        
         //omorfos pin4
        styleTableHeader();  // η μέθοδος που μορφοποιεί τις επικεφαλίδες
       styleOrdersTable(); 
        // Zebra style + grid lines
        styleOrdersTable();
    }
    
      public BookPage() {
        this(null);
    }
    
    /**
     * Creates new form BookPage
     */
     private void initCustomLogic() {
        // Κάνε τα κουμπιά “αόρατα” πάνω στην εικόνα
        makeButtonInvisible(jButton1); // Add
        makeButtonInvisible(jButton2); // Update
        makeButtonInvisible(jButton3); // Delete
        makeButtonInvisible(jButton4); // Refresh
        makeButtonInvisible(jButton5);//Back
        


        // Αν θες hand cursor:
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        jButton5.addActionListener(e -> back());
        jButton1.addActionListener(e -> addBookFromFields());
        jButton2.addActionListener(e -> updateSelectedBook());
        jButton3.addActionListener(e -> deleteSelectedBook());
        jButton4.addActionListener(e -> undoLastAction());
        
            
        // Table model για τα βιβλία
         tableModel = new DefaultTableModel(
                new Object[]{"Title", "Category", "Price", "Bookstore", "Stock"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // ΔΕΝ επιτρέπουμε edit στο Category (στήλη 2),
                // γιατί προκύπτει από moods (join) και όχι από απλή στήλη.
                return true;
            }
        };

        booksTable.setModel(tableModel);
        // Renderer για στήλη Price με σύμβολο €
        DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value == null) {
                    setText("");
                } else if (value instanceof Number) {
                    double d = ((Number) value).doubleValue();
                    setText(String.format("%.2f €", d));
                } else {
                    // fallback: αν ήρθε String, δοκίμασε να το κάνεις number
                    String s = value.toString().replace("€", "").trim();
                    try {
                        double d = Double.parseDouble(s);
                        setText(String.format("%.2f €", d));
                    } catch (NumberFormatException ex) {
                        setText(value.toString());
                    }
                }
            }
        };
        // δεξιά στοίχιση
        priceRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        // column index 3 = "Price"
        booksTable.getColumnModel().getColumn(2).setCellRenderer(priceRenderer);



        // Φόρτωμα δεδομένων από DB
        loadBooksIntoTable();

        // Listener για inline edits
        tableModel.addTableModelListener(e -> {
            if (isUpdatingFromCode) return;

            int row = e.getFirstRow();
            int column = e.getColumn();
            if (row < 0 || column < 0) return;
            if (row >= currentBooks.size()) return;

            Book book = currentBooks.get(row);
            Object value = tableModel.getValueAt(row, column);

            switch (column) {
                    case 0: // Title
                        book.setTitle(String.valueOf(value));
                        break;

                    case 1: // Category
                        book.setCategory(String.valueOf(value));
                        break;

                    case 2: // Price
                        double price;
                        try {
                            String s = String.valueOf(value).replace("€", "").trim();
                            price = s.isEmpty() ? 0.0 : Double.parseDouble(s);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Η τιμή πρέπει να είναι αριθμός (π.χ. 12.50).",
                                    "Λάθος τιμή",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            isUpdatingFromCode = true;
                            tableModel.setValueAt(book.getPrice(), row, column);
                            isUpdatingFromCode = false;
                            return;
                        }
                        book.setPrice(price);
                        break;

                    case 3: // Bookstore
                        book.setBookstore(String.valueOf(value));
                        break;

                    case 4: // Stock
                        int stock;
                        try {
                            String s = String.valueOf(value).trim();
                            stock = s.isEmpty() ? 0 : Integer.parseInt(s);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Το stock πρέπει να είναι ακέραιος αριθμός.",
                                    "Λάθος stock",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            isUpdatingFromCode = true;
                            tableModel.setValueAt(book.getStock(), row, column);
                            isUpdatingFromCode = false;
                            return;
                        }
                        book.setStock(stock);
                        break;

                    default:
                        return;
            }


            // Σπρώξε τις αλλαγές στη βάση
                //bookDAO.updateBookInline(book);
        });

        // Διαγραφή με το πλήκτρο Delete
        booksTable.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                    deleteSelectedBook();
                }
            }
        });

          
    }
    
     
     
    private void back() {
        if (parent != null) {
            parent.setLocationRelativeTo(this);
            parent.setVisible(true);
        } else {
            // fallback, αν άνοιξε χωρίς parent
            MainPage main = new MainPage();
            main.setLocationRelativeTo(this);
            main.setVisible(true);
        }
        this.dispose();
    }


   private void deleteSelectedBook() {
        // Αν κάποιος επεξεργάζεται κελί, κάνε commit πρώτα
        if (booksTable.isEditing()) {
            booksTable.getCellEditor().stopCellEditing();
        }

        int row = booksTable.getSelectedRow();

        if (row < 0 || row >= currentBooks.size()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Επέλεξε πρώτα μια γραμμή για διαγραφή.",
                    "No selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Book selected = currentBooks.get(row);

        int result = JOptionPane.showConfirmDialog(
                this,
                "Να διαγραφεί σίγουρα το βιβλίο \"" + selected.getTitle() + "\";",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {

            // === UNDO SNAPSHOT ΓΙΑ DELETE ===
            lastAction = LastActionType.DELETE;

            // Πάλι, ΠΟΤΕ new Book() σκέτο.
            lastBookSnapshot = new Book(
                    selected.getId(),
                    selected.getTitle(),
                    selected.getAuthor(),
                    selected.getDescription()
            );
            lastBookSnapshot.setCategory(selected.getCategory());
            lastBookSnapshot.setPrice(selected.getPrice());
            lastBookSnapshot.setBookstore(selected.getBookstore());
            lastBookSnapshot.setStock(selected.getStock());

            // Διαγραφή από DB
            BookDAO.DeleteResult res = bookDAO.deleteBook(selected.getId());

            if (res == BookDAO.DeleteResult.FAILED) {
                JOptionPane.showMessageDialog(this,
                        "Αποτυχία διαγραφής βιβλίου (DB error).",
                        "Delete failed",
                        JOptionPane.ERROR_MESSAGE);
            } else if (res == BookDAO.DeleteResult.DISABLED) {
                JOptionPane.showMessageDialog(this,
                        "Το βιβλίο υπάρχει σε παραγγελίες, άρα δεν σβήνεται.\n" +
                        "Έγινε απενεργοποίηση (soft delete) και μηδενίστηκε το stock.",
                        "Soft delete",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            loadBooksIntoTable();

        }
    }

     
    private void makeButtonInvisible(javax.swing.JButton b) {
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        // Κάνει το κείμενο διάφανο αλλά ΔΕΝ το σβήνει -> κρατάει το σωστό μέγεθος
        b.setForeground(new java.awt.Color(0, 0, 0, 0));
    }
    
        private void loadBooksIntoTable() {
        isUpdatingFromCode = true;

        tableModel.setRowCount(0);
        currentBooks.clear();

        List<Book> books = bookDAO.getAllBooksOverview();

        for (Book b : books) {
            currentBooks.add(b);
            tableModel.addRow(new Object[]{
                    b.getTitle(),
                    b.getCategory(),   // moods merged
                    b.getPrice(),
                    b.getBookstore(),
                    b.getStock()
            });
        }

        isUpdatingFromCode = false;
    }
        
    private void initTextFields() {
        setupPlaceholder(jTextField1, "Books", new Color(255, 182, 193));
        setupPlaceholder(jTextField2, "Category", new Color(255, 182, 193));
        setupPlaceholder(jTextField3, "Price €", new Color(255, 182, 193));
        setupPlaceholder(jTextField4, "Store", new Color(255, 182, 193));
        setupPlaceholder(jTextField5, "Stock", new Color(255, 182, 193));
    }

    private void setupPlaceholder(JTextField field, String placeholder, Color color) {
        field.setForeground(color);
        field.setText(placeholder);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if(field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if(field.getText().isEmpty()) {
                    field.setForeground(color);
                    field.setText(placeholder);
                }
            }
        });
    }
    
    private void addBookFromFields() {
            // Βγάζουμε placeholders
            String title = jTextField1.getText().trim();
            if ("Books".equals(title)) title = "";

            String category = jTextField2.getText().trim();
            if ("Category".equals(category)) category = "";

            String priceStr = jTextField3.getText().trim();
            if ("Price €".equals(priceStr)) priceStr = "";

            String bookstore = jTextField4.getText().trim();
            if ("Store".equals(bookstore)) bookstore = "";

            String stockStr = jTextField5.getText().trim();
            if ("Stock".equals(stockStr)) stockStr = "";

            // Έλεγχος ότι όλα είναι συμπληρωμένα
            if (title.isEmpty() || category.isEmpty() ||
                priceStr.isEmpty() || bookstore.isEmpty() || stockStr.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Συμπλήρωσε όλα τα πεδία (Books / Genre / Price / Store / Stock).",
                        "Ελλιπή στοιχεία",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Μετατροπή Price / Stock
            double price;
            int stock;
            try {
                price = Double.parseDouble(
                    priceStr.replace("€", "").replace(",", ".").trim()
                );
                stock = Integer.parseInt(stockStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Βάλε σωστούς αριθμούς για Price και Stock.",
                        "Λάθος τιμές",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Κλήση στο DAO -> INSERT σε books + moods + book_moods
            bookDAO.addBookWithDetails(title, category, price, bookstore, stock);

            // Refresh του πίνακα από τη βάση
            loadBooksIntoTable();

            // Reset placeholders
            initTextFields();

            JOptionPane.showMessageDialog(
                    this,
                    "Το βιβλίο προστέθηκε με επιτυχία στη βάση.",
                    "Επιτυχία",
                    JOptionPane.INFORMATION_MESSAGE
            );
    }


    private void updateSelectedBook() {

        // Commit τυχόν ενεργό editing
        if (booksTable.isEditing()) {
            booksTable.getCellEditor().stopCellEditing();
        }

        int row = booksTable.getSelectedRow();

        if (row < 0 || row >= currentBooks.size()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a row to update.",
                    "No selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Book book = currentBooks.get(row);

        // === UNDO SNAPSHOT ΓΙΑ UPDATE ===
        Book originalFromDb = bookDAO.getBookById(book.getId());
        if (originalFromDb != null) {
            lastAction = LastActionType.UPDATE;

            // ΔΕΝ καλούμε new Book() σκέτο. Παίρνει ΠΑΝΤΑ 4 args.
            lastBookSnapshot = new Book(
                    originalFromDb.getId(),
                    originalFromDb.getTitle(),
                    originalFromDb.getAuthor(),
                    originalFromDb.getDescription()
            );
            lastBookSnapshot.setCategory(originalFromDb.getCategory());
            lastBookSnapshot.setPrice(originalFromDb.getPrice());
            lastBookSnapshot.setBookstore(originalFromDb.getBookstore());
            lastBookSnapshot.setStock(originalFromDb.getStock());
        } else {
            lastAction = LastActionType.NONE;
            lastBookSnapshot = null;
        }

        // Σπρώξε τις τωρινές τιμές στη βάση
        bookDAO.updateBookInline(book);

        // Reload από DB
        loadBooksIntoTable();

        JOptionPane.showMessageDialog(
                this,
                "Book updated successfully.",
                "Update",
                JOptionPane.INFORMATION_MESSAGE
        );
    }





    private void undoLastAction() {
        if (lastAction == LastActionType.NONE || lastBookSnapshot == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Δεν υπάρχει κάποια ενέργεια για αναίρεση.",
                    "Undo",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        switch (lastAction) {
            case UPDATE:
                bookDAO.updateBookInline(lastBookSnapshot);
                break;

            case DELETE:
                bookDAO.addBookWithDetails(
                        lastBookSnapshot.getTitle(),
                        lastBookSnapshot.getCategory(),
                        lastBookSnapshot.getPrice(),
                        lastBookSnapshot.getBookstore(),
                        lastBookSnapshot.getStock()
                );
                break;
        }

        lastAction = LastActionType.NONE;
        lastBookSnapshot = null;

        loadBooksIntoTable();
    }
    
    
    //omorgos pinakas1
     private void styleTableHeader() {
    JTableHeader header = booksTable.getTableHeader();

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
        booksTable.setDefaultEditor(Object.class, null); // read-only

        // Zebra renderer
       booksTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        booksTable.setShowGrid(true);
        booksTable.setGridColor(Color.LIGHT_GRAY);
        booksTable.setIntercellSpacing(new Dimension(1, 1));
        booksTable.getTableHeader().setReorderingAllowed(false);

       booksTable.setRowHeight(28);
       booksTable.setFillsViewportHeight(true);

        // Αν ο πίνακας ήταν κενός, βάζουμε κάποιες κενές σειρές για οπτικό zebra
        DefaultTableModel model = (DefaultTableModel) booksTable.getModel();
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

        jButton4 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        booksTable = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton4.setText("jButton4");
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 640, 120, 50));

        jButton2.setText("jButton2");
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 640, 110, 50));

        jButton3.setText("jButton3");
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 640, 110, 50));

        jButton5.setText("jButton5");
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 20, 50, 40));
        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 250, -1, -1));

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 640, 80, 50));
        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 260, -1, -1));

        booksTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "BOOKS", "Title 2", "Title 3", "Title 4"
            }
        ));
        booksTable.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                booksTableComponentShown(evt);
            }
        });
        jScrollPane2.setViewportView(booksTable);

        jScrollPane4.setViewportView(jScrollPane2);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 1020, 410));

        jTextField1.setText("jTextField1");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 650, 110, 30));

        jTextField2.setText("jTextField2");
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 650, 100, 30));

        jTextField3.setText("jTextField3");
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 650, 70, 30));

        jTextField4.setText("jTextField4");
        getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 650, 230, 30));

        jTextField5.setText("jTextField5");
        getContentPane().add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 650, 60, 30));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bookrecommendation/images/books.png"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1210, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void booksTableComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_booksTableComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_booksTableComponentShown

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

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
            java.util.logging.Logger.getLogger(BookPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BookPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BookPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BookPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BookPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable booksTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables

  

   
}
