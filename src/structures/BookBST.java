package structures;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
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

// ==========================================================
// EXTRA FUNCTIONALITIES: DISPLAY AND FLEXIBLE SEARCH
// ==========================================================

public void searchByTitle(String keyword) {
    System.out.println("\n--- Search Results for Title: " + keyword + " ---");
    boolean found = searchByTitleRec(root, keyword.toLowerCase());

    if (!found) {
        System.out.println("No books found with title containing: " + keyword);
    }
}

private boolean searchByTitleRec(Book current, String keyword) {
    if (current == null) {
        return false;
    }

    boolean foundLeft = searchByTitleRec(current.left, keyword);

    boolean foundCurrent = false;
    if (current.title.toLowerCase().contains(keyword)) {
        printBook(current);
        foundCurrent = true;
    }

    boolean foundRight = searchByTitleRec(current.right, keyword);

    return foundLeft || foundCurrent || foundRight;
}

public void searchByAuthor(String keyword) {
    System.out.println("\n--- Search Results for Author: " + keyword + " ---");
    boolean found = searchByAuthorRec(root, keyword.toLowerCase());

    if (!found) {
        System.out.println("No books found with author containing: " + keyword);
    }
}

private boolean searchByAuthorRec(Book current, String keyword) {
    if (current == null) {
        return false;
    }

    boolean foundLeft = searchByAuthorRec(current.left, keyword);

    boolean foundCurrent = false;
    if (current.author.toLowerCase().contains(keyword)) {
        printBook(current);
        foundCurrent = true;
    }

    boolean foundRight = searchByAuthorRec(current.right, keyword);

    return foundLeft || foundCurrent || foundRight;
}

public void displayCataloguePaginated(Scanner sc) {
    if (root == null) {
        System.out.println("No books in the catalogue.");
        return;
    }

    final int PAGE_SIZE = 10;
    int[] counter = {0};
    int[] page = {1};

    while (true) {
        System.out.println("\n===== Catalogue Page " + page[0] + " =====");

        int start = (page[0] - 1) * PAGE_SIZE;
        int end = start + PAGE_SIZE;

        counter[0] = 0;
        int displayed = displayPageRec(root, start, end, counter);

        if (displayed == 0) {
            System.out.println("No more books on this page.");
        }

        System.out.println("\n1. Next Page");
        System.out.println("2. Previous Page");
        System.out.println("3. Back");
        System.out.print("Enter choice: ");

        try {
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                page[0]++;
            } else if (choice == 2) {
                if (page[0] > 1) {
                    page[0]--;
                } else {
                    System.out.println("You are already on the first page.");
                }
            } else if (choice == 3) {
                break;
            } else {
                System.out.println("Invalid option.");
            }
        } catch (Exception e) {
            System.out.println("Error: Please enter a valid number.");
            sc.nextLine();
        }
    }
}

private int displayPageRec(Book current, int start, int end, int[] counter) {
    if (current == null) {
        return 0;
    }

    int displayed = 0;

    displayed += displayPageRec(current.left, start, end, counter);

    if (counter[0] >= start && counter[0] < end) {
        printBook(current);
        displayed++;
    }

    counter[0]++;

    displayed += displayPageRec(current.right, start, end, counter);

    return displayed;
}

private void printBook(Book b) {
    System.out.println(
        "ISBN: " + b.isbn +
        " | Title: " + b.title +
        " | Author: " + b.author +
        " | Status: " + (b.isBorrowed ? "Borrowed" : "Available"));
}

    // =============================
    // DATABASE FILE I/O LOGIC
    // =============================

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