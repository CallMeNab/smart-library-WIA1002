package structures;

import models.Book;
import models.BorrowNode;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;


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
    public void push(Book book, String borrowerName, String action) {
        BorrowNode newNode = new BorrowNode(book, borrowerName, action);
        newNode.next = top;
        top = newNode;
    }

    /**
     * Displays all borrowed books in LIFO order.
     */
    public void show() {
        if (top == null) {
            System.out.println("Global history is empty.");
            return;
        }

        System.out.println("\n--- Global Borrowing History (Newest First) ---");
        BorrowNode current = top;
        while (current != null) {
            System.out.println("[ISBN: " + current.book.isbn + "] '" + current.book.title + "' was " + current.action + " by " + current.borrowerName);
            current = current.next;
        }
        System.out.println("-----------------------------------------------");
    }

    // ==========================================================
    // DATABASE FILE I/O LOGIC
    // ==========================================================

    public void saveDatabase() {
        new File("database").mkdirs(); // Ensure root folder exists

        try (PrintWriter writer = new PrintWriter(new File("database/history.txt"))) {
            // We will temporarily put the nodes in a list so we can iterate backward.
            ArrayList<BorrowNode> tempList = new ArrayList<>();
            BorrowNode current = top;
            while (current != null) {
                tempList.add(current);
                current = current.next;
            }

            // Write to file from oldest to newest
            for (int i = tempList.size() - 1; i >= 0; i--) {
                BorrowNode n = tempList.get(i);
                writer.println(n.book.isbn + ";" + n.book.title + ";" + n.borrowerName + ";" + n.action);
            }
        } catch (Exception e) {
            System.out.println("Database Error: Could not save the history log.");
        }
    }

    public void loadDatabase(BookBST catalogue) {
        File historyFile = new File("database/history.txt");
        if (!historyFile.exists()) return;

        try (Scanner sc = new Scanner(historyFile)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split(";");

                if (parts.length == 4) {
                    try {
                        int isbn = Integer.parseInt(parts[0]);
                        String borrowerName = parts[2];
                        String action = parts[3];

                        Book b = catalogue.search(isbn);
                        if (b != null) {
                            push(b, borrowerName, action);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception e) {
            System.out.println("Database Error: Could not load the history log.");
        }
    }
}