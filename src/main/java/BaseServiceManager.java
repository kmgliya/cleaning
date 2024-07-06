import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseServiceManager {

    public static double getBasePrice(int serviceId) {
        double basePrice = 0.0;
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement("SELECT base_price FROM services WHERE id = ?")) {
            statement.setInt(1, serviceId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                basePrice = resultSet.getDouble("base_price");
            }
        } catch (SQLException e) {
            System.out.println("Error when receiving the basic price of the service: " + e.getMessage());
        }
        return basePrice;
    }
}
