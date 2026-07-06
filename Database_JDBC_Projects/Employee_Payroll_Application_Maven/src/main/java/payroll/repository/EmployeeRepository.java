package payroll.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import payroll.model.Employee;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class EmployeeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final RowMapper<Employee> employeeRowMapper = new RowMapper<Employee>() {

        @Override
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {

            Employee employee = new Employee();

            employee.setId(rs.getInt("id"));
            employee.setName(rs.getString("name"));
            employee.setProfileImage(rs.getString("profile_image"));
            employee.setGender(rs.getString("gender"));

            String dept = rs.getString("departments");

            if (dept != null) {
                employee.setDepartments(Arrays.asList(dept.split(",")));
            }

            employee.setSalary(rs.getBigDecimal("salary"));
            employee.setStartDate(rs.getDate("start_date").toLocalDate());
            employee.setNotes(rs.getString("notes"));
            employee.setCreatedBy(rs.getInt("created_by"));

            return employee;
        }
    };

    public void addEmployee(Employee employee) {

        transactionTemplate.execute(status -> {

            String employeeSql = """
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
                    (
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?
                    )
                    RETURNING id
                    """;

            Integer employeeId = jdbcTemplate.queryForObject(
                    employeeSql,
                    Integer.class,
                    employee.getName(),
                    employee.getProfileImage(),
                    employee.getGender(),
                    employee.getSalary(),
                    Date.valueOf(employee.getStartDate()),
                    employee.getNotes(),
                    employee.getCreatedBy()
            );

            String departmentSql = """
                    INSERT INTO employee_departments
                    (
                        employee_id,
                        department
                    )
                    VALUES
                    (
                        ?,
                        ?
                    )
                    """;

            for (String department : employee.getDepartments()) {

                jdbcTemplate.update(
                        departmentSql,
                        employeeId,
                        department
                );
            }

            return null;
        });

    }
    public List<Employee> findAll() {

        String sql = """
                SELECT
                    e.id,
                    e.name,
                    e.profile_image,
                    e.gender,
                    STRING_AGG(ed.department, ',') AS departments,
                    e.salary,
                    e.start_date,
                    e.notes,
                    e.created_by
                FROM employees e
                LEFT JOIN employee_departments ed
                    ON e.id = ed.employee_id
                GROUP BY
                    e.id,
                    e.name,
                    e.profile_image,
                    e.gender,
                    e.salary,
                    e.start_date,
                    e.notes,
                    e.created_by
                ORDER BY e.id
                """;

        return jdbcTemplate.query(sql, employeeRowMapper);

    }

    public Employee findById(int id) {

        String sql = """
                SELECT
                    e.id,
                    e.name,
                    e.profile_image,
                    e.gender,
                    STRING_AGG(ed.department, ',') AS departments,
                    e.salary,
                    e.start_date,
                    e.notes,
                    e.created_by
                FROM employees e
                LEFT JOIN employee_departments ed
                    ON e.id = ed.employee_id
                WHERE e.id = ?
                GROUP BY
                    e.id,
                    e.name,
                    e.profile_image,
                    e.gender,
                    e.salary,
                    e.start_date,
                    e.notes,
                    e.created_by
                """;

        List<Employee> list = jdbcTemplate.query(sql, employeeRowMapper, id);

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);

    }

    public Employee findByEmail(String email) {

        String sql = """
                SELECT
                    e.id,
                    e.name,
                    e.profile_image,
                    e.gender,
                    STRING_AGG(ed.department, ',') AS departments,
                    e.salary,
                    e.start_date,
                    e.notes,
                    e.created_by
                FROM employees e
                JOIN users u
                    ON e.created_by = u.id
                LEFT JOIN employee_departments ed
                    ON e.id = ed.employee_id
                WHERE u.email = ?
                GROUP BY
                    e.id,
                    e.name,
                    e.profile_image,
                    e.gender,
                    e.salary,
                    e.start_date,
                    e.notes,
                    e.created_by
                """;

        List<Employee> list = jdbcTemplate.query(sql, employeeRowMapper, email);

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);

    }
    public void updateEmployee(int id,
                               BigDecimal salary,
                               String notes,
                               int adminId) {

        transactionTemplate.execute(status -> {

            String sql = """
                    UPDATE employees
                    SET salary = ?,
                        notes = ?,
                        created_by = ?
                    WHERE id = ?
                    """;

            int rows = jdbcTemplate.update(
                    sql,
                    salary,
                    notes,
                    adminId,
                    id
            );

            if (rows == 0) {

                status.setRollbackOnly();

                throw new RuntimeException("Employee Not Found.");

            }

            return null;

        });

    }

    public void deleteEmployee(int id) {

        transactionTemplate.execute(status -> {

            String sql = """
                    DELETE FROM employees
                    WHERE id = ?
                    """;

            int rows = jdbcTemplate.update(sql, id);

            if (rows == 0) {

                status.setRollbackOnly();

                throw new RuntimeException("Employee Not Found.");

            }

            return null;

        });

    }
    public BigDecimal getDeptPayroll(String department) {

        String sql = "SELECT get_total_payroll_by_dept(?)";

        BigDecimal total = jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                department
        );

        return total == null ? BigDecimal.ZERO : total;

    }

    public List<Map<String, Object>> findAuditLogs() {

        String sql = """
                SELECT
                    id,
                    employee_id,
                    action_type,
                    old_salary,
                    new_salary,
                    changed_by,
                    changed_at
                FROM payroll_audit
                ORDER BY changed_at DESC
                """;

        return jdbcTemplate.queryForList(sql);

    }

}



