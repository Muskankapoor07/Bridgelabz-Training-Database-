package payroll.repository;

import payroll.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("role")
            );
        }
    };

    public User findByUsername(String username) {

        String sql = """
                SELECT id,
                       username,
                       password,
                       email,
                       role
                FROM users
                WHERE username = ?
                """;

        try {

            return jdbcTemplate.queryForObject(
                    sql,
                    userRowMapper,
                    username
            );

        } catch (EmptyResultDataAccessException e) {

            return null;

        }

    }

    public void registerUser(String username,
                             String hashedPassword,
                             String email,
                             String role) {

        String sql = """
                INSERT INTO users
                (
                    username,
                    password,
                    email,
                    role
                )
                VALUES
                (
                    ?,
                    ?,
                    ?,
                    ?
                )
                """;

        jdbcTemplate.update(
                sql,
                username,
                hashedPassword,
                email,
                role
        );

    }

}