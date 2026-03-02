package rs.hbs.zarade.service;

import rs.hbs.zarade.HbsZaradeApp;
import rs.hbs.zarade.db.DatabaseConnection;
import rs.hbs.zarade.domain.LogEntry;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servis za logovanje svih promena u sistemu.
 * Svaka promena (INSERT, UPDATE, DELETE) se beleži u _Log tabelu.
 */
public class LogService {

    private static LogService instance;

    private LogService() {}

    public static LogService getInstance() {
        if (instance == null) {
            instance = new LogService();
        }
        return instance;
    }

    /**
     * Zapisuje log entry u bazu.
     */
    public void log(String action, String tableName, String recordId, String description) {
        String username = HbsZaradeApp.getCurrentUser();
        if (username == null) username = "SYSTEM";

        LogEntry entry = new LogEntry(username, action, tableName, recordId, description);
        saveToDatabase(entry);
    }

    /**
     * Zapisuje log za INSERT operaciju.
     */
    public void logInsert(String tableName, String recordId, String description) {
        log("INSERT", tableName, recordId, description);
    }

    /**
     * Zapisuje log za UPDATE operaciju.
     */
    public void logUpdate(String tableName, String recordId, String oldValue, String newValue, String description) {
        String username = HbsZaradeApp.getCurrentUser();
        if (username == null) username = "SYSTEM";

        LogEntry entry = new LogEntry(username, "UPDATE", tableName, recordId, description);
        entry.setOldValue(oldValue);
        entry.setNewValue(newValue);
        saveToDatabase(entry);
    }

    /**
     * Zapisuje log za DELETE operaciju.
     */
    public void logDelete(String tableName, String recordId, String description) {
        log("DELETE", tableName, recordId, description);
    }

    /**
     * Zapisuje log za login/logout.
     */
    public void logLogin(String username) {
        LogEntry entry = new LogEntry(username, "LOGIN", "Staff", username, "Korisnik se prijavio");
        saveToDatabase(entry);
    }

    public void logLogout(String username) {
        LogEntry entry = new LogEntry(username, "LOGOUT", "Staff", username, "Korisnik se odjavio");
        saveToDatabase(entry);
    }

    /**
     * Vraća poslednjih N log unosa.
     */
    public List<LogEntry> getRecentLogs(int limit) {
        List<LogEntry> logs = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " * FROM [_Log] ORDER BY ID DESC";

        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            Connection conn = dbConn.getMainConnection();
            if (conn != null) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        LogEntry entry = mapResultSet(rs);
                        logs.add(entry);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Greška pri čitanju log-a: " + e.getMessage());
        }

        return logs;
    }

    private void saveToDatabase(LogEntry entry) {
        // Koristi jednostavan INSERT u _Log tabelu
        // Struktura tabele se pretpostavlja da ima: ID, Timestamp, Username, Action, TableName, RecordId, Description
        String sql = "INSERT INTO [_Log] (Timestamp, Username, Action, TableName, RecordId, Description) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            Connection conn = dbConn.getMainConnection();
            if (conn != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setTimestamp(1, Timestamp.valueOf(entry.getTimestamp()));
                    pstmt.setString(2, entry.getUsername());
                    pstmt.setString(3, entry.getAction());
                    pstmt.setString(4, entry.getTableName());
                    pstmt.setString(5, entry.getRecordId());
                    pstmt.setString(6, entry.getDescription());
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            // Ne prekidaj aplikaciju zbog log greške, samo ispiši upozorenje
            System.err.println("Upozorenje: Nije moguće zapisati log: " + e.getMessage());
            // Ispiši u konzolu za debug
            System.out.println("LOG: " + entry);
        }
    }

    private LogEntry mapResultSet(ResultSet rs) throws SQLException {
        LogEntry entry = new LogEntry();
        entry.setId(rs.getInt("ID"));

        Timestamp ts = rs.getTimestamp("Timestamp");
        if (ts != null) {
            entry.setTimestamp(ts.toLocalDateTime());
        }

        entry.setUsername(rs.getString("Username"));
        entry.setAction(rs.getString("Action"));
        entry.setTableName(rs.getString("TableName"));
        entry.setRecordId(rs.getString("RecordId"));
        entry.setDescription(rs.getString("Description"));

        return entry;
    }
}
