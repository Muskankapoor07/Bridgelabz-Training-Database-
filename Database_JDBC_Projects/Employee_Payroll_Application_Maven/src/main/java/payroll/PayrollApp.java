package payroll;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import payroll.config.AppConfig;
import payroll.model.Employee;
import payroll.model.User;
import payroll.repository.EmployeeRepository;
import payroll.repository.UserRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PayrollApp {

    private static final Scanner scanner = new Scanner(System.in);

    private static User currentUser;

    private static UserRepository userRepository;

    private static EmployeeRepository employeeRepository;

    private static final List<String> AVAILABLE_PROFILES = Arrays.asList(

            "ellipse-1.png",
            "ellipse-2.png",
            "ellipse-3.png",
            "ellipse-4.png"

    );

    public static void main(String[] args) {

        loadEnvironmentVariables();

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        userRepository = context.getBean(UserRepository.class);

        employeeRepository = context.getBean(EmployeeRepository.class);

        System.out.println();

        System.out.println("======================================");
        System.out.println(" Employee Payroll Application ");
        System.out.println("======================================");

        while (true) {

            if (currentUser == null) {

                showAnonymousMenu();

            } else if ("ADMIN".equals(currentUser.getRole())) {

                showAdminMenu();

            } else {

                showUserMenu();

            }

        }

    }

    private static void loadEnvironmentVariables() {

        try {

            if (Files.exists(Paths.get(".env"))) {

                List<String> lines = Files.readAllLines(Paths.get(".env"));

                for (String line : lines) {

                    line = line.trim();

                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    String[] parts = line.split("=", 2);

                    if (parts.length == 2) {

                        System.setProperty(
                                parts[0].trim(),
                                parts[1].trim()
                        );

                    }

                }

            } else {

                System.out.println(".env file not found.");

            }

        } catch (IOException e) {

            System.out.println("Unable to load .env file.");

        }

    }

    private static void showAnonymousMenu() {

        System.out.println("\n========== EMPLOYEE PAYROLL ==========");

        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");

        System.out.print("Enter Choice : ");

        String choice = scanner.nextLine();

        switch (choice) {

            case "1":
                login();
                break;

            case "2":
                register();
                break;

            case "3":
                System.out.println("Thank You...");
                System.exit(0);
                break;

            default:
                System.out.println("Invalid Choice.");

        }

    }

    private static void showAdminMenu() {

        System.out.println("\n========== ADMIN MENU ==========");

        System.out.println("Welcome : " + currentUser.getUsername());

        System.out.println("1. Add Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. Edit Employee");
        System.out.println("4. Delete Employee");
        System.out.println("5. Department Payroll");
        System.out.println("6. View Audit Logs");
        System.out.println("7. Logout");

        System.out.print("Enter Choice : ");

        String choice = scanner.nextLine();

        switch (choice) {

            case "1":
                addEmployee();
                break;

            case "2":
                viewAllEmployees();
                break;

            case "3":
                editEmployee();
                break;

            case "4":
                deleteEmployee();
                break;

            case "5":
                getDeptPayroll();
                break;

            case "6":
                viewAuditLogs();
                break;

            case "7":
                logout();
                break;

            default:
                System.out.println("Invalid Choice.");

        }

    }

    private static void showUserMenu() {

        System.out.println("\n========== USER MENU ==========");

        System.out.println("Welcome : " + currentUser.getUsername());

        System.out.println("1. View My Payroll");
        System.out.println("2. Logout");

        System.out.print("Enter Choice : ");

        String choice = scanner.nextLine();

        switch (choice) {

            case "1":
                viewMyDetails();
                break;

            case "2":
                logout();
                break;

            default:
                System.out.println("Invalid Choice.");

        }

    }

    private static void register() {

        System.out.println("\n========== REGISTER ==========");

        System.out.print("Enter Username : ");
        String username = scanner.nextLine();

        System.out.print("Enter Password : ");
        String password = scanner.nextLine();

        System.out.print("Enter Email : ");
        String email = scanner.nextLine();

        System.out.print("Enter Role (ADMIN/USER) : ");
        String role = scanner.nextLine().toUpperCase();

        if (!role.equals("ADMIN") && !role.equals("USER")) {

            System.out.println("Invalid Role.");

            return;

        }

        try {

            userRepository.registerUser(
                    username,
                    payroll.util.HashUtil.hashPassword(password),
                    email,
                    role
            );

            System.out.println("\nRegistration Successful.");

        } catch (Exception e) {

            System.out.println("\nUsername or Email Already Exists.");

        }

    }

    private static void login() {

        System.out.println("\n========== LOGIN ==========");

        System.out.print("Enter Username : ");
        String username = scanner.nextLine();

        System.out.print("Enter Password : ");
        String password = scanner.nextLine();

        User user = userRepository.findByUsername(username);

        if (user == null) {

            System.out.println("Username Not Found.");

            return;

        }

        String hashedPassword =
                payroll.util.HashUtil.hashPassword(password);

        if (user.getPassword().equals(hashedPassword)) {

            currentUser = user;

            System.out.println("\nLogin Successful.");

            System.out.println("Welcome " + currentUser.getUsername());

        } else {

            System.out.println("Incorrect Password.");

        }

    }

    private static void logout() {

        currentUser = null;

        System.out.println("\nLogout Successful.");

    }

    private static void addEmployee() {

        System.out.println("\n========== ADD EMPLOYEE ==========");

        System.out.print("Enter Name : ");
        String name = scanner.nextLine();

        System.out.println();

        System.out.println("Available Profile Images");

        for (int i = 0; i < AVAILABLE_PROFILES.size(); i++) {

            System.out.println((i + 1) + ". " + AVAILABLE_PROFILES.get(i));

        }

        System.out.print("Choose Profile Image : ");

        int profileChoice = Integer.parseInt(scanner.nextLine());

        String profileImage = AVAILABLE_PROFILES.get(profileChoice - 1);

        System.out.print("\nEnter Gender (Male/Female) : ");

        String gender = scanner.nextLine();

        System.out.println();

        System.out.println("Departments");

        System.out.println("1. HR");
        System.out.println("2. Sales");
        System.out.println("3. Finance");
        System.out.println("4. Engineer");
        System.out.println("5. Others");

        System.out.println();

        System.out.print("Enter Departments (Example : 1,3,5) : ");

        String input = scanner.nextLine();

        List<String> departments = new java.util.ArrayList<>();

        for (String value : input.split(",")) {

            switch (value.trim()) {

                case "1":
                    departments.add("HR");
                    break;

                case "2":
                    departments.add("Sales");
                    break;

                case "3":
                    departments.add("Finance");
                    break;

                case "4":
                    departments.add("Engineer");
                    break;

                case "5":
                    departments.add("Others");
                    break;

            }

        }

        System.out.print("\nEnter Salary : ");

        BigDecimal salary = new BigDecimal(scanner.nextLine());

        System.out.print("Enter Start Date (yyyy-mm-dd) : ");

        java.time.LocalDate startDate =
                java.time.LocalDate.parse(scanner.nextLine());

        System.out.print("Enter Notes : ");

        String notes = scanner.nextLine();

        Employee employee = new Employee();

        employee.setName(name);
        employee.setProfileImage(profileImage);
        employee.setGender(gender);
        employee.setDepartments(departments);
        employee.setSalary(salary);
        employee.setStartDate(startDate);
        employee.setNotes(notes);
        employee.setCreatedBy(currentUser.getId());

        try {

            employeeRepository.addEmployee(employee);

            System.out.println();

            System.out.println("Employee Added Successfully.");

        } catch (Exception e) {

            System.out.println();

            System.out.println("Unable To Add Employee.");

            System.out.println(e.getMessage());

        }

    }

    private static void viewAllEmployees() {

        List<Employee> employees = employeeRepository.findAll();

        System.out.println("\n================ EMPLOYEE LIST ================");

        if (employees.isEmpty()) {

            System.out.println("No Employees Found.");

            return;

        }

        for (Employee employee : employees) {

            System.out.println("---------------------------------------------");

            System.out.println("ID          : " + employee.getId());

            System.out.println("Name        : " + employee.getName());

            System.out.println("Profile     : " + employee.getProfileImage());

            System.out.println("Gender      : " + employee.getGender());

            System.out.println("Departments : " + employee.getDepartments());

            System.out.println("Salary      : " + employee.getSalary());

            System.out.println("Start Date  : " + employee.getStartDate());

            System.out.println("Notes       : " + employee.getNotes());

        }

    }

    private static void editEmployee() {

        System.out.println("\n========== UPDATE EMPLOYEE ==========");

        System.out.print("Enter Employee ID : ");

        int id = Integer.parseInt(scanner.nextLine());

        Employee employee = employeeRepository.findById(id);

        if (employee == null) {

            System.out.println("Employee Not Found.");

            return;

        }

        System.out.println();

        System.out.println("Current Salary : " + employee.getSalary());

        System.out.print("Enter New Salary : ");

        BigDecimal salary = new BigDecimal(scanner.nextLine());

        System.out.println();

        System.out.println("Current Notes : " + employee.getNotes());

        System.out.print("Enter New Notes : ");

        String notes = scanner.nextLine();

        try {

            employeeRepository.updateEmployee(

                    id,
                    salary,
                    notes,
                    currentUser.getId()

            );

            System.out.println();

            System.out.println("Employee Updated Successfully.");

        } catch (Exception e) {

            System.out.println();

            System.out.println("Unable To Update Employee.");

            System.out.println(e.getMessage());

        }

    }

    private static void deleteEmployee() {

        System.out.println("\n========== DELETE EMPLOYEE ==========");

        System.out.print("Enter Employee ID : ");

        int id = Integer.parseInt(scanner.nextLine());

        try {

            employeeRepository.deleteEmployee(id);

            System.out.println();

            System.out.println("Employee Deleted Successfully.");

        } catch (Exception e) {

            System.out.println();

            System.out.println("Unable To Delete Employee.");

            System.out.println(e.getMessage());

        }

    }

    private static void getDeptPayroll() {

        System.out.println("\n========== DEPARTMENT PAYROLL ==========");

        System.out.print("Enter Department Name : ");

        String department = scanner.nextLine();

        try {

            BigDecimal total =
                    employeeRepository.getDeptPayroll(department);

            System.out.println();

            System.out.println("Department : " + department);

            System.out.println("Total Payroll : " + total);

        } catch (Exception e) {

            System.out.println("Unable To Calculate Payroll.");

        }

    }

    private static void viewAuditLogs() {

        List<Map<String, Object>> logs =
                employeeRepository.findAuditLogs();

        System.out.println("\n================ PAYROLL AUDIT LOGS ================");

        if (logs.isEmpty()) {

            System.out.println("No Audit Records Found.");

            return;

        }

        for (Map<String, Object> log : logs) {

            System.out.println("----------------------------------------");

            System.out.println("Audit ID      : " + log.get("id"));

            System.out.println("Employee ID   : " + log.get("employee_id"));

            System.out.println("Action        : " + log.get("action_type"));

            System.out.println("Old Salary    : " + log.get("old_salary"));

            System.out.println("New Salary    : " + log.get("new_salary"));

            System.out.println("Changed By    : " + log.get("changed_by"));

            System.out.println("Changed At    : " + log.get("changed_at"));

        }

    }

    private static void viewMyDetails() {

        System.out.println("\n========== MY PAYROLL ==========");

        Employee employee =
                employeeRepository.findByEmail(currentUser.getEmail());

        if (employee == null) {

            System.out.println("Employee Details Not Found.");

            return;

        }

        System.out.println("ID          : " + employee.getId());

        System.out.println("Name        : " + employee.getName());

        System.out.println("Profile     : " + employee.getProfileImage());

        System.out.println("Gender      : " + employee.getGender());

        System.out.println("Departments : " + employee.getDepartments());

        System.out.println("Salary      : " + employee.getSalary());

        System.out.println("Start Date  : " + employee.getStartDate());

        System.out.println("Notes       : " + employee.getNotes());

    }
}
