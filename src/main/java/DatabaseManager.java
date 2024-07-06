import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:src/main/resources/date.db";
    private static final int TIMEOUT = 30; // Время ожидания в секундах

    public static Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        connection.setAutoCommit(true);
        connection.createStatement().execute("PRAGMA busy_timeout = " + (TIMEOUT * 1000)); // Increase timeout

        // Enable foreign key support
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            System.out.println("Failed to enable foreign keys: " + e.getMessage());
        }

        // Enable WAL mode
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA journal_mode = WAL;");
        } catch (SQLException e) {
            System.out.println("Failed to set journal mode to WAL: " + e.getMessage());
        }

        return connection;
    }

    public static String getUserName(int userId) {
        String userName = "";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("SELECT name FROM clients WHERE id = ?")) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userName = resultSet.getString("name");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user name: " + e.getMessage());
        }
        return userName;
    }

    public static void updateReviewsTable() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            statement.execute("PRAGMA foreign_keys = OFF;");

            statement.execute("CREATE TABLE reviews_new (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "service_id INTEGER NOT NULL, " +
                    "review TEXT NOT NULL, " +
                    "FOREIGN KEY (service_id) REFERENCES services(id)" +
                    ");");

            statement.execute("INSERT INTO reviews_new (id, service_id, review) " +
                    "SELECT id, service_id, review FROM reviews;");

            // Example of using executeDeleteReview(int)
            boolean deleteResult = executeDeleteReview(5); // Assuming you want to delete review with ID 5
            if (deleteResult) {
                System.out.println("Review with ID 5 deleted successfully.");
            }

            statement.execute("DROP TABLE reviews;");

            statement.execute("ALTER TABLE reviews_new RENAME TO reviews;");

            statement.execute("PRAGMA foreign_keys = ON;");

            System.out.println("Table 'reviews' updated with foreign key.");
        } catch (SQLException e) {
            System.out.println("Error updating 'reviews' table: " + e.getMessage());
        }
    }

    public static boolean executeDeleteReview(int reviewId) {
        boolean success = false;
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM reviews WHERE id = ?")) {
            statement.setInt(1, reviewId);
            int retries = 0;
            while (!success && retries < 3) {
                try {
                    statement.executeUpdate();
                    success = true;
                } catch (SQLException e) {
                    if (e.getErrorCode() == 5) { // SQLITE_BUSY error code
                        retries++;
                        System.out.println("Database is locked, retrying... (Attempt " + retries + ")");
                    } else {
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting review: " + e.getMessage());
        }
        return success;
    }

    public static void main(String[] args) {
        updateReviewsTable();
        // Other initialization code...
    }
}
