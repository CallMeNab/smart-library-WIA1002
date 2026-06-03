package models;

/**
 * Represents a physical book in the library.
 * This class also acts as a node for the Binary Search Tree catalogue,
 * containing pointers to left and right child nodes.
 */
public class Book {
    // Core data
    public int isbn;
    public String title;
    public String author;

    // Pointers for the Binary Search Tree
    public Book left;
    public Book right;

    /**
     * Constructs a new Book node.
     * @param isbn   The unique identifier for the book.
     * @param title  The title of the book.
     * @param author The author of the book.
     */
    public Book(int isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.left = null;
        this.right = null;
    }
}