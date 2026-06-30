package greet.repository;
import greet.model.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
@Repository
public class GreetingRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final RowMapper<Greeting> greetingRowMapper = new RowMapper<Greeting>
            () {
        @Override
        public Greeting mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Greeting(
                    rs.getInt("id"),
                    rs.getString("message"),
                    rs.getInt("created_by"),
                    rs.getString("creator")
            );
        }
    };
    public List<Greeting> findAll() {
        String sql = "SELECT g.id, g.message, g.created_by, u.username AS creator " +
        "FROM greetings g " +
                "LEFT JOIN users u ON g.created_by = u.id " +
                "ORDER BY g.id ASC";
        return jdbcTemplate.query(sql, greetingRowMapper);
    }
    public void save(String message, int createdBy) {
        String sql = "INSERT INTO greetings (message, created_by) VALUES (?, ?)";
        jdbcTemplate.update(sql, message, createdBy);
    }
    public int update(int id, String message) {
        String sql = "UPDATE greetings SET message = ? WHERE id = ?";
        return jdbcTemplate.update(sql, message, id);
    }
    public int delete(int id) {
        String sql = "DELETE FROM greetings WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
    public int getGreetingCountForUser(String username) {
        String sql = "SELECT get_user_greeting_count(?)";
        return jdbcTemplate.queryForObject(sql, Integer.class, username);
    }
    public List<Map<String, Object>> getAuditLogs() {
        String sql = "SELECT id, greeting_id, action_type, old_message, new_message, changed_by " +"FROM greeting_audit ORDER BY changed_at DESC";
        return jdbcTemplate.queryForList(sql);
    }
}
