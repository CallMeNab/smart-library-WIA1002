package structures;

import models.Book;

/**
 * A custom Linked List that tracks the books currently checked out by a specific user.
 * This replaces the global stack for active borrowing logic.
 */
public class ActiveBookList {

    // ==========================================================
    // THE INNER CLASS (Perfect Information Hiding)
    // ==========================================================
    private class ActiveBookNode {
        Book book;
        ActiveBookNode next;

        ActiveBookNode(Book book) {
            this.book = book;
            this.next = null;
        }
    }

    // ==========================================================
    // LIST LOGIC
    // ==========================================================

    private ActiveBookNode head;

    public ActiveBookList() {
        this.head = null;
    }

    /**
     * Adds a newly borrowed book to the user's active list.
     */
    public void add(Book book) {
        ActiveBookNode newNode = new ActiveBookNode(book);
        // Insert at the head for O(1) efficiency
        newNode.next = head;
        head = newNode;
    }

    /**
     * Removes a book from the user's active list when they return it.
     * @param isbn The ISBN of the book being returned.
     * @return true if successfully returned, false if they didn't have the book.
     */
    public boolean remove(int isbn) {
        if (head == null) return false;

        // If the book to return is at the head of the list
        if (head.book.isbn == isbn) {
            head = head.next;
            return true;
        }

        // Traverse the list to find the book
        ActiveBookNode current = head;
        while (current.next != null && current.next.book.isbn != isbn) {
            current = current.next;
        }

        // If we found the book, bypass its node to remove it
        if (current.next != null) {
            current.next = current.next.next;
            return true;
        }

        return false; // Book not found in this user's possession
    }

    /**
     * Displays all books this user currently holds.
     */
    public void display() {
        if (head == null) {
            System.out.println("You currently have no active checkouts.");
            return;
        }
        
        System.out.println("\n===================================");
        System.out.println("       Your Active Checkouts       ");
        System.out.println("===================================");
        ActiveBookNode current = head;
        while (current != null) {
            System.out.println("[ISBN: " + current.book.isbn + "] " + current.book.title);
            current = current.next;
        }
        System.out.println("-----------------------------------");
    }

    /**
     * Extracts a comma-separated list of ISBNs for database saving.
     * @return A string of ISBNs (e.g., "101,105,204,")
     */
    public String getSavedData() {
        if (head == null) return "";

        StringBuilder sb = new StringBuilder();
        ActiveBookNode current = head;
        while (current != null) {
            sb.append(current.book.isbn).append(",");
            current = current.next;
        }
        return sb.toString();
    }
}