package app;

import interfaces.LibraryADT;
import structures.BookBST;
import structures.BorrowStack;
import models.Book;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * where everything happen.
 */
public class SmartLibrary implements LibraryADT {

    // calling the ds
    private BookBST catalogue;
    private BorrowStack history;

    public SmartLibrary() {
        this.catalogue = new BookBST();
        this.history = new BorrowStack();
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
            System.out.println("------------------");
        } else {
            System.out.println("\nNot Found: No book matches ISBN " + isbn + ".");
        }
    }

    @Override
    public void borrowBook(int isbn) {
        Book b = catalogue.search(isbn);
        if (b != null) {
            history.push(b);
            System.out.println("Success: You have borrowed '" + b.title + "'.");
        } else {
            System.out.println("Failed: Book not in catalogue.");
        }
    }

    @Override
    public void viewLatestHistory() {
        history.show();
    }

    // ==========================================================
    // CONSOLE MENU LOGIC
    // ==========================================================

    public void runMenu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            printMenu();
            int choice = getValidInt(sc, "\nEnter your choice (1-5): ");

            if (choice == 5) {
                System.out.println("Exiting Smart Library. Goodbye!");
                break;
            }
            handleChoice(choice, sc);
        }
        sc.close();
    }

    private void printMenu() {
        System.out.println("\n--- SmartLibrary Menu ---");
        System.out.println("1. Add Book");
        System.out.println("2. Search (BST)");
        System.out.println("3. Borrow (Stack)");
        System.out.println("4. History");
        System.out.println("5. Exit");
    }

    private void handleChoice(int choice, Scanner sc) {
        switch (choice) {
            case 1:
                // Use the helper to ensure ISBN is a valid integer
                int i = getValidInt(sc, "Enter ISBN (Integer): ");

                System.out.print("Enter Title: ");
                String t = sc.nextLine();

                System.out.print("Enter Author: ");
                String a = sc.nextLine();

                addBook(i, t, a);
                break;
            case 2:
                System.out.print("Enter ISBN to search: ");
                searchBook(sc.nextInt());
                break;
            case 3:
                System.out.print("Enter ISBN to borrow: ");
                borrowBook(sc.nextInt());
                break;
            case 4:
                viewLatestHistory();
                break;
            default:
                System.out.println("Invalid option. Please choose 1-5.");
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
                sc.nextLine();
                return input;
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid number. Letters and symbols are not allowed.");
                sc.nextLine();
            }
        }
    }
}