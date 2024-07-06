public class ServiceManager {

    public static double calculatePrice(int serviceId, double area) {
        double basePrice = BaseServiceManager.getBasePrice(serviceId);
        return area * basePrice;
    }
}
