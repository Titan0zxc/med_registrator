package main.med_registrator.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:med_registrator.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public void init() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        try (Statement st = connection.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS questionnaires (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT,
                    symptoms TEXT,
                    total_score INTEGER,
                    chronic TEXT,
                    age INTEGER,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS appeals (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    questionnaire_id INTEGER,
                    priority TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS tickets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    appeal_id INTEGER,
                    type TEXT,
                    details TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )""");
            try {
                st.execute("ALTER TABLE questionnaires ADD COLUMN full_name TEXT");
            } catch (SQLException ignored) {}
        }
    }

    public int saveQuestionnaire(String fullName, String symptoms, int totalScore,
                                 String chronic, int age) throws SQLException {
        String sql = "INSERT INTO questionnaires (full_name, symptoms, total_score, chronic, age) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fullName);
            ps.setString(2, symptoms);
            ps.setInt(3, totalScore);
            ps.setString(4, chronic);
            ps.setInt(5, age);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public int saveAppeal(int questionnaireId, String priority) throws SQLException {
        String sql = "INSERT INTO appeals (questionnaire_id, priority) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, questionnaireId);
            ps.setString(2, priority);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public void saveTicket(int appealId, String type, String details) throws SQLException {
        String sql = "INSERT INTO tickets (appeal_id, type, details) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, appealId);
            ps.setString(2, type);
            ps.setString(3, details);
            ps.executeUpdate();
        }
    }

    public List<String[]> getAllAppeals() throws SQLException {
        String sql = """
            SELECT a.id, q.full_name, q.age, a.priority, a.created_at, t.type, t.details
            FROM appeals a
            JOIN questionnaires q ON a.questionnaire_id = q.id
            LEFT JOIN tickets t ON t.appeal_id = a.id
            ORDER BY a.created_at DESC
        """;
        List<String[]> result = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new String[]{
                        rs.getString("id"),
                        rs.getString("full_name"),
                        rs.getString("age"),
                        rs.getString("priority"),
                        rs.getString("created_at"),
                        rs.getString("type"),
                        rs.getString("details")
                });
            }
        }
        return result;
    }

    // Статистика за период: from/to в формате 'YYYY-MM-DD'
    public Map<String, Integer> getStatsByPeriod(String from, String to) throws SQLException {
        String sql = """
            SELECT priority, COUNT(*) as cnt
            FROM appeals
            WHERE created_at >= ? AND created_at <= ? || ' 23:59:59'
            GROUP BY priority
        """;
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("RED", 0);
        stats.put("YELLOW", 0);
        stats.put("GREEN", 0);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, from);
            ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("priority"), rs.getInt("cnt"));
                }
            }
        }
        return stats;
    }

    // Все обращения за период (для таблицы в статистике)
    public List<String[]> getAppealsByPeriod(String from, String to) throws SQLException {
        String sql = """
            SELECT a.id, q.full_name, q.age, a.priority, a.created_at, t.type
            FROM appeals a
            JOIN questionnaires q ON a.questionnaire_id = q.id
            LEFT JOIN tickets t ON t.appeal_id = a.id
            WHERE a.created_at >= ? AND a.created_at <= ? || ' 23:59:59'
            ORDER BY a.created_at DESC
        """;
        List<String[]> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, from);
            ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new String[]{
                            rs.getString("id"),
                            rs.getString("full_name"),
                            rs.getString("age"),
                            rs.getString("priority"),
                            rs.getString("created_at"),
                            rs.getString("type")
                    });
                }
            }
        }
        return result;
    }
}