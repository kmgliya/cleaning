import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class ReviewManager {
    private static final Scanner scanner = new Scanner(System.in);

    public void manageReviews() {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM reviews");
             var resultSet = statement.executeQuery()) {

            System.out.println("Current reviews:");
            while (resultSet.next()) {
                int reviewId = resultSet.getInt("id");
                int userId = resultSet.getInt("user_id");
                String reviewText = resultSet.getString("review");

                System.out.println("Review ID: " + reviewId + ", User ID: " + userId + ", Review: " + reviewText);
            }

            System.out.println("Choose an action:");
            System.out.println("1. Delete review");
            System.out.println("2. Go back");

            int action = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (action) {
                case 1:
                    deleteReview();
                    break;
                case 2:
                    // Go back to the admin actions menu
                    break;
                default:
                    System.out.println("Invalid action");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving reviews: " + e.getMessage());
        }
    }

    private void deleteReview() {
        System.out.println("Enter the ID of the review to delete:");
        int reviewId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        int retries = 5;
        while (retries-- > 0) {
            boolean success = executeDeleteReview(reviewId);
            if (success) {
                return;
            }
            System.out.println("Database is locked, retrying...");
            try {
                Thread.sleep(2000); // Increase wait time before retrying
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted, failed to complete operation");
            }
        }
        System.out.println("Failed to delete review after multiple attempts due to database lock.");
    }

    private boolean executeDeleteReview(int reviewId) {
        boolean isDeleted = false;
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM reviews WHERE id = ?")) {
            statement.setInt(1, reviewId);
            int rowsAffected = statement.executeUpdate();
            isDeleted = (rowsAffected > 0);
            if (isDeleted) {
                System.out.println("Review deleted successfully.");
            } else {
                System.out.println("No review found with the specified ID.");
            }
        } catch (SQLException e) {
            if (!e.getMessage().contains("database is locked")) {
                System.out.println("Error deleting review: " + e.getMessage());
                isDeleted = true; // No need to retry if the error is not related to a database lock
            }
        }
        return isDeleted;
    }
}
