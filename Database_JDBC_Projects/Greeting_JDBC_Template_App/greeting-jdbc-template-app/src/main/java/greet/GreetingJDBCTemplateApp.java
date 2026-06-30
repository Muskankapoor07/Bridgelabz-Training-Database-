package greet;
import greet.config.AppConfig;
import greet.model.Greeting;
import greet.model.User;
import greet.repository.GreetingRepository;
import greet.repository.UserRepository;
import greet.util.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
@Component
public class GreetingJDBCTemplateApp {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GreetingRepository greetingRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;
    private static final Scanner scanner = new Scanner(System.in);
    private User currentUser = null; // Session holder
    public static void main(String[] args) {
        // Load configurations from .env before initializing Spring Context
        loadEnvironmentVariables();
        // Bootstrap Spring ApplicationContext container
        AnnotationConfigApplicationContext context = new
                AnnotationConfigApplicationContext(AppConfig.class);

        // Retrieve main application bean
        GreetingJDBCTemplateApp app =
                context.getBean(GreetingJDBCTemplateApp.class);

        System.out.println("=== Greeting Spring JdbcTemplate Console ===");
        app.runLoop();

        context.close();
    }
    // Helper method to load .env variables into System Properties
    private static void loadEnvironmentVariables() {
        try {
            if (Files.exists(Paths.get(".env"))) {
                List<String> lines = Files.readAllLines(Paths.get(".env"));
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            System.setProperty(parts[0].trim(), parts[1].trim());
                        }
                    }
                }
            } else {
                System.out.println("Warning: .env file not found. Falling back to system environment variables.");
            }
        } catch (IOException e) {
            System.err.println("Failed to read .env file. " + e.getMessage());
        }
    }
    private void runLoop() {
        while (true) {
            if (currentUser == null) {
                showAnonymousMenu();
            } else {
                showUserMenu();
            }
        }
    }
    private void showAnonymousMenu() {
        System.out.println("\n1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Select Option: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                System.out.println("Goodbye!");
                System.exit(0);
            default:
                System.out.println("Invalid Option.");
        }
    }
    private void showUserMenu() {
        System.out.println("\n--- Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ") ---");
        System.out.println("1. View All Greetings");
        System.out.println("2. View Audit Logs (Trigger Verification)");
        System.out.println("3. Get User Greeting Count (Stored Procedure call)");

        if (currentUser.getRole().equals("ADMIN")) {
            System.out.println("4. Create a Greeting");
            System.out.println("5. Update a Greeting (Transaction Template Demo)");
            System.out.println("6. Delete a Greeting");
        }
        System.out.println("7. Logout");
        System.out.print("Select Option: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                viewGreetings();
                break;
            case "2":
                viewAuditLogs();
                break;
            case "3":
                viewUserGreetingCount();
                break;
            case "4":
                if (isAdmin()) createGreeting();
                else System.out.println("Access Denied: Admin role required.");
                break;
            case "5":
                if (isAdmin()) updateGreeting();
                else System.out.println("Access Denied: Admin role required.");
                break;
            case "6":
                if (isAdmin()) deleteGreeting();
                else System.out.println("Access Denied: Admin role required.");
                break;
            case "7":
                logout();
                break;
            default:
                System.out.println("Invalid Option.");
        }
    }
    private boolean isAdmin() {
        return currentUser != null && currentUser.getRole().equals("ADMIN");
    }
    private void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter role (ADMIN/USER): ");
        String role = scanner.nextLine().toUpperCase();
        if (!role.equals("ADMIN") && !role.equals("USER")) {
            System.out.println("Invalid role choice.");
            return;
        }
        User user = new User(0, username, HashUtil.hashPassword(password), email,
                role);
        try {
            userRepository.save(user);
            System.out.println("Registration successful! Please login.");
        } catch (Exception e) {
            System.err.println("Registration failed. Name or email duplicates database records.");
        }
    }
    private void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        User user = userRepository.findByUsername(username);
        if (user != null) {
            String hashedInput = HashUtil.hashPassword(password);
            if (user.getPassword().equals(hashedInput)) {
                currentUser = user;
                System.out.println("Login successful! Welcome " +
                        currentUser.getUsername());
            } else {
                System.out.println("Incorrect password.");
            }
        } else {
            System.out.println("Username not found.");
        }
    }
    private void logout() {
        System.out.println("Logged out.");
        currentUser = null;
    }
    private void viewGreetings() {
        List<Greeting> list = greetingRepository.findAll();
        System.out.println("\n--- GREETINGS CATALOG ---");
        for (Greeting g : list) {
            System.out.println(g);
        }
        if (list.isEmpty()) {
            System.out.println("No greetings found.");
        }
    }
    private void viewAuditLogs() {
        List<Map<String, Object>> logs = greetingRepository.getAuditLogs();
        System.out.println("\n--- GREETING CHANGE AUDIT LOGS (TRIGGER AUTOMATION) ---");
                System.out.printf("%-4s | %-9s | %-8s | %-25s | %-25s | %-12s\n",
                        "ID", "Greet_ID", "Action", "Old Message", "New Message", "Changed By");
                        System.out.println("------------------------------------------------------ ------------------------------------------");

        for (Map<String, Object> log : logs) {
            System.out.printf("%-4s | %-9s | %-8s | %-25s | %-25s | %-12s\n",
                    log.get("id"),
                    log.get("greeting_id"),
                    log.get("action_type"),
                    log.get("old_message") != null ? log.get("old_message") :
                            "NULL",
                    log.get("new_message") != null ? log.get("new_message") :
                            "NULL",
                    log.get("changed_by")
            );
        }
        if (logs.isEmpty()) {
            System.out.println("No audit logs found.");
        }
    }
    private void viewUserGreetingCount() {
        System.out.print("Enter username to check stats: ");
        String username = scanner.nextLine();
        try {
            int count = greetingRepository.getGreetingCountForUser(username);
            System.out.println("\n>> Statistics: User '" + username + "' has created " + count + " greeting(s).");
        } catch (Exception e) {
            System.err.println("Failed to query procedure count.");
        }
    }
    private void createGreeting() {
        System.out.print("Enter greeting message: ");
        String message = scanner.nextLine();
        try {
            greetingRepository.save(message, currentUser.getId());
            System.out.println("Greeting created.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateGreeting() {
        System.out.print("Enter Greeting ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter new greeting message: ");
        String message = scanner.nextLine();
        try {
            transactionTemplate.execute(status -> {
                int rows = greetingRepository.update(id, message);
                if (rows > 0) {
                    System.out.println("Greeting updated transactionally.");
                } else {
                    status.setRollbackOnly();
                    System.out.println("Greeting ID not found. Transaction rolled back.");
                }
                return null;
            });
        } catch (Exception e) {
            System.err.println("Transaction failed.");
            e.printStackTrace();
        }
    }
    private void deleteGreeting() {
        System.out.print("Enter Greeting ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());
        int rows = greetingRepository.delete(id);
        if (rows > 0) {
            System.out.println("Greeting deleted.");
        } else {
            System.out.println("Greeting ID not found.");
        }
    }
}