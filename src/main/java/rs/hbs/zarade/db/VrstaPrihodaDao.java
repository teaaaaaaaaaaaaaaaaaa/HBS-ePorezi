package rs.hbs.zarade.db;

import rs.hbs.zarade.domain.VrstaPrihoda;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO za rad sa tabelom SFR_VRSTAPRIHODA (šifarnik vrsta prihoda).
 * Baza: data_zarade.mdb (Main DB)
 *
 * Stvarne kolone: VPZ (PK, šifra npr. "303"), NAZIV, OPIS, GrupaSVR
 */
public class VrstaPrihodaDao {

    private static final String TABLE_NAME = "SFR_VRSTAPRIHODA";

    /**
     * Vraća sve vrste prihoda iz baze, sortirane po VPZ (šifri).
     */
    public List<VrstaPrihoda> findAll(Connection connection) throws SQLException {
        List<VrstaPrihoda> result = new ArrayList<>();
        String sql = "SELECT VPZ, NAZIV, OPIS, GrupaSVR FROM " + TABLE_NAME + " ORDER BY VPZ";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapResultSet(rs));
            }
        }
        return result;
    }

    /**
     * Pronalazi vrstu prihoda po šifri VPZ (npr. "303").
     */
    public Optional<VrstaPrihoda> findByVpz(Connection connection, String vpz) throws SQLException {
        String sql = "SELECT VPZ, NAZIV, OPIS, GrupaSVR FROM " + TABLE_NAME + " WHERE VPZ = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vpz);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Pronalazi vrste prihoda po grupi SVR (npr. "IZ", "VAN").
     */
    public List<VrstaPrihoda> findByGrupaSvr(Connection connection, String grupaSvr) throws SQLException {
        List<VrstaPrihoda> result = new ArrayList<>();
        String sql = "SELECT VPZ, NAZIV, OPIS, GrupaSVR FROM " + TABLE_NAME + " WHERE GrupaSVR = ? ORDER BY VPZ";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, grupaSvr);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSet(rs));
                }
            }
        }
        return result;
    }

    private VrstaPrihoda mapResultSet(ResultSet rs) throws SQLException {
        VrstaPrihoda vp = new VrstaPrihoda();
        vp.setVpz(rs.getString("VPZ"));
        vp.setNaziv(rs.getString("NAZIV"));
        vp.setOpis(rs.getString("OPIS"));
        vp.setGrupaSvr(rs.getString("GrupaSVR"));
        return vp;
    }
}
