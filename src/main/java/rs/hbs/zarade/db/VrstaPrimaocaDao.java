package rs.hbs.zarade.db;

import rs.hbs.zarade.domain.VrstaPrimaoca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO za rad sa tabelom SFR_VRSTAPRIMAOCA (šifarnik vrsta primaoca prihoda).
 * Baza: data_zarade.mdb (Main DB)
 *
 * Stvarne kolone: IDvp (PK, integer 1-13), VP (naziv)
 * Nema posebne kolone "Sifra" - IDvp služi i kao šifra.
 */
public class VrstaPrimaocaDao {

    private static final String TABLE_NAME = "SFR_VRSTAPRIMAOCA";

    /**
     * Vraća sve vrste primaoca iz baze, sortirane po ID-u.
     */
    public List<VrstaPrimaoca> findAll(Connection connection) throws SQLException {
        List<VrstaPrimaoca> result = new ArrayList<>();
        String sql = "SELECT IDvp, VP FROM " + TABLE_NAME + " ORDER BY IDvp";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapResultSet(rs));
            }
        }
        return result;
    }

    /**
     * Pronalazi vrstu primaoca po ID-u.
     */
    public Optional<VrstaPrimaoca> findById(Connection connection, Integer id) throws SQLException {
        String sql = "SELECT IDvp, VP FROM " + TABLE_NAME + " WHERE IDvp = ?";

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

    private VrstaPrimaoca mapResultSet(ResultSet rs) throws SQLException {
        VrstaPrimaoca vp = new VrstaPrimaoca();
        int idvp = rs.getInt("IDvp");
        vp.setId(rs.wasNull() ? null : idvp);
        // IDvp je i šifra (1-13)
        vp.setSifra(vp.getId() != null ? String.valueOf(idvp) : null);
        vp.setNaziv(rs.getString("VP"));
        return vp;
    }
}
