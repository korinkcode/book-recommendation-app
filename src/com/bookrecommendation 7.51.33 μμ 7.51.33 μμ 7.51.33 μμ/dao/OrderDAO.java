/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookrecommendation.dao;
import com.bookrecommendation.model.OrderOverview;
import com.bookrecommendation.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author korinakaratzioti
 */
public class OrderDAO {
    
/**
     * Επιστρέφει όλες τις παραγγελίες από το v_orders_overview,
     * ταξινομημένες από τη πιο πρόσφατη προς την παλαιότερη.
     */
    public List<OrderOverview> getAllOrdersOverview() {
        List<OrderOverview> orders = new ArrayList<>();

        String sql =
                "SELECT id, username, books, bookstore, " +
                "       total_amount, total_books, created_at " +
                "FROM v_orders_overview " +
                "ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String books = rs.getString("books");
                String bookstore = rs.getString("bookstore");
                double totalAmount = rs.getDouble("total_amount");
                int totalBooks = rs.getInt("total_books");
                Timestamp createdAt = rs.getTimestamp("created_at");

                OrderOverview order = new OrderOverview(
                        id,
                        username,
                        books,
                        bookstore,
                        totalAmount,
                        totalBooks,
                        createdAt
                );

                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
    
        // Μοντέλο εισόδου για τα items της παραγγελίας
    public static class OrderItemInput {
        private final int bookId;
        private final int quantity;
        private final double unitPrice;

        public OrderItemInput(int bookId, int quantity, double unitPrice) {
            this.bookId = bookId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public int getBookId() {
            return bookId;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }
    }
    
    public static class OrderDetails {
        private final String username;
        private final List<OrderItemInput> items;

        public OrderDetails(String username, List<OrderItemInput> items) {
            this.username = username;
            this.items = items;
        }

        public String getUsername() {
            return username;
        }

        public List<OrderItemInput> getItems() {
            return items;
        }
    }
    
        public OrderDetails getOrderDetails(int orderId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            // 1) Βρίσκουμε username
            String userSql =
                    "SELECT u.username " +
                    "FROM orders o " +
                    "JOIN users u ON u.id = o.user_id " +
                    "WHERE o.id = ?";

            String username;
            try (PreparedStatement psUser = conn.prepareStatement(userSql)) {
                psUser.setInt(1, orderId);
                try (ResultSet rs = psUser.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Δεν βρέθηκε παραγγελία με id = " + orderId);
                    }
                    username = rs.getString("username");
                }
            }

            // 2) Παίρνουμε όλα τα items της παραγγελίας
            String itemsSql =
                    "SELECT book_id, quantity, unit_price " +
                    "FROM order_items " +
                    "WHERE order_id = ?";

            List<OrderItemInput> items = new ArrayList<>();

            try (PreparedStatement psItems = conn.prepareStatement(itemsSql)) {
                psItems.setInt(1, orderId);
                try (ResultSet rs = psItems.executeQuery()) {
                    while (rs.next()) {
                        int bookId = rs.getInt("book_id");
                        int quantity = rs.getInt("quantity");
                        double unitPrice = rs.getDouble("unit_price");
                        items.add(new OrderItemInput(bookId, quantity, unitPrice));
                    }
                }
            }

            return new OrderDetails(username, items);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void deleteOrderById(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }



    /**
     * Δημιουργεί ΜΙΑ παραγγελία για τον συγκεκριμένο χρήστη με ΠΟΛΛΑ βιβλία.
     * Γράφει σε orders + order_items. Τα triggers ενημερώνουν total_amount, total_books, bookstore.
     */
    public void createOrderWithItems(String username, List<OrderItemInput> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Δεν υπάρχουν items στην παραγγελία.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1) Βρες user_id από το username
            int userId;
            try (PreparedStatement psUser = conn.prepareStatement(
                    "SELECT id FROM users WHERE username = ?")) {
                psUser.setString(1, username);
                try (ResultSet rs = psUser.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("User not found: " + username);
                    }
                    userId = rs.getInt("id");
                }
            }

            // 2) Φτιάξε την εγγραφή στην orders
            int orderId;
            try (PreparedStatement psOrder = conn.prepareStatement(
                    "INSERT INTO orders (user_id) VALUES (?) RETURNING id")) {
                psOrder.setInt(1, userId);
                try (ResultSet rs = psOrder.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to insert order.");
                    }
                    orderId = rs.getInt(1);
                }
            }

            // 3) Γράψε όλα τα items στην order_items
            String insertItemSql =
                    "INSERT INTO order_items (order_id, book_id, quantity, unit_price) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement psItem = conn.prepareStatement(insertItemSql)) {
                for (OrderItemInput item : items) {
                    psItem.setInt(1, orderId);
                    psItem.setInt(2, item.getBookId());
                    psItem.setInt(3, item.getQuantity());
                    psItem.setDouble(4, item.getUnitPrice());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            // Triggers στη βάση ενημερώνουν total_amount, total_books, bookstore
            conn.commit();

        } catch (SQLException | RuntimeException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
 
    public List<OrderOverview> getOrdersOverviewForUser(String username) {
        List<OrderOverview> orders = new ArrayList<>();

        String sql =
                "SELECT id, username, books, bookstore, " +
                "       total_amount, total_books, created_at " +
                "FROM v_orders_overview " +
                "WHERE username = ? " +
                "ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String uname = rs.getString("username");
                    String books = rs.getString("books");
                    String bookstore = rs.getString("bookstore");
                    double totalAmount = rs.getDouble("total_amount");
                    int totalBooks = rs.getInt("total_books");
                    Timestamp createdAt = rs.getTimestamp("created_at");

                    OrderOverview order = new OrderOverview(
                            id,
                            uname,
                            books,
                            bookstore,
                            totalAmount,
                            totalBooks,
                            createdAt
                    );

                    orders.add(order);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
}


