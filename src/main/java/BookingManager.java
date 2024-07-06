import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookingManager {

    public static void bookServiceInDB(int clientId, int serviceId, String bookingDate, String comments, double totalPrice) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO bookings (user_id, service_id, booking_date, comments, price) VALUES (?, ?, ?, ?, ?)")) {
            statement.setInt(1, clientId);
            statement.setInt(2, serviceId);
            statement.setString(3, bookingDate);
            statement.setString(4, comments);
            statement.setDouble(5, totalPrice);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Booking error: " + e.getMessage());
        }
    }
}
