/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.bookrecommendation.dao;

import com.bookrecommendation.model.Book;
import com.bookrecommendation.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing user libraries.
 */
public class LibraryDAO {
    // Adds a book to a user's library
    public void addBookToLibrary(int userId, int bookId) {
        String query = "INSERT INTO user_library (user_id, book_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
    }

    // Checks if a book is already in a user's library
    public boolean isBookInLibrary(int userId, int bookId) {
        String query = "SELECT 1 FROM user_library WHERE user_id = ? AND book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
        return false;
    }

    // Retrieves all books in a user's library
    public List<Book> getUserLibrary(int userId) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT b.id, b.title, b.author, b.description, b.rating " +
                "FROM books b " +
                "JOIN user_library ul ON b.id = ul.book_id " +
                "WHERE ul.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Create Book object
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    String description = rs.getString("description");
                    int rating = rs.getInt("rating");
                    Book book = new Book(id, title, author, description);
                    book.setRating(rating);

                    // Load comments
                    List<String> comments = new BookDAO().getBookComments(id);
                    book.getComments().addAll(comments);

                    books.add(book);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }

        return books;
    }
}
