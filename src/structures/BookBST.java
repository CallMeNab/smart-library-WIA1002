package structures;

import models.Book;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * BST that acts as the library's main database catalogue.
 * Books are organized by their ISBN.
 */
public class BookBST {


    private Book root;

    /**
     * Initializes an empty Binary Search Tree.
     */
    public BookBST() {
        this.root = null;
    }

    /**
     * method to insert a new book into the catalogue.
     * @param isbn   The unique ISBN.
     * @param title  The title of the book.
     * @param author The author of the book.
     */
    public void insert(int isbn, String title, String author) {
        root = insertRec(root, isbn, title, author);
    }

    /**
     * helper method to find the correct placement for a new book.
     * @param current The current node being evaluated in the tree traversal.
     * @param isbn    The ISBN to insert.
     * @param title   The title of the book.
     * @param author  The author of the book.
     * @return The updated current node.
     */
    private Book insertRec(Book current, int isbn, String title, String author) {
        if (current == null) {
            return new Book(isbn, title, author);
        }

        if (isbn < current.isbn) {
            current.left = insertRec(current.left, isbn, title, author);
        } else if (isbn > current.isbn) {
            current.right = insertRec(current.right, isbn, title, author);
        }

        return current;
    }

    /**
     * method to search for a book by its ISBN.
     * @param isbn The ISBN of the book to find.
     * @return The Book object if found, or null if it does not exist in the catalogue.
     */
    public Book search(int isbn) {
        return searchRec(root, isbn);
    }

    /**
     * helper method to traverse the tree efficiently.
     * @param current The current node being evaluated.
     * @param isbn    The target ISBN being searched for.
     * @return The found Book node, or null if the search hits a dead end.
     */
    private Book searchRec(Book current, int isbn) {
        if (current == null || current.isbn == isbn) {
            return current;
        }

        if (isbn < current.isbn) {
            return searchRec(current.left, isbn);
        }

        return searchRec(current.right, isbn);
    }

    // ==========================================================
    // DATABASE FILE I/O LOGIC
    // ==========================================================

    // Note: Put these imports at the very top of your file!

    /**
     * Saves the BST to a text file using Pre-Order traversal.
     * This ensures the tree shape is preserved when loaded later!
     */
    public void saveDatabase() {
        new File("database/books").mkdirs();

        try (PrintWriter writer = new PrintWriter(new File("database/books/catalogue.txt"))) {
            saveNodePreOrder(root, writer);
        } catch (Exception e) {
            System.out.println("Database Error: Could not save the catalogue.");
        }
    }

    /**
     * Recursive helper to save nodes: Root, Left, Right.
     */
    private void saveNodePreOrder(Book current, PrintWriter writer) {
        if (current == null) return;

        // Write the current node (Root)
        writer.println(current.isbn + ";" + current.title + ";" + current.author);

        // Traverse Left
        saveNodePreOrder(current.left, writer);

        // Traverse Right
        saveNodePreOrder(current.right, writer);
    }

    /**
     * Loads the catalogue from the text file.
     */
    public void loadDatabase() {
        File catFile = new File("database/books/catalogue.txt");
        if (!catFile.exists()) return; // Normal for the very first run

        try (Scanner sc = new Scanner(catFile)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split(";");

                if (parts.length == 3) {
                    try {
                        int isbn = Integer.parseInt(parts[0]);
                        String title = parts[1];
                        String author = parts[2];

                        // Use the existing insert method to rebuild the tree
                        insert(isbn, title, author);
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception e) {
            System.out.println("Database Error: Could not load the catalogue.");
        }
    }
}