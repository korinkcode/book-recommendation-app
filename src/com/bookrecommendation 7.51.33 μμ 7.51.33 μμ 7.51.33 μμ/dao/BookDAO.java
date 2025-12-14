package com.bookrecommendation.dao;

import com.bookrecommendation.model.Book;
import com.bookrecommendation.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
/**
 * Data Access Object for Book-related database operations.
 */
public class BookDAO {

    // Retrieves books associated with a specific mood
    public List<Book> getBooksByMood(String moodName) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, category, price, bookstore, stock " +
                     "FROM v_books_overview " +
                     "ORDER BY title";

         try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String category = rs.getString("category");
                double price = rs.getDouble("price");
                if (rs.wasNull()) {
                    price = 0.0;
                }
                String bookstore = rs.getString("bookstore");
                int stock = rs.getInt("stock");

                Book book = new Book(id, title, author, ""); // description δεν μας νοιάζει εδώ
                book.setCategory(category);
                book.setPrice(price);
                book.setBookstore(bookstore);
                book.setStock(stock);

                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // Retrieves ALL books from the database (ΧΩΡΙΣ comments για να μην βαράει πολλά queries)
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT id, title, author, description, rating FROM books WHERE is_deleted = FALSE";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String description = rs.getString("description");
                int rating = rs.getInt("rating");

                Book book = new Book(id, title, author, description);
                book.setRating(rating);

                // ΔΕΝ φορτώνουμε comments εδώ για να μην κάνουμε 1 query ανά βιβλίο
                // Αν χρειαστείς comments σε άλλη οθόνη, θα τα ζητήσεις ξεχωριστά

                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }
    
    
    public Book getBookById(int id) {
        String sql = "SELECT id, title, description, category, price, bookstore, stock " +
                     "FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int bookId      = rs.getInt("id");
                    String title    = rs.getString("title");
                    String desc     = rs.getString("description");
                    String category = rs.getString("category");

                    double price = rs.getDouble("price");
                    if (rs.wasNull()) {
                        price = 0.0;
                    }

                    String bookstore = rs.getString("bookstore");
                    int stock        = rs.getInt("stock");

                    Book b = new Book(bookId, title, "", desc); // author κενό
                    b.setCategory(category);
                    b.setPrice(price);
                    b.setBookstore(bookstore);
                    b.setStock(stock);

                    // Αν θέλεις και comments:
                    for (String c : getBookComments(bookId)) {
                        b.addComment(c);
                    }

                    return b;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    
    
        // Προσθήκη νέου βιβλίου (η στήλη rating χρησιμοποιεί το default της DB)
    public void addBook(String title, String author, String description) {
        String query = "INSERT INTO books (title, author, description) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, description);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ενημέρωση υπάρχοντος βιβλίου (χωρίς να πειράζουμε το rating)
        // Ενημερώνει βασικά στοιχεία βιβλίου (ΧΩΡΙΣ να πειράζει τα moods / category)
    public void updateBookInline(Book book) {
        String sql = "UPDATE books " +
                     "SET title = ?, description = ?, category = ?, price = ?, bookstore = ?, stock = ? " +
                     "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getDescription());
            pstmt.setString(3, book.getCategory());

            if (book.getPrice() == 0.0) {
                pstmt.setNull(4, java.sql.Types.NUMERIC);
            } else {
                pstmt.setDouble(4, book.getPrice());
            }

            pstmt.setString(5, book.getBookstore());
            pstmt.setInt(6, book.getStock());
            pstmt.setInt(7, book.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBookRating(int id, int selectedRating) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }



    // Διαγραφή βιβλίου
    public enum DeleteResult { DELETED, DISABLED, FAILED }

    public DeleteResult deleteBook(int id) {
        String checkSql = "SELECT 1 FROM order_items WHERE book_id = ? LIMIT 1";
        String softSql  = "UPDATE books SET is_deleted = TRUE, deleted_at = NOW(), stock = 0 WHERE id = ?";
        String hardSql  = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            boolean hasRefs = false;
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    hasRefs = rs.next();
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(hasRefs ? softSql : hardSql)) {
                ps.setInt(1, id);
                int affected = ps.executeUpdate();
                conn.commit();
                if (affected == 0) return DeleteResult.FAILED;
            }

            return hasRefs ? DeleteResult.DISABLED : DeleteResult.DELETED;

        } catch (SQLException e) {
            e.printStackTrace();
            return DeleteResult.FAILED;
        }
    }




        // Updates the rating of a book
        

    // Retrieves comments for a specific book
    public List<String> getBookComments(int bookId) {
        List<String> comments = new ArrayList<>();
        String query = "SELECT comment FROM comments WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(rs.getString("comment"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }

    // Adds a comment to a book
    public void addBookComment(int bookId, String comment) {
        String query = "INSERT INTO comments (book_id, comment) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, bookId);
            pstmt.setString(2, comment);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
        // Φέρνει όλα τα βιβλία μαζί με category (moods), price, bookstore, stock
    public List<Book> getAllBooksOverview() {
        List<Book> books = new ArrayList<>();

        String sql = "SELECT id, title, category, price, bookstore, stock " +
                     "FROM v_books_overview " +
                     "ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String category = rs.getString("category");

                double price = rs.getDouble("price");
                if (rs.wasNull()) {
                    price = 0.0;
                }

                String bookstore = rs.getString("bookstore");
                int stock = rs.getInt("stock");

                // Ο constructor Book θέλει ακόμη author/description,
                // αλλά στη βάση δεν υπάρχουν πλέον authors.
                Book b = new Book(id, title, "", ""); // author κενό, description κενό
                b.setCategory(category);
                b.setPrice(price);
                b.setBookstore(bookstore);
                b.setStock(stock);

                books.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    
    
        // Προσθήκη νέου βιβλίου με όλα τα στοιχεία + δέσιμο με mood (category)
    public void addBookWithDetails(String title,
                               String category,
                               double price,
                               String bookstore,
                               int stock) {

        String description = "Added from BookPage";

        String insertBookSql =
                "INSERT INTO books (title, description, category, price, bookstore, stock) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "RETURNING id";

        String selectMoodSql = "SELECT id FROM moods WHERE name = ?";
        String insertMoodSql = "INSERT INTO moods (name) VALUES (?) RETURNING id";
        String linkSql       = "INSERT INTO book_moods (book_id, mood_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            int bookId = -1;

            // 1) INSERT στο books και πάρε το id
            try (PreparedStatement ps = conn.prepareStatement(insertBookSql)) {
                ps.setString(1, title);
                ps.setString(2, description);
                ps.setString(3, category);

                if (price == 0.0) {
                    ps.setNull(4, java.sql.Types.NUMERIC);
                } else {
                    ps.setDouble(4, price);
                }

                ps.setString(5, bookstore);
                ps.setInt(6, stock);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        bookId = rs.getInt("id");
                    }
                }
            }

            if (bookId <= 0) {
                return; // κάτι πήγε στραβά, δεν συνεχίζουμε
            }

            // 2) mood/category (αν έχει δοθεί)
            if (category != null && !category.isBlank()) {
                int moodId = -1;

                // 2a) ψάξε αν υπάρχει ήδη
                try (PreparedStatement psMood = conn.prepareStatement(selectMoodSql)) {
                    psMood.setString(1, category);
                    try (ResultSet rsMood = psMood.executeQuery()) {
                        if (rsMood.next()) {
                            moodId = rsMood.getInt("id");
                        }
                    }
                }

                // 2b) αν δεν υπάρχει, φτιάξ' το
                if (moodId <= 0) {
                    try (PreparedStatement psMoodInsert = conn.prepareStatement(insertMoodSql)) {
                        psMoodInsert.setString(1, category);
                        try (ResultSet rsMoodIns = psMoodInsert.executeQuery()) {
                            if (rsMoodIns.next()) {
                                moodId = rsMoodIns.getInt("id");
                            }
                        }
                    }
                }

                // 2c) δέσε το book με το mood
                if (moodId > 0) {
                    try (PreparedStatement psLink = conn.prepareStatement(linkSql)) {
                        psLink.setInt(1, bookId);
                        psLink.setInt(2, moodId);
                        psLink.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}