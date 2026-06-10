package interfaces;

public interface LibraryADT {
    /**
     * Adds a new book to the library catalogue.
     * @param isbn   The unique International Standard Book Number.
     * @param title  The title of the book.
     * @param author The author of the book.
     */
    void addBook(int isbn, String title, String author);

    /**
     * Searches for a book in the catalogue using its ISBN.
     * @param isbn The ISBN of the book to find.
     */
    void searchBook(int isbn);

    /**
     * Checks out a book and adds it to the borrowing history.
     * @param isbn The ISBN of the book being borrowed.
     * @param currentUser the user that borrow the book
     */
    void borrowBook(int isbn, models.User currentUser);

    /**
     * Displays the borrowing history in Last-In-First-Out (LIFO) order,
     * showing the most recently borrowed books first.
     */
    void viewLatestHistory();

    /**
     * Return the book to the library
     * @param isbn The ISBN of the book being returned.
     * @param currentUser the user that return the book
     */
    void returnBook(int isbn, models.User currentUser);

    /**
     * View IDK
     * @param currentUser the user that borrow the book
     */
    void viewActiveCheckouts(models.User currentUser);

    void searchBookByTitle(String title);

    void searchBookByAuthor(String author);
    
    void viewCatalogue(java.util.Scanner sc);
}