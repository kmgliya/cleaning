import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminManager {
    private static final String ADMIN_PASSWORD = "taza"; // Administrator password
    private static final Scanner scanner = new Scanner(System.in);
    private static final ReviewManager reviewManager = new ReviewManager(); // Add ReviewManager instance

    public static void adminLogin() {
        // Request the administrator password
        System.out.println("Enter the administrator password:");
        String passwordAttempt = scanner.nextLine();

        // Check if the entered password matches the administrator password
        if (passwordAttempt.equals(ADMIN_PASSWORD)) {
            System.out.println("You have successfully logged in as an administrator!");
            adminActions();
        } else {
            System.out.println("Incorrect administrator password.");
        }
    }

    private static void adminActions() {
        boolean isAdminMenuActive = true;
        while (isAdminMenuActive) {
            // Display the menu of actions for the administrator
            System.out.println("Choose an action:");
            System.out.println("1. View current booking orders");
            System.out.println("2. View information about employees");
            System.out.println("3. View and manage reviews");
            System.out.println("4. Exit");

            int action = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            // Perform the selected action
            switch (action) {
                case 1:
                    viewBookings();
                    break;
                case 2:
                    viewEmployees();
                    break;
                case 3:
                    reviewManager.manageReviews(); // Call manageReviews from ReviewManager
                    break;
                case 4:
                    isAdminMenuActive = false;
                    break;
                default:
                    System.out.println("Invalid action");
            }
        }
    }

    private static void viewBookings() {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM bookings");
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Current booking orders:");
            while (resultSet.next()) {
                String bookingDate = resultSet.getString("booking_date");
                String userName = DatabaseManager.getUserName(resultSet.getInt("user_id"));
                String comments = resultSet.getString("comments");

                System.out.println("Booking date: " + bookingDate + ", User name: " + userName + ", Comments: " + comments);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving bookings: " + e.getMessage());
        }
    }

    private static void viewEmployees() {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement("SELECT id, name, contact FROM employees");
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Information about employees:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String contact = resultSet.getString("contact");

                System.out.println("ID: " + id + ", Name: " + name + ", Contact: " + contact);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving information about employees: " + e.getMessage());
        }
    }
}
