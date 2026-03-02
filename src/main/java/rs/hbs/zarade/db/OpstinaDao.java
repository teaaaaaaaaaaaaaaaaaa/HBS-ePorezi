package rs.hbs.zarade.db;

import rs.hbs.zarade.domain.Opstina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO za rad sa tabelom SFR_OPSTINA (šifarnik opština).
 * Baza: data_zarade.mdb (Main DB)
 *
 * Stvarne kolone: ID_SFR_OPS (PK), OPSTINA (naziv), SIFRA
 */
public class OpstinaDao {

    private static final String TABLE_NAME = "SFR_OPSTINA";

    /**
     * Vraća sve opštine iz baze, sortirane po šifri.
     */
    public List<Opstina> findAll(Connection connection) throws SQLException {
        List<Opstina> result = new ArrayList<>();
        String sql = "SELECT ID_SFR_OPS, SIFRA, OPSTINA FROM " + TABLE_NAME + " ORDER BY SIFRA";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapResultSet(rs));
            }
        }
        return result;
    }

    /**
     * Pronalazi opštinu po ID-u.
     */
    public Optional<Opstina> findById(Connection connection, Integer id) throws SQLException {
        String sql = "SELECT ID_SFR_OPS, SIFRA, OPSTINA FROM " + TABLE_NAME + " WHERE ID_SFR_OPS = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Pronalazi opštinu po šifri (npr. "101").
     */
    public Optional<Opstina> findBySifra(Connection connection, String sifra) throws SQLException {
        String sql = "SELECT ID_SFR_OPS, SIFRA, OPSTINA FROM " + TABLE_NAME + " WHERE SIFRA = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sifra);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Pronalazi opštinu po nazivu.
     */
    public Optional<Opstina> findByNaziv(Connection connection, String naziv) throws SQLException {
        String sql = "SELECT ID_SFR_OPS, SIFRA, OPSTINA FROM " + TABLE_NAME + " WHERE OPSTINA = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, naziv);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Opstina mapResultSet(ResultSet rs) throws SQLException {
        Opstina o = new Opstina();
        o.setId(rs.getInt("ID_SFR_OPS"));
        if (rs.wasNull()) o.setId(null);
        o.setSifra(rs.getString("SIFRA"));
        o.setNaziv(rs.getString("OPSTINA"));
        return o;
    }
}
