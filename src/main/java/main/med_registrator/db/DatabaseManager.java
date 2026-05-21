package main.med_registrator.db;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:med_registrator.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void init() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        try (Statement st = connection.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS questionnaires (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
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
        }
    }

    public int saveQuestionnaire(String symptoms, int totalScore, String chronic, int age) throws SQLException {
        String sql = "INSERT INTO questionnaires (symptoms, total_score, chronic, age) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, symptoms);
            ps.setInt(2, totalScore);
            ps.setString(3, chronic);
            ps.setInt(4, age);
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
}