package payroll;

import payroll.model.Employee;
import payroll.model.User;
import payroll.util.DBUtil;
import payroll.util.HashUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PayrollApp {

    private static final Scanner scanner = new Scanner(System.in);

    private static User currentUser = null;

    static void main(String[] args) {

        while (true) {

            if (currentUser == null) {
                guestMenu();
            } else if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
                adminMenu();
            } else {
                userMenu();
            }

        }

    }
    // =====================================================
// Guest Menu
// =====================================================

    private static void guestMenu() {

        while (currentUser == null) {

            System.out.println("\n======================================");
            System.out.println("      EMPLOYEE PAYROLL SYSTEM");
            System.out.println("======================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter Choice : ");

            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input!");
                continue;
            }

            switch (choice) {

                case 1:
                    login();
                    break;

                case 2:
                    register();
                    break;

                case 3:
                    System.out.println("Thank You...");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid Choice.");

            }

        }

    }

    // =====================================================
// Admin Menu
// =====================================================

    private static void adminMenu() {

        while (currentUser != null && currentUser.getRole().equalsIgnoreCase("ADMIN")) {

            System.out.println("\n======================================");
            System.out.println("           ADMIN PANEL");
            System.out.println("======================================");
            System.out.println("Welcome : " + currentUser.getUsername());

            System.out.println("\n1. Add Employee");
            System.out.println("2. View All Employees");
            System.out.println("3. Update Employee");
            System.out.println("4. Delete Employee");
            System.out.println("5. Department Wise Payroll");
            System.out.println("6. View Audit Logs");
            System.out.println("7. Logout");

            System.out.print("\nEnter Choice : ");

            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input!");
                continue;
            }

            switch (choice) {

                case 1:
                    addEmployee();
                    break;

                case 2:
                    viewEmployees();
                    break;

                case 3:
                    updateEmployee();
                    break;

                case 4:
                    deleteEmployee();
                    break;

                case 5:
                    departmentPayroll();
                    break;

                case 6:
                    auditLogs();
                    break;

                case 7:
                    logout();
                    break;

                default:
                    System.out.println("Invalid Choice.");
            }

        }

    }
    // =====================================================
// User Menu
// =====================================================

    private static void userMenu() {

        while (currentUser != null && currentUser.getRole().equalsIgnoreCase("USER")) {

            System.out.println("\n======================================");
            System.out.println("            USER PANEL");
            System.out.println("======================================");
            System.out.println("Welcome : " + currentUser.getUsername());

            System.out.println("\n1. View My Payroll");
            System.out.println("2. Logout");

            System.out.print("\nEnter Choice : ");

            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input!");
                continue;
            }

            switch (choice) {

                case 1:
                    viewMyPayroll();
                    break;

                case 2:
                    logout();
                    break;

                default:
                    System.out.println("Invalid Choice.");

            }

        }

    }
    // =====================================================
// Register User
// =====================================================

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

        String sql = """
                INSERT INTO users
                (username,password,email,role)
                VALUES
                (?,?,?,?)
                """;

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            String hashedPassword = HashUtil.hashPassword(password);

            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, email);
            ps.setString(4, role);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("\nRegistration Successful.");
            } else {
                System.out.println("\nRegistration Failed.");
            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

    }

    // =====================================================
// Add Employee
// =====================================================

    private static void addEmployee() {

        System.out.println("\n========== ADD EMPLOYEE ==========");

        System.out.print("Enter Name : ");
        String name = scanner.nextLine();

        System.out.print("Enter Profile Image : ");
        String profileImage = scanner.nextLine();

        System.out.print("Enter Gender (Male/Female) : ");
        String gender = scanner.nextLine();

        System.out.println("\nDepartments");
        System.out.println("1. HR");
        System.out.println("2. Sales");
        System.out.println("3. Finance");
        System.out.println("4. Engineer");
        System.out.println("5. Others");

        System.out.print("Enter Departments (Example : 1,3,5) : ");
        String input = scanner.nextLine();

        List<String> departments = new ArrayList<>();

        String[] arr = input.split(",");

        for (String s : arr) {

            switch (Integer.parseInt(s.trim())) {

                case 1:
                    departments.add("HR");
                    break;

                case 2:
                    departments.add("Sales");
                    break;

                case 3:
                    departments.add("Finance");
                    break;

                case 4:
                    departments.add("Engineer");
                    break;

                case 5:
                    departments.add("Others");
                    break;
            }
        }

        System.out.print("Enter Salary : ");
        BigDecimal salary = scanner.nextBigDecimal();
        scanner.nextLine();

        System.out.print("Enter Start Date (yyyy-mm-dd) : ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter Notes : ");
        String notes = scanner.nextLine();

        String empSql = """
            INSERT INTO employees
            (
                name,
                profile_image,
                gender,
                salary,
                start_date,
                notes,
                created_by
            )
            VALUES
            (?,?,?,?,?,?,?)
            """;

        String deptSql = """
            INSERT INTO employee_departments
            (
                employee_id,
                department
            )
            VALUES
            (?,?)
            """;

        try (Connection conn = DBUtil.getConnection()) {

            conn.setAutoCommit(false);

            PreparedStatement empPs = conn.prepareStatement(
                    empSql,
                    Statement.RETURN_GENERATED_KEYS
            );

            empPs.setString(1, name);
            empPs.setString(2, profileImage);
            empPs.setString(3, gender);
            empPs.setBigDecimal(4, salary);
            empPs.setDate(5, Date.valueOf(startDate));
            empPs.setString(6, notes);
            empPs.setInt(7, currentUser.getId());

            empPs.executeUpdate();

            ResultSet rs = empPs.getGeneratedKeys();

            int employeeId = 0;

            if (rs.next()) {
                employeeId = rs.getInt(1);
            }

            PreparedStatement deptPs = conn.prepareStatement(deptSql);

            for (String department : departments) {

                deptPs.setInt(1, employeeId);
                deptPs.setString(2, department);
                deptPs.executeUpdate();

            }

            conn.commit();

            System.out.println("\nEmployee Added Successfully.");

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    // =====================================================
// View All Employees
// =====================================================

    private static void viewEmployees() {

        String sql = """
                SELECT
                    e.id,
                    e.name,
                    e.profile_image,
                    e.gender,
                    STRING_AGG(d.department, ', ') AS departments,
                    e.salary,
                    e.start_date,
                    e.notes,
                    u.username
                FROM employees e
                LEFT JOIN employee_departments d
                    ON e.id = d.employee_id
                LEFT JOIN users u
                    ON e.created_by = u.id
                GROUP BY
                    e.id,
                    e.name,
                    e.profile_image,
                    e.gender,
                    e.salary,
                    e.start_date,
                    e.notes,
                    u.username
                ORDER BY e.id;
                """;

        try (Connection conn = DBUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n================ EMPLOYEE LIST ================\n");

            while (rs.next()) {

                System.out.println("Employee ID      : " + rs.getInt("id"));
                System.out.println("Name             : " + rs.getString("name"));
                System.out.println("Profile Image    : " + rs.getString("profile_image"));
                System.out.println("Gender           : " + rs.getString("gender"));
                System.out.println("Departments      : " + rs.getString("departments"));
                System.out.println("Salary           : " + rs.getBigDecimal("salary"));
                System.out.println("Start Date       : " + rs.getDate("start_date"));
                System.out.println("Notes            : " + rs.getString("notes"));
                System.out.println("Created By       : " + rs.getString("username"));

                System.out.println("---------------------------------------------");

            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

    }

    // =====================================================
// Update Employee
// =====================================================

    private static void updateEmployee() {

        System.out.println("\n========== UPDATE EMPLOYEE ==========");

        System.out.print("Enter Employee ID : ");
        int id = Integer.parseInt(scanner.nextLine());

        String checkSql = """
                SELECT *
                FROM employees
                WHERE id = ?
                """;

        String updateSql = """
                UPDATE employees
                SET
                    name = ?,
                    salary = ?,
                    notes = ?
                WHERE id = ?
                """;

        try (Connection conn = DBUtil.getConnection()) {

            PreparedStatement checkPs = conn.prepareStatement(checkSql);

            checkPs.setInt(1, id);

            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {

                System.out.println("Employee Not Found.");
                return;

            }

            System.out.println("\nCurrent Name   : " + rs.getString("name"));
            System.out.println("Current Salary : " + rs.getBigDecimal("salary"));
            System.out.println("Current Notes  : " + rs.getString("notes"));

            System.out.print("\nEnter New Name : ");
            String name = scanner.nextLine();

            System.out.print("Enter New Salary : ");
            BigDecimal salary = scanner.nextBigDecimal();
            scanner.nextLine();

            System.out.print("Enter New Notes : ");
            String notes = scanner.nextLine();

            PreparedStatement updatePs = conn.prepareStatement(updateSql);

            updatePs.setString(1, name);
            updatePs.setBigDecimal(2, salary);
            updatePs.setString(3, notes);
            updatePs.setInt(4, id);

            int rows = updatePs.executeUpdate();

            if (rows > 0) {

                System.out.println("\nEmployee Updated Successfully.");

            } else {

                System.out.println("\nUpdate Failed.");

            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

    }

    // =====================================================
// Delete Employee
// =====================================================

    private static void deleteEmployee() {

        System.out.println("\n========== DELETE EMPLOYEE ==========");

        System.out.print("Enter Employee ID : ");
        int id = Integer.parseInt(scanner.nextLine());

        String checkSql = """
                SELECT *
                FROM employees
                WHERE id = ?
                """;

        String deleteDeptSql = """
                DELETE FROM employee_departments
                WHERE employee_id = ?
                """;

        String deleteEmpSql = """
                DELETE FROM employees
                WHERE id = ?
                """;

        try (Connection conn = DBUtil.getConnection()) {

            conn.setAutoCommit(false);

            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, id);

            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {

                System.out.println("\nEmployee Not Found.");

                conn.rollback();

                return;

            }

            PreparedStatement deptPs = conn.prepareStatement(deleteDeptSql);
            deptPs.setInt(1, id);
            deptPs.executeUpdate();

            PreparedStatement empPs = conn.prepareStatement(deleteEmpSql);
            empPs.setInt(1, id);

            int rows = empPs.executeUpdate();

            if (rows > 0) {

                conn.commit();

                System.out.println("\nEmployee Deleted Successfully.");

            } else {

                conn.rollback();

                System.out.println("\nDelete Failed.");

            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

    }

    // =====================================================
// Department Wise Payroll
// =====================================================

    private static void departmentPayroll() {

        System.out.println("\n========== DEPARTMENT PAYROLL ==========");

        System.out.println("1. HR");
        System.out.println("2. Sales");
        System.out.println("3. Finance");
        System.out.println("4. Engineer");
        System.out.println("5. Others");

        System.out.print("Choose Department : ");

        int choice = Integer.parseInt(scanner.nextLine());

        String department = "";

        switch (choice) {

            case 1:
                department = "HR";
                break;

            case 2:
                department = "Sales";
                break;

            case 3:
                department = "Finance";
                break;

            case 4:
                department = "Engineer";
                break;

            case 5:
                department = "Others";
                break;

            default:
                System.out.println("Invalid Choice.");
                return;

        }

        String sql = "{ ? = call get_total_payroll_by_dept(?) }";

        try (Connection conn = DBUtil.getConnection(); CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.NUMERIC);

            cs.setString(2, department);

            cs.execute();

            BigDecimal total = cs.getBigDecimal(1);

            System.out.println("\nDepartment : " + department);
            System.out.println("Total Payroll : " + total);

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

    }
    // =====================================================
// View Audit Logs
// =====================================================

    private static void auditLogs() {

        String sql = """
                SELECT *
                FROM payroll_audit
                ORDER BY id
                """;

        try (Connection conn = DBUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n============= PAYROLL AUDIT LOGS =============\n");

            while (rs.next()) {

                System.out.println("Audit ID      : " + rs.getInt("id"));
                System.out.println("Employee ID   : " + rs.getInt("employee_id"));
                System.out.println("Action        : " + rs.getString("action_type"));
                System.out.println("Old Salary    : " + rs.getBigDecimal("old_salary"));
                System.out.println("New Salary    : " + rs.getBigDecimal("new_salary"));
                System.out.println("Changed By    : " + rs.getString("changed_by"));
                System.out.println("Changed At    : " + rs.getTimestamp("changed_at"));

                System.out.println("--------------------------------------------");

            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

    }

    // =====================================================
// View My Payroll
// =====================================================

    private static void viewMyPayroll() {

        String sql = """
                SELECT
                    e.id,
                    e.name,
                    e.gender,
                    STRING_AGG(d.department, ', ') AS departments,
                    e.salary,
                    e.start_date,
                    e.notes
                FROM employees e
                LEFT JOIN employee_departments d
                ON e.id = d.employee_id
                WHERE e.user_id = ?
                GROUP BY
                e.id,
                e.name,
                e.gender,
                e.salary,
                e.start_date,
                e.notes
                """;

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentUser.getId());

            ResultSet rs = ps.executeQuery();

            System.out.println("\n============= MY PAYROLL =============\n");

            while (rs.next()) {

                System.out.println("Employee ID : " + rs.getInt("id"));
                System.out.println("Name        : " + rs.getString("name"));
                System.out.println("Department  : " + rs.getString("departments"));
                System.out.println("Salary      : " + rs.getBigDecimal("salary"));
                System.out.println("Start Date  : " + rs.getDate("start_date"));
                System.out.println("Notes       : " + rs.getString("notes"));

                System.out.println("--------------------------------------");

            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

    }
    // =====================================================
// Login
// =====================================================

    private static void login() {

        System.out.println("\n========== LOGIN ==========");

        System.out.print("Enter Username : ");
        String username = scanner.nextLine();

        System.out.print("Enter Password : ");
        String password = scanner.nextLine();

        String sql = """
                SELECT *
                FROM users
                WHERE username = ?
                """;

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String dbPassword = rs.getString("password");

                String inputPassword = HashUtil.hashPassword(password);

                if (dbPassword.equals(inputPassword)) {

                    currentUser = new User();

                    currentUser.setId(rs.getInt("id"));
                    currentUser.setUsername(rs.getString("username"));
                    currentUser.setPassword(rs.getString("password"));
                    currentUser.setEmail(rs.getString("email"));
                    currentUser.setRole(rs.getString("role"));

                    System.out.println("\nLogin Successful.");
                    System.out.println("Welcome " + currentUser.getUsername());

                } else {

                    System.out.println("\nIncorrect Password.");

                }

            } else {

                System.out.println("\nUser Not Found.");

            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

    }

// =====================================================
// Logout
// =====================================================

    private static void logout() {

        currentUser = null;

        System.out.println("\nLogout Successful.");

    }

}
