package structures;

import models.Book;

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
}