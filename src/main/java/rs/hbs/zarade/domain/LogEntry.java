package rs.hbs.zarade.domain;

import java.time.LocalDateTime;

/**
 * Zapis u log tabeli.
 * Prati sve promene u sistemu.
 */
public class LogEntry {

    private Integer id;
    private LocalDateTime timestamp;
    private String username;
    private String action;          // INSERT, UPDATE, DELETE
    private String tableName;       // Naziv tabele koja je promenjena
    private String recordId;        // ID zapisa koji je promenjen
    private String oldValue;        // Stara vrednost (JSON ili opis)
    private String newValue;        // Nova vrednost (JSON ili opis)
    private String description;     // Opis promene

    public LogEntry() {
        this.timestamp = LocalDateTime.now();
    }

    public LogEntry(String username, String action, String tableName, String recordId, String description) {
        this();
        this.username = username;
        this.action = action;
        this.tableName = tableName;
        this.recordId = recordId;
        this.description = description;
    }

    // Getteri i setteri
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "LogEntry{" +
                "timestamp=" + timestamp +
                ", username='" + username + '\'' +
                ", action='" + action + '\'' +
                ", tableName='" + tableName + '\'' +
                ", recordId='" + recordId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
