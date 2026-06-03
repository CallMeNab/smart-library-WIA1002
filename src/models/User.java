package models;

import structures.ActiveBookList;

/**
 * Represents a person interacting with the Smart Library.
 * Holds their profile name and a custom linked list of their borrowed books.
 */
public class User {
    public String name;

    // Every user gets their own personal list of checked-out books
    public ActiveBookList activeBooks;

    public User(String name) {
        this.name = name;
        // Automatically initialize an empty list when a new user profile is created
        this.activeBooks = new ActiveBookList();
    }
}