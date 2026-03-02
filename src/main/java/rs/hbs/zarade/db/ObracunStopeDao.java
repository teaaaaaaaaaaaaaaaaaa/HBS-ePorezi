package rs.hbs.zarade.db;

import rs.hbs.zarade.domain.ObracunStope;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO za rad sa tabelom Obracun_Stope.
 * Baza: data_zarade.mdb (Main DB)
 *
 * Ova tabela sadrzi poreske stope i stope doprinosa za razlicite tipove prihoda.
 * Stope se menjaju kroz vreme - polje PromenaStope oznacava verziju.
 */
public class ObracunStopeDao implements BaseDao<ObracunStope, Integer> {

    private static final String TABLE_NAME = "Obracun_Stope";

    // SQL upiti
    private static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME + " ORDER BY TIP, PromenaStope";
    private static final String SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE ID_OBRACUN = ?";
    private static final String SELECT_BY_TIP = "SELECT * FROM " + TABLE_NAME + " WHERE TIP = ? ORDER BY PromenaStope DESC";
    private static final String SELECT_BY_TIP_AND_PROMENA = "SELECT * FROM " + TABLE_NAME +
            " WHERE TIP = ? AND PromenaStope = ?";
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM " + TABLE_NAME;

    @Override
    public Optional<ObracunStope> findById(Connection connection, Integer id) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToObracunStope(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ObracunStope> findAll(Connection connection) throws SQLException {
        List<ObracunStope> result = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                result.add(mapResultSetToObracunStope(rs));
            }
        }
        return result;
    }

    @Override
    public ObracunStope save(Connection connection, ObracunStope entity) throws SQLException {
        throw new UnsupportedOperationException("Save nije jos implementiran");
    }

    @Override
    public void update(Connection connection, ObracunStope entity) throws SQLException {
        throw new UnsupportedOperationException("Update nije jos implementiran");
    }

    @Override
    public void delete(Connection connection, Integer id) throws SQLException {
        throw new UnsupportedOperationException("Delete nije jos implementiran");
    }

    @Override
    public long count(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(COUNT_ALL)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    /**
     * Pronalazi stope za dati tip prihoda i verziju stopa.
     * Ovo je KLJUCNA metoda za obracun - vraca stope koje vaze za odredjenu verziju.
     *
     * @param connection aktivna konekcija
     * @param tip sifra vrste prihoda (npr. "101", "405")
     * @param promenaStope verzija stopa
     * @return Optional sa stopama ili prazan Optional
     */
    public Optional<ObracunStope> findByTipAndPromenaStope(Connection connection, String tip, Integer promenaStope) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_TIP_AND_PROMENA)) {
            stmt.setString(1, tip);
            stmt.setInt(2, promenaStope);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToObracunStope(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Pronalazi sve verzije stopa za dati tip prihoda.
     * Rezultati su sortirani po PromenaStope DESC (najnovije prve).
     *
     * @param connection aktivna konekcija
     * @param tip sifra vrste prihoda
     * @return lista stopa za taj tip
     */
    public List<ObracunStope> findByTip(Connection connection, String tip) throws SQLException {
        List<ObracunStope> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_TIP)) {
            stmt.setString(1, tip);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToObracunStope(rs));
                }
            }
        }
        return result;
    }

    /**
     * Pronalazi najnoviju verziju stopa za dati tip prihoda.
     *
     * @param connection aktivna konekcija
     * @param tip sifra vrste prihoda
     * @return Optional sa najnovijim stopama ili prazan Optional
     */
    public Optional<ObracunStope> findLatestByTip(Connection connection, String tip) throws SQLException {
        List<ObracunStope> stope = findByTip(connection, tip);
        return stope.isEmpty() ? Optional.empty() : Optional.of(stope.get(0));
    }

    /**
     * Mapira ResultSet red na ObracunStope objekat.
     */
    private ObracunStope mapResultSetToObracunStope(ResultSet rs) throws SQLException {
        ObracunStope s = new ObracunStope();

        s.setIdObracun(getIntOrNull(rs, "ID_OBRACUN"));
        s.setTip(rs.getString("TIP"));
        s.setPromenaStope(getIntOrNull(rs, "PromenaStope"));
        s.setTipNaslov(rs.getString("TIP_NASLOV"));
        s.setTipOpis(rs.getString("TIP_OPIS"));

        // Stope - cuvaju se kao DOUBLE u bazi
        s.setPorezP(getDoubleOrNull(rs, "POREZ_P"));
        s.setPioP(getDoubleOrNull(rs, "PIO_P"));
        s.setZdrP(getDoubleOrNull(rs, "ZDR_P"));
        s.setNezP(getDoubleOrNull(rs, "NEZ_P"));
        s.setPioZ(getDoubleOrNull(rs, "PIO_Z"));
        s.setZdrZ(getDoubleOrNull(rs, "ZDR_Z"));
        s.setNezZ(getDoubleOrNull(rs, "NEZ_Z"));
        s.setNormTrosak(getDoubleOrNull(rs, "NORM_TROSAK"));

        // Poresko oslobodjenje - DECIMAL
        BigDecimal pOsl = rs.getBigDecimal("P_OSL");
        s.setpOsl(pOsl);

        // Osnovice
        s.setpOsn(rs.getString("P_OSN"));
        s.setOsnDop(rs.getString("OSN_DOP"));
        s.setMinOsnDop(rs.getString("MIN_OSN_DOP"));
        s.setMaxOsnDop(rs.getString("MAX_OSN_DOP"));

        // Koeficijenti
        s.setKoefMinRad(getDoubleOrNull(rs, "KOEF_MIN_RAD"));

        return s;
    }

    private Integer getIntOrNull(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }

    private Double getDoubleOrNull(ResultSet rs, String columnName) throws SQLException {
        double value = rs.getDouble(columnName);
        return rs.wasNull() ? null : value;
    }
}
