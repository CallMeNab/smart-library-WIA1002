package models;

/**
 * A node for BorrowStack.
 * It holds reference to a borrowed Book and a pointer to the next node
 * in the stack to maintain LIFO order.
 */
public class BorrowNode {
    public Book book;
    public BorrowNode next;

    /**
     * Constructs a new stack node for a borrowed book.
     * @param book The Book object being added to the history.
     */
    public BorrowNode(Book book) {
        this.book = book;
        this.next = null;
    }
}