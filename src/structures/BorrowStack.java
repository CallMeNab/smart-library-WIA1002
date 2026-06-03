package structures;

import models.Book;
import models.BorrowNode;

/**
 * A custom Linked-List based Stack(LIFO) to track borrowing history.
 */
public class BorrowStack {

    /** the most recently borrowed book. */
    private BorrowNode top;

    public BorrowStack() {
        this.top = null;
    }

    /**
     * Pushes a newly borrowed book onto the top of the stack.
     * @param book The Book object to add to the history.
     */
    public void push(Book book) {
        BorrowNode newNode = new BorrowNode(book);

        // The new node points down to whatever was previously on top
        newNode.next = top;

        // The new node officially becomes the new top of the stack
        top = newNode;
    }

    /**
     * Displays all borrowed books in LIFO order (newest to oldest).
     */
    public void show() {

        if (top == null) {
            System.out.println("Borrowing history is empty.");
            return;
        }

        System.out.println("\n--- Borrowing History (Newest First) ---");

        // Start at the top and work our way down until we hit the bottom (null)
        BorrowNode current = top;
        while (current != null) {
            System.out.println("[ISBN: " + current.book.isbn + "] " + current.book.title + " by " + current.book.author);
            current = current.next;
        }
        System.out.println("----------------------------------------");
    }
}