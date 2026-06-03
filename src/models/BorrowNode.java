package models;

public class BorrowNode {
    public Book book;
    public BorrowNode next;

    public BorrowNode(Book book) {
        this.book = book;
        this.next = null; // Always null by default until pushed onto the stack
    }
}