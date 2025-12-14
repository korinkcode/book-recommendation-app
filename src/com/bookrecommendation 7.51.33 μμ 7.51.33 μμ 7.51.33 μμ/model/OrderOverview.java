/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookrecommendation.model;
import java.sql.Timestamp;

/**
 *
 * @author korinakaratzioti
 */
public class OrderOverview {
     private int id;
    private String username;
    private String books;
    private String bookstore;
    private double totalAmount;
    private int totalBooks;
    private Timestamp createdAt;

    public OrderOverview(int id,
                         String username,
                         String books,
                         String bookstore,
                         double totalAmount,
                         int totalBooks,
                         Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.books = books;
        this.bookstore = bookstore;
        this.totalAmount = totalAmount;
        this.totalBooks = totalBooks;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getBooks() {
        return books;
    }

    public String getBookstore() {
        return bookstore;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getTotalBooks() {
        return totalBooks;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}

