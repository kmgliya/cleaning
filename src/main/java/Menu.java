import java.util.Scanner;

public class Menu {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Display role selection menu
        System.out.println("Choose a role:");
        System.out.println("1. Administrator");
        System.out.println("2. Client");

        // Get user choice
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        // Process user choice
        switch (choice) {
            case 1:
                AdminManager.adminLogin();
                break;
            case 2:
                ClientManager.clientLogin();
                break;
            default:
                System.out.println("Invalid choice");
        }
        scanner.close();
    }
}
