package app;

import interfaces.LibraryADT;
import structures.BookBST;
import structures.BorrowStack;
import structures.UserList;
import models.Book;
import models.User;
import java.util.Scanner;
import java.util.InputMismatchException;
/**
 * where everything happen.
 */
public class SmartLibrary implements LibraryADT {

    private BookBST catalogue;
    private BorrowStack history;
    private UserList users;

    public SmartLibrary() {
        this.catalogue = new BookBST();
        this.history = new BorrowStack();
        this.users = new UserList();

        System.out.println("Loading database...");
        this.catalogue.loadDatabase();
        this.users.loadDatabase(this.catalogue);
        this.history.loadDatabase(this.catalogue);
        System.out.println("System ready.\n");
    }

    // ==========================================================
    // ADT IMPLEMENTATIONS
    // ==========================================================

    @Override
    public void addBook(int isbn, String title, String author) {
        Book existingBook = catalogue.search(isbn);
        if (existingBook != null) {
            System.out.println("Error: A book with ISBN " + isbn + " already exists (Title: " + existingBook.title + ").");
        } else {
            catalogue.insert(isbn, title, author);
            System.out.println("Success: '" + title + "' has been added to the catalogue.");
        }
    }

    @Override
    public void searchBook(int isbn) {
        Book b = catalogue.search(isbn);
        if (b != null) {
            System.out.println("\n--- Book Found ---");
            System.out.println("ISBN:   " + b.isbn);
            System.out.println("Title:  " + b.title);
            System.out.println("Author: " + b.author);
            System.out.println("Status: " + (b.isBorrowed ? "Currently Checked Out" : "Available on Shelf"));
            System.out.println("------------------");
        } else {
            System.out.println("\nNot Found: No book matches ISBN " + isbn + ".");
        }
    }

    @Override
    public void borrowBook(int isbn, User currentUser) {
        Book b = catalogue.search(isbn);

        if (b == null) {
            System.out.println("Failed: Book not in catalogue.");
            return;
        }

        if (b.isBorrowed) {
            System.out.println("Failed: '" + b.title + "' is currently checked out by someone else.");
            return;
        }

        b.isBorrowed = true;
        currentUser.activeBooks.add(b);
        history.push(b, currentUser.name, "BORROWED");
        System.out.println("Success: You have borrowed '" + b.title + "'.");
    }

    @Override
    public void viewLatestHistory() {
        history.show();
    }

    @Override
    public void returnBook(int isbn, User currentUser) {
        // Attempt to remove it from the user's personal list first
        boolean removed = currentUser.activeBooks.remove(isbn);

        if (removed) {
            // If they had it, find it in the main database and make it available again
            Book b = catalogue.search(isbn);
            b.isBorrowed = false;
            history.push(b, currentUser.name, "RETURNED");
            System.out.println("Success: You have returned '" + b.title + "'. Thank you!");
        } else {
            System.out.println("Failed: You do not currently have a book with ISBN " + isbn + " checked out.");
        }
    }

    @Override
    public void viewActiveCheckouts(User currentUser) {
        currentUser.activeBooks.display();
    }

    // ==========================================================
    // CONSOLE MENU LOGIC
    // ==========================================================

    public void runMenu() {
        Scanner sc = new Scanner(System.in);
        System.out.println("=================================");
        System.out.println(" Welcome to the Smart Library! ");
        System.out.println("=================================");

        while (true) {
            System.out.print("\nLOGIN - Enter your name (or type 'exit' to quit): ");
            String name = sc.nextLine().trim();

            if (name.equalsIgnoreCase("exit")) {
                System.out.println("Saving databases... please wait.");

                // --- TRIGGER SAVING BEFORE EXIT ---
                catalogue.saveDatabase();
                users.saveDatabase();
                history.saveDatabase();

                System.out.println("All data secured. Shutting down the library. Goodbye!");
                break;
            }

            // Role-Based Routing
            if (name.equalsIgnoreCase("admin")) {
                runAdminMenu(sc);
            } else {
                User currentUser = users.findOrCreateUser(name);
                System.out.println("\nWelcome, " + currentUser.name + "!");
                runStudentMenu(sc, currentUser);
            }
        }
        sc.close();
    }

    // --- ADMIN MENU ---
    private void runAdminMenu(Scanner sc) {
        while (true) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. Add a New Book");
            System.out.println("2. View Global Borrowing History");
            System.out.println("3. View Registered Users");
            System.out.println("4. Logout");

            int choice = getValidInt(sc, "Enter choice (1-4): ");

            if (choice == 4) {
                System.out.println("Admin logged out.");
                break;
            }

            switch (choice) {
                case 1:
                    int i = getValidInt(sc, "Enter ISBN (Integer): ");
                    System.out.print("Enter Title: ");
                    String t = sc.nextLine();
                    System.out.print("Enter Author: ");
                    String a = sc.nextLine();
                    addBook(i, t, a);
                    break;
                case 2:
                    viewLatestHistory();
                    break;
                case 3:
                    users.displayAllUsers();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // --- STUDENT MENU ---
    private void runStudentMenu(Scanner sc, User currentUser) {
        while (true) {
            System.out.println("\n--- STUDENT MENU ---");
            System.out.println("1. Search Catalogue");
            System.out.println("2. Borrow a Book");
            System.out.println("3. Return a Book");
            System.out.println("4. View My Active Checkouts");
            System.out.println("5. Logout");

            int choice = getValidInt(sc, "Enter choice (1-5): ");

            if (choice == 5) {
                System.out.println(currentUser.name + " logged out.");
                break;
            }

            switch (choice) {
                case 1:
                    searchBook(getValidInt(sc, "Enter ISBN to search: "));
                    break;
                case 2:
                    borrowBook(getValidInt(sc, "Enter ISBN to borrow: "), currentUser);
                    break;
                case 3:
                    returnBook(getValidInt(sc, "Enter ISBN to return: "), currentUser);
                    break;
                case 4:
                    viewActiveCheckouts(currentUser);
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /**
     * helper method to ensure the user inputs a valid integer.
     */
    private int getValidInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = sc.nextInt();
                sc.nextLine(); // Clear the buffer
                return input;
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid number.");
                sc.nextLine(); // Clear bad input
            }
        }
    }
}