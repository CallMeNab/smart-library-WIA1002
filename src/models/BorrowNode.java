package models;

/**
 * A node for BorrowStack.
 * It holds reference to a borrowed Book and a pointer to the next node
 * in the stack to maintain LIFO order.
 */
public class BorrowNode {
    public Book book;
    public String borrowerName;
    public String action;
    public BorrowNode next;

    /**
     * Constructs a new stack node for a borrowed book.
     * @param book The Book object being added to the history.
     * @param borrowerName the user that borrow the book.
     * @param action what user doing, borrow or returning.
     */
    public BorrowNode(Book book, String borrowerName, String action) {
        this.book = book;
        this.borrowerName = borrowerName;
        this.action = action;
        this.next = null;
    }
}