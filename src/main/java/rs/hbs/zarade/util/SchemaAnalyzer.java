package rs.hbs.zarade.util;

import rs.hbs.zarade.db.DatabaseConfig;
import rs.hbs.zarade.db.DatabaseConfigException;
import rs.hbs.zarade.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility klasa za analizu seme baze podataka.
 * Koristi se za inventarizaciju tabela i kolona.
 */
public class SchemaAnalyzer {

    public static void main(String[] args) {
        try {
            DatabaseConfig config = DatabaseConfig.load();
            DatabaseConnection dbConnection = new DatabaseConnection(config);

            System.out.println("==============================================");
            System.out.println("ANALIZA SEME BAZE - KLJUCNE TABELE");
            System.out.println("==============================================\n");

            // Analiziraj Main bazu - tu su sve kljucne tabele
            try (Connection conn = dbConnection.getMainConnection()) {
                System.out.println("--- MAIN DB (data_zarade.mdb) ---");
                listAllTables(conn);
                String[] keyTables = {
                    "Primaoci",
                    "Poslodavac",
                    "Obracun",
                    "ObracunDef",
                    "Obracun_Stope",
                    "Obracun_Prijava",
                    "Obracun_MFP",
                    "SFR_VRSTAPRIHODA",
                    "SFR_VRSTAPRIMAOCA",
                    "SFR_VRSTAISPLATIOCA",
                    "SFR_OPSTINA"
                };

                for (String table : keyTables) {
                    analyzeTable(conn, table);
                }
            }

        } catch (DatabaseConfigException e) {
            System.err.println("Greska konfiguracije: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL Greska: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void listAllTables(Connection conn) throws SQLException {
        System.out.println("  Sve tabele:");
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                System.out.println("    - " + rs.getString("TABLE_NAME"));
            }
        }
        System.out.println();
    }

    private static void analyzeTable(Connection conn, String tableName) throws SQLException {
        System.out.println("======= " + tableName + " =======");

        DatabaseMetaData metaData = conn.getMetaData();

        // Kolone
        List<ColumnInfo> columns = new ArrayList<>();
        try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                ColumnInfo col = new ColumnInfo();
                col.name = rs.getString("COLUMN_NAME");
                col.typeName = rs.getString("TYPE_NAME");
                col.size = rs.getInt("COLUMN_SIZE");
                col.nullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                col.ordinal = rs.getInt("ORDINAL_POSITION");
                columns.add(col);
            }
        }

        if (columns.isEmpty()) {
            System.out.println("  [Tabela nije pronadjena ili nema kolona]");
            System.out.println();
            return;
        }

        // Primarni kljuc
        List<String> primaryKeys = new ArrayList<>();
        try (ResultSet rs = metaData.getPrimaryKeys(null, null, tableName)) {
            while (rs.next()) {
                primaryKeys.add(rs.getString("COLUMN_NAME"));
            }
        }

        // Ispis
        System.out.println("  Kolone (" + columns.size() + "):");
        for (ColumnInfo col : columns) {
            String pkMarker = primaryKeys.contains(col.name) ? " [PK]" : "";
            String nullMarker = col.nullable ? "" : " NOT NULL";
            System.out.printf("    %2d. %-30s %-15s (%d)%s%s%n",
                col.ordinal, col.name, col.typeName, col.size, nullMarker, pkMarker);
        }

        if (!primaryKeys.isEmpty()) {
            System.out.println("  Primarni kljuc: " + String.join(", ", primaryKeys));
        }

        System.out.println();
    }

    private static class ColumnInfo {
        String name;
        String typeName;
        int size;
        boolean nullable;
        int ordinal;
    }
}
