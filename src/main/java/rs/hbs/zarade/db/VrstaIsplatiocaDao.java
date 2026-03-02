package rs.hbs.zarade.db;

import rs.hbs.zarade.domain.VrstaIsplatioca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO za rad sa tabelom SFR_VRSTAISPLATIOCA (šifarnik vrsta isplatioca).
 * Baza: data_zarade.mdb (Main DB)
 *
 * Stvarne kolone: IDvi (PK, integer 1-7), VI (naziv)
 */
public class VrstaIsplatiocaDao {

    private static final String TABLE_NAME = "SFR_VRSTAISPLATIOCA";

    /**
     * Vraća sve vrste isplatioca iz baze, sortirane po ID-u.
     */
    public List<VrstaIsplatioca> findAll(Connection connection) throws SQLException {
        List<VrstaIsplatioca> result = new ArrayList<>();
        String sql = "SELECT IDvi, VI FROM " + TABLE_NAME + " ORDER BY IDvi";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapResultSet(rs));
            }
        }
        return result;
    }

    /**
     * Pronalazi vrstu isplatioca po ID-u.
     */
    public Optional<VrstaIsplatioca> findById(Connection connection, Integer id) throws SQLException {
        String sql = "SELECT IDvi, VI FROM " + TABLE_NAME + " WHERE IDvi = ?";

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

    private VrstaIsplatioca mapResultSet(ResultSet rs) throws SQLException {
        VrstaIsplatioca vi = new VrstaIsplatioca();
        int idvi = rs.getInt("IDvi");
        vi.setId(rs.wasNull() ? null : idvi);
        vi.setNaziv(rs.getString("VI"));
        return vi;
    }
}
