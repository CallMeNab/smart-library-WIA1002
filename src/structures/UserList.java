package structures;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import models.Book;
import models.User;
/**
 * A custom Linked List acting as the global database of registered users.
 */
public class UserList {

    // ==========================================================
    // THE INNER CLASS (Perfect Information Hiding)
    // ==========================================================
    private class UserNode {
        User user;
        UserNode next;

        UserNode(User user) {
            this.user = user;
            this.next = null;
        }
    }

    // ==========================================================
    // LIST LOGIC
    // ==========================================================

    private UserNode head;

    public UserList() {
        this.head = null;
    }

    /**
     * Searches for a user by name. If they don't exist, it creates and registers them.
     * @param name The name entered at the login prompt.
     * @return The existing or newly created User profile.
     */
    public User findOrCreateUser(String name) {
        UserNode current = head;

        // Step 1: Search for an existing user (ignoring uppercase/lowercase differences)
        while (current != null) {
            if (current.user.name.equalsIgnoreCase(name)) {
                return current.user;
            }
            current = current.next;
        }
        User newUser = new User(name);
        UserNode newNode = new UserNode(newUser);
        newNode.next = head;
        head = newNode;
        return newUser;
    }

    /**
     * Displays all registered users. This will be used in the Admin Menu.
     */
    public void displayAllUsers() {
        if (head == null) {
            System.out.println("No users registered yet.");
            return;
        }
        System.out.println();
        System.out.println("========================");
        System.out.println("    Registered Users    ");
        System.out.println("========================\n");
        UserNode current = head;
        while (current != null) {
            System.out.println("- " + current.user.name);
            current = current.next;
        }
        System.out.println("------------------------");
    }

// ==========================================================
    // DOCUMENT-BASED DATABASE I/O LOGIC
    // ==========================================================

    /**
     * Saves the master user list and individual user profile files.
     */
    public void saveDatabase() {
        // Step 1: Tell Java to automatically create the "database/users" folders if they don't exist
        new File("database/users").mkdirs();

        try (PrintWriter masterWriter = new PrintWriter(new File("database/users.txt"))) {
            UserNode current = head;

            while (current != null) {
                // Step 2: Write the user's name to the master users.txt file
                masterWriter.println(current.user.name);

                // Step 3: Create a dedicated text file just for this user (e.g., "database/users/Alice.txt")
                File userFile = new File("database/users/" + current.user.name + ".txt");
                try (PrintWriter userWriter = new PrintWriter(userFile)) {
                    // Write their comma-separated ISBNs into their personal file
                    userWriter.println(current.user.activeBooks.getSavedData());
                }

                current = current.next;
            }
        } catch (Exception e) {
            System.out.println("Database Error: Could not save the users database.");
        }
    }

    /**
     * Loads the master user list, then individually opens each user's file to restore their books.
     * @param catalogue The main BST so we can find the real Book objects by their ISBN.
     */
    public void loadDatabase(BookBST catalogue) {
        File masterFile = new File("database/users.txt");

        // If the database doesn't exist yet (first time running), just safely exit the method
        if (!masterFile.exists()) return;

        try (Scanner sc = new Scanner(masterFile)) {
            while (sc.hasNextLine()) {
                // Step 1: Read the name from the master list
                String name = sc.nextLine().trim();
                if (name.isEmpty()) continue;

                // Step 2: Recreate their profile in memory
                User user = findOrCreateUser(name);

                // Step 3: Open their specific user file (e.g., "database/users/Alice.txt")
                File userFile = new File("database/users/" + name + ".txt");
                if (userFile.exists()) {
                    try (Scanner userSc = new Scanner(userFile)) {
                        if (userSc.hasNextLine()) {
                            String isbns = userSc.nextLine();
                            String[] isbnArray = isbns.split(",");

                            // Step 4: Reconnect them with their physical books
                            for (String isbnStr : isbnArray) {
                                if (!isbnStr.trim().isEmpty()) {
                                    try {
                                        int targetIsbn = Integer.parseInt(isbnStr.trim());
                                        Book b = catalogue.search(targetIsbn);

                                        if (b != null) {
                                            b.isBorrowed = true; // Mark book as checked out globally
                                            user.activeBooks.add(b); // Put it in their personal list
                                        }
                                    } catch (NumberFormatException ignored) {}
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Database Error: Could not load the users database.");
        }
    }
}
