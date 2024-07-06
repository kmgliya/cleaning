import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ClientManager {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            clientLogin();
        }
    }

    public static void clientLogin() {
        System.out.println("Choose an action:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                System.out.println("Goodbye!");
                System.exit(0);
            default:
                System.out.println("Invalid choice");
        }
    }

    private static void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username:");
        String username = scanner.nextLine();

        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        // Simple validation example:
        if (isValidUser(username, password)) {
            System.out.println("Welcome, " + username + "!");
            // After successful login, show the client menu
            clientMenu();
        } else {
            System.out.println("Incorrect username or password.");
        }
    }

    private static void register() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username:");
        String username = scanner.nextLine();

        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        System.out.println("Enter your email:");
        String email = scanner.nextLine();

        // Insert the registration data into the database
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Registration successful!");
                // After successful registration, show the client menu
                clientMenu();
            } else {
                System.out.println("Registration failed. Please try again later.");
            }
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    private static boolean isValidUser(String username, String password) {
        return true; // Placeholder for actual validation logic
    }

    private static void clientMenu() {
        while (true) {
            System.out.println("Choose an action:");
            System.out.println("1. View services");
            System.out.println("2. Book a service");
            System.out.println("3. Leave a review");
            System.out.println("4. About 'Taza'");
            System.out.println("5. Exit");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    viewServices();
                    break;
                case 2:
                    bookService(1); // Placeholder for actual client ID
                    break;
                case 3:
                    leaveReview(1); // Placeholder for actual client ID
                    break;
                case 4:
                    AboutUs.showInfo();
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void viewServices() {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM services");
             ResultSet resultSet = statement.executeQuery()) {
            System.out.println("Viewing all available services:");
            System.out.println("ID\tName\tDescription\tPrice");
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String description = resultSet.getString(3);
                double price = resultSet.getDouble(4);
                System.out.println(id + "\t" + name + "\t" + description + "\t" + price);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving services: " + e.getMessage());
        }
    }

    public static void bookService(int clientId) {
        Scanner scanner = new Scanner(System.in); // Create a new Scanner object
        try {
            System.out.println("Enter the ID of the service you want to book:");
            int serviceId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            System.out.println("Enter the area (in square meters):");
            double area = scanner.nextDouble();
            scanner.nextLine(); // Consume the newline character

            double totalPrice = ServiceManager.calculatePrice(serviceId, area);

            System.out.println("The approximate cost of the service is: " + totalPrice + " som. Our manager will contact you to discuss the details of your order.");

            System.out.println("Enter the booking date (YYYY-MM-DD):");
            String bookingDate = scanner.nextLine();

            System.out.println("Enter comments:");
            String comments = scanner.nextLine();

            BookingManager.bookServiceInDB(clientId, serviceId, bookingDate, comments, totalPrice);

            System.out.println("Booking successfully completed!");
        } catch (Exception e) {
            System.out.println("Error during booking: " + e.getMessage());
        }
    }

    public static void leaveReview(int clientId) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Enter the ID of the service you want to review:");
            while (!scanner.hasNextInt()) {
                System.out.println("Please enter a valid service ID.");
                scanner.next(); // Consume the invalid input
            }
            int serviceId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            System.out.println("Enter your review:");
            String review = scanner.nextLine();

            try (Connection connection = DatabaseManager.connect();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO reviews (service_id, review) VALUES (?, ?)")) {
                statement.setInt(1, serviceId);
                statement.setString(2, review);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Review submitted successfully!");
                } else {
                    System.out.println("Failed to submit review. Please try again later.");
                }
            } catch (SQLException e) {
                System.out.println("Error submitting review: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error during review submission: " + e.getMessage());
        }
    }
    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next(); // Consume the invalid input
        }
        return scanner.nextInt();
    }
}