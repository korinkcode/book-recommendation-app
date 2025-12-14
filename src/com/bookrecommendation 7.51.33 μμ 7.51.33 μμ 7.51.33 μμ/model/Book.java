/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookrecommendation.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a Book.
 */
public class Book {
    private int id; // Added id
    private String title;
    private String author;
    private String description;
    private int rating; // Rating from 1-5
    private List<String> comments; // List of comments
       // ΝΕΑ ΠΕΔΙΑ
    private String category;   // moods ως κείμενο, π.χ. "Happy, Fantasy"
    private double price;
    private String bookstore;
    private int stock;

    // Constructor with id
    public Book(int id, String title, String author, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.rating = 0; // Default rating
        
        this.category = null;
        this.price = 0.0;
        this.bookstore = null;
        this.stock = 0;

        
        this.comments = new ArrayList<>(); // Initialize comment list
        
        
        
   
        
    }
    
        public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBookstore() {
        return bookstore;
    }

    public void setBookstore(String bookstore) {
        this.bookstore = bookstore;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

        public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    
    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
    
    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
    }

    // Setters
    public void setRating(int rating) {
        this.rating = rating;
    }

    // Comment methods
    public List<String> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    @Override
    public String toString() {
        return title; // Only display the title in the list
    }
}