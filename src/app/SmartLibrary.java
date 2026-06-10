package app;

import interfaces.LibraryADT;
import java.util.InputMismatchException;
import java.util.Scanner;
import models.Book;
import models.User;
import structures.BookBST;
import structures.BorrowStack;
import structures.UserList;
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
            System.out.println();
            printSmallLine();
            System.out.println("BOOK FOUND");
            printSmallLine();
            System.out.println("ISBN   : " + b.isbn);
            System.out.println("Title  : " + b.title);
            System.out.println("Author : " + b.author);
            System.out.println("Status : " + (b.isBorrowed ? "Currently Checked Out" : "Available on Shelf"));
            printSmallLine();
        } else {
            System.out.println("\n[INFO] No book matches ISBN " + isbn + ".");
        }
    }

        @Override
    public void searchBookByTitle(String title) {
        catalogue.searchByTitle(title);
    }

    @Override
    public void searchBookByAuthor(String author) {
        catalogue.searchByAuthor(author);
    }

    @Override
    public void viewCatalogue(Scanner sc) {
        catalogue.displayCataloguePaginated(sc);
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
        System.out.println("===============================");
        System.out.println(" Welcome to the Smart Library! ");
        System.out.println("===============================");

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
                runStudentMenu(sc, currentUser);
            }
        }
        sc.close();
    }

    // --- ADMIN MENU ---
    private void runAdminMenu(Scanner sc) {
        while (true) {
            System.out.println();
            printLine();
            System.out.println("             ADMIN DASHBOARD ");
            printLine();
            System.out.println("Welcome admin !");
            System.out.println();
            System.out.println("1. Add a New Book");
            System.out.println("2. View Global Borrowing History");
            System.out.println("3. View Registered Users");
            System.out.println("4. Logout\n");

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
        System.out.println();
        printLine();
        System.out.println("              STUDENT MENU");
        printLine();
        System.out.println("Welcome, " + currentUser.name);
        System.out.println();
        System.out.println("1. Search Catalogue");
        System.out.println("2. Borrow a Book");
        System.out.println("3. Return a Book");
        System.out.println("4. View My Active Checkouts");
        System.out.println("5. View Catalogue");
        System.out.println("6. Logout");
        printLine();

            int choice = getValidInt(sc, "Enter choice (1-6): ");

            if (choice == 6) {
                System.out.println(currentUser.name + " logged out.");
                break;
            }

            switch (choice) {
                case 1:
                    runSearchMenu(sc);
                    break;
                case 2:
                    borrowBook(getValidInt(sc, "Enter ISBN to borrow: "), currentUser);
                    pause(sc);
                    break;
                case 3:
                    System.out.println("\nHere are your active checkouts:");
                    viewActiveCheckouts(currentUser);
                    returnBook(getValidInt(sc, "\nEnter ISBN to return: "), currentUser);
                    pause(sc);
                    break;
                case 4:
                    viewActiveCheckouts(currentUser);
                    pause(sc);
                    break;
                case 5:
                    viewCatalogue(sc);
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void runSearchMenu(Scanner sc) {
        while (true) {
        System.out.println();
        printLine();
        System.out.println("            SEARCH CATALOGUE");
        printLine();
        System.out.println("1. Search by ISBN");
        System.out.println("2. Search by Title");
        System.out.println("3. Search by Author");
        System.out.println("4. Back");
        printLine();

        int choice = getValidInt(sc, "Enter choice (1-4): ");

        switch (choice) {
            case 1:
                searchBook(getValidInt(sc, "Enter ISBN to search: "));
                pause(sc);
                break;

            case 2:
                System.out.print("Enter title keyword: ");
                String title = sc.nextLine().trim();

                if (title.isEmpty()) {
                    System.out.println("[ERROR] Title keyword cannot be empty.");
                } else {
                    searchBookByTitle(title);
                }

                pause(sc);
                break;

            case 3:
                System.out.print("Enter author keyword: ");
                String author = sc.nextLine().trim();

                if (author.isEmpty()) {
                    System.out.println("[ERROR] Author keyword cannot be empty.");
                } else {
                    searchBookByAuthor(author);
                }

                pause(sc);
                break;

            case 4:
                return;

            default:
                System.out.println("[ERROR] Invalid option.");
                pause(sc);
            }
        }
    }
    
    private void printLine() {
    System.out.println("========================================");
    }

    private void printSmallLine() {
        System.out.println("----------------------------------------");
    }

    private void pause(Scanner sc) {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
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