/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.bookrecommendation.gui;

import com.bookrecommendation.dao.BookDAO;
import com.bookrecommendation.dao.LibraryDAO;
import com.bookrecommendation.model.Book;
import com.bookrecommendation.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Main application window.
 */
public class MainFrame extends JFrame {
    private final JComboBox<String> moodComboBox;
    private final JButton recommendButton;
    private final JButton addToLibraryButton;
    private final JButton viewLibraryButton;
    private final JButton signUpButton;
    private JButton loginButton; // New login button
    private final JList<Book> bookList;
    private User currentUser;
    private JLabel userInfoLabel; // Label to display username

    // Panels for grouping components
    private JPanel moodPanel;
    private JPanel bottomPanel;

    public MainFrame() {
        super("Book Recommendation App");

        // Window settings
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        signUpButton = new JButton("Sign Up");
        loginButton = new JButton("Log In"); // Create the login button

        // Initialize userInfoLabel
        userInfoLabel = new JLabel();
        userInfoLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Top panel with sign-up and login buttons, plus user info
        JPanel topPanel = new JPanel(new BorderLayout());

        // Left side (FlowLayout with LEFT alignment)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(signUpButton);
        leftPanel.add(loginButton); // Add the login button next to sign up

        // Right side (FlowLayout with RIGHT alignment)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(userInfoLabel);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // Mood selection panel (initially hidden)
        moodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel moodLabel = new JLabel("Select Your Mood:");
        String[] moods = {
                "Happy", "Adventurous", "Philosophical", "Romantic", "Mysterious",
                "Inspirational", "Fantasy", "Historical", "Sci-Fi", "Thriller"
        };
        moodComboBox = new JComboBox<>(moods);
        recommendButton = new JButton("Get Recommendations");
        moodPanel.add(moodLabel);
        moodPanel.add(moodComboBox);
        moodPanel.add(recommendButton);
        moodPanel.setVisible(false); // Hide initially

        // Book list
        bookList = new JList<>();
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane bookListScrollPane = new JScrollPane(bookList);

        // Bottom panel with library buttons (initially hidden)
        bottomPanel = new JPanel(new FlowLayout());
        addToLibraryButton = new JButton("Add to Library");
        viewLibraryButton = new JButton("View Library");
        bottomPanel.add(addToLibraryButton);
        bottomPanel.add(viewLibraryButton);
        bottomPanel.setVisible(false); // Hide initially

        // Main content panel to hold moodPanel and bookList
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BorderLayout());

        // Add moodPanel to the NORTH of mainContentPanel
        mainContentPanel.add(moodPanel, BorderLayout.NORTH);
        // Add bookListScrollPane to the CENTER of mainContentPanel
        mainContentPanel.add(bookListScrollPane, BorderLayout.CENTER);

        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action for "Sign Up" button
        signUpButton.addActionListener(e -> new SignUpDialog(this));

        // Action for "Log In" button
        loginButton.addActionListener(e -> {
            // Show the LoginDialog. On success, it calls setCurrentUser(user).
            LoginDialog loginDialog = new LoginDialog(this);
            loginDialog.setVisible(true);
        });

        // Action for "Get Recommendations" button
        recommendButton.addActionListener(e -> showRecommendations());

        // Action for "Add to Library" button
        addToLibraryButton.addActionListener(e -> addToLibrary());

        // Action for "View Library" button
        viewLibraryButton.addActionListener(e -> viewLibrary());

        // Action for double-click on the list
        bookList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Book selectedBook = bookList.getSelectedValue();
                    if (selectedBook != null) {
                        showBookDetails(selectedBook);
                    }
                }
            }
        });

        // Initially no user is logged in, so update UI accordingly
        updateUserInfo();
    }

    // Method to set the current user
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserInfo();
    }

    // Method to update the user info label and show/hide components
    private void updateUserInfo() {
        if (currentUser != null) {
            userInfoLabel.setText("Welcome, " + currentUser.getUsername());
            // Show the mood panel and bottom panel
            moodPanel.setVisible(true);
            bottomPanel.setVisible(true);
            signUpButton.setVisible(false); // Hide the sign-up button after login
            loginButton.setVisible(false);  // Hide the login button after login
        } else {
            userInfoLabel.setText("");
            // Hide the mood panel and bottom panel
            moodPanel.setVisible(false);
            bottomPanel.setVisible(false);
            signUpButton.setVisible(true); // Show the sign-up button when no user
            loginButton.setVisible(true);  // Show the login button when no user
        }
    }

    private void showRecommendations() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please sign up or log in to get recommendations.");
            return;
        }
        String selectedMood = (String) moodComboBox.getSelectedItem();
        BookDAO bookDAO = new BookDAO();
        List<Book> books = bookDAO.getBooksByMood(selectedMood);
        DefaultListModel<Book> listModel = new DefaultListModel<>();
        for (Book book : books) {
            listModel.addElement(book);
        }
        bookList.setModel(listModel);
    }

    private void showBookDetails(Book book) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please sign up or log in to view book details.");
            return;
        }
        JDialog dialog = new JDialog(this, "Book Details", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JTextArea detailsArea = new JTextArea();
        detailsArea.setText(getBookDetailsText(book));
        detailsArea.setEditable(false);

        JPanel ratingPanel = new JPanel(new FlowLayout());
        JLabel ratingLabel = new JLabel("Rate this book (1-5):");
        JComboBox<Integer> ratingComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JButton rateButton = new JButton("Submit Rating");
        ratingPanel.add(ratingLabel);
        ratingPanel.add(ratingComboBox);
        ratingPanel.add(rateButton);

        JPanel commentPanel = new JPanel(new BorderLayout());
        JLabel commentLabel = new JLabel("Add a comment:");
        JTextField commentField = new JTextField();
        JButton addCommentButton = new JButton("Add Comment");
        commentPanel.add(commentLabel, BorderLayout.NORTH);
        commentPanel.add(commentField, BorderLayout.CENTER);
        commentPanel.add(addCommentButton, BorderLayout.SOUTH);

        // Combine rating and comment panels
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(ratingPanel, BorderLayout.NORTH);
        southPanel.add(commentPanel, BorderLayout.SOUTH);

        rateButton.addActionListener(e -> {
            int selectedRating = (int) ratingComboBox.getSelectedItem();
            book.setRating(selectedRating);
            BookDAO bookDAO = new BookDAO();
            bookDAO.updateBookRating(book.getId(), selectedRating);
            detailsArea.setText(getBookDetailsText(book));
        });

        addCommentButton.addActionListener(e -> {
            String comment = commentField.getText();
            if (!comment.isEmpty()) {
                book.addComment(comment);
                BookDAO bookDAO = new BookDAO();
                bookDAO.addBookComment(book.getId(), comment);
                commentField.setText("");
                detailsArea.setText(getBookDetailsText(book));
            }
        });

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        contentPanel.add(southPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    // Helper method to format book details
    private String getBookDetailsText(Book book) {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(book.getTitle()).append("\n")
                .append("Author: ").append(book.getAuthor()).append("\n")
                .append("Description: ").append(book.getDescription()).append("\n")
                .append("Rating: ").append(book.getRating()).append("/5\n")
                .append("Comments:\n");
        for (String comment : book.getComments()) {
            sb.append("- ").append(comment).append("\n");
        }
        return sb.toString();
    }

    private void addToLibrary() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please sign up or log in to add books to your library.");
            return;
        }
        Book selectedBook = bookList.getSelectedValue();
        if (selectedBook != null) {
            LibraryDAO libraryDAO = new LibraryDAO();
            if (libraryDAO.isBookInLibrary(currentUser.getId(), selectedBook.getId())) {
                JOptionPane.showMessageDialog(this, "Book is already in your library.");
            } else {
                libraryDAO.addBookToLibrary(currentUser.getId(), selectedBook.getId());
                JOptionPane.showMessageDialog(this, selectedBook.getTitle() + " has been added to your library.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to add to your library.");
        }
    }

    private void viewLibrary() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please sign up or log in to view your library.");
            return;
        }
        JDialog dialog = new JDialog(this, "Your Library", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        LibraryDAO libraryDAO = new LibraryDAO();
        List<Book> libraryBooks = libraryDAO.getUserLibrary(currentUser.getId());

        DefaultListModel<Book> libraryModel = new DefaultListModel<>();
        for (Book book : libraryBooks) {
            libraryModel.addElement(book);
        }
        JList<Book> libraryList = new JList<>(libraryModel);
        libraryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add double-click action to library list
        libraryList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Book selectedBook = libraryList.getSelectedValue();
                    if (selectedBook != null) {
                        showBookDetails(selectedBook);
                    }
                }
            }
        });

        dialog.add(new JScrollPane(libraryList));
        dialog.setVisible(true);
    }
}

