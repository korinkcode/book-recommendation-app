/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookrecommendation.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author korinakaratzioti
 */
public class UserManager {
    private static final List<User> users = new ArrayList<>();

    public static void addUser(User user) {
        users.add(user);
    }

    // Method to get all users
    public static List<User> getUsers() {
        return users;
    }
}


