package rs.hbs.zarade.db;

import rs.hbs.zarade.domain.ObracunDefinicija;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO za rad sa tabelom ObracunDef.
 * Baza: data_zarade.mdb (Main DB)
 *
 * Definicija obracuna grupise vise pojedinacnih obracuna (Obracun tabela).
 */
public class ObracunDefinicijaDao implements BaseDao<ObracunDefinicija, Integer> {

    private static final String TABLE_NAME = "ObracunDef";

    // SQL upiti
    private static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME + " ORDER BY DatumObracuna DESC, ID_Obracun DESC";
    private static final String SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE ID_Obracun = ?";
    private static final String SELECT_BY_POSLODAVAC = "SELECT * FROM " + TABLE_NAME +
            " WHERE lnkPoslodavac = ? ORDER BY DatumObracuna DESC";
    private static final String SELECT_BY_PERIOD = "SELECT * FROM " + TABLE_NAME +
            " WHERE DatumObracuna >= ? AND DatumObracuna <= ? ORDER BY DatumObracuna DESC";
    private static final String SELECT_MAX_ID = "SELECT MAX(ID_Obracun) AS MaxID FROM " + TABLE_NAME;
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM " + TABLE_NAME;

    @Override
    public Optional<ObracunDefinicija> findById(Connection connection, Integer id) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToObracunDefinicija(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ObracunDefinicija> findAll(Connection connection) throws SQLException {
        List<ObracunDefinicija> result = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                result.add(mapResultSetToObracunDefinicija(rs));
            }
        }
        return result;
    }

    private static final String INSERT_SQL =
        "INSERT INTO " + TABLE_NAME +
        " (NAZIV_OBRACUNA, OVP, Mesec, DatumObracuna, DatumSlanja, OznakaZaKonacnu," +
        "  BrojDana, FondSati, lnkPoslodavac, NN, NV, Napomena, RBObracuna, PU_ID)" +
        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_SQL =
        "UPDATE " + TABLE_NAME +
        " SET NAZIV_OBRACUNA=?, OVP=?, Mesec=?, DatumObracuna=?," +
        "     DatumSlanja=?, OznakaZaKonacnu=?, BrojDana=?, FondSati=?," +
        "     lnkPoslodavac=?, NN=?, NV=?, Napomena=?" +
        " WHERE ID_Obracun=?";

    private static final String DELETE_SQL = "DELETE FROM " + TABLE_NAME + " WHERE ID_Obracun=?";

    @Override
    public ObracunDefinicija save(Connection connection, ObracunDefinicija entity) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL,
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getNazivObracuna());
            setIntOrNull(stmt, 2, entity.getOvp());
            stmt.setString(3, entity.getMesec());
            stmt.setDate(4, entity.getDatumObracuna() != null ?
                    java.sql.Date.valueOf(entity.getDatumObracuna()) : null);
            stmt.setDate(5, entity.getDatumSlanja() != null ?
                    java.sql.Date.valueOf(entity.getDatumSlanja()) : null);
            stmt.setString(6, entity.getOznakaZaKonacnu());
            setIntOrNull(stmt, 7, entity.getBrojDana());
            setIntOrNull(stmt, 8, entity.getFondSati());
            setIntOrNull(stmt, 9, entity.getLnkPoslodavac());
            stmt.setBigDecimal(10, entity.getNn());
            stmt.setBigDecimal(11, entity.getNv());
            stmt.setString(12, entity.getNapomena());
            setIntOrNull(stmt, 13, entity.getRbObracuna());
            setIntOrNull(stmt, 14, entity.getPuId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setIdObracun(generatedKeys.getInt(1));
                }
            }
        }
        return entity;
    }

    @Override
    public void update(Connection connection, ObracunDefinicija entity) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, entity.getNazivObracuna());
            setIntOrNull(stmt, 2, entity.getOvp());
            stmt.setString(3, entity.getMesec());
            stmt.setDate(4, entity.getDatumObracuna() != null ?
                    java.sql.Date.valueOf(entity.getDatumObracuna()) : null);
            stmt.setDate(5, entity.getDatumSlanja() != null ?
                    java.sql.Date.valueOf(entity.getDatumSlanja()) : null);
            stmt.setString(6, entity.getOznakaZaKonacnu());
            setIntOrNull(stmt, 7, entity.getBrojDana());
            setIntOrNull(stmt, 8, entity.getFondSati());
            setIntOrNull(stmt, 9, entity.getLnkPoslodavac());
            stmt.setBigDecimal(10, entity.getNn());
            stmt.setBigDecimal(11, entity.getNv());
            stmt.setString(12, entity.getNapomena());
            stmt.setInt(13, entity.getIdObracun());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(Connection connection, Integer id) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private void setIntOrNull(PreparedStatement stmt, int paramIndex, Integer value) throws SQLException {
        if (value != null) {
            stmt.setInt(paramIndex, value);
        } else {
            stmt.setNull(paramIndex, java.sql.Types.INTEGER);
        }
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
     * Pronalazi poslednji (maksimalni) ID obracuna.
     * Koristi se pri kreiranju novog obracuna.
     * Iz legacy sistema: MaxOfObracun upit
     *
     * @param connection aktivna konekcija
     * @return maksimalni ID ili 0 ako nema obracuna
     */
    public int findMaxId(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_MAX_ID)) {
            if (rs.next()) {
                return rs.getInt("MaxID");
            }
        }
        return 0;
    }

    /**
     * Pronalazi obracune po poslodavcu.
     *
     * @param connection aktivna konekcija
     * @param lnkPoslodavac ID poslodavca
     * @return lista definicija obracuna
     */
    public List<ObracunDefinicija> findByPoslodavac(Connection connection, Integer lnkPoslodavac) throws SQLException {
        List<ObracunDefinicija> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_POSLODAVAC)) {
            stmt.setInt(1, lnkPoslodavac);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToObracunDefinicija(rs));
                }
            }
        }
        return result;
    }

    /**
     * Pronalazi obracune u datom periodu.
     *
     * @param connection aktivna konekcija
     * @param odDatuma pocetak perioda
     * @param doDatuma kraj perioda
     * @return lista definicija obracuna
     */
    public List<ObracunDefinicija> findByPeriod(Connection connection, LocalDate odDatuma, LocalDate doDatuma) throws SQLException {
        List<ObracunDefinicija> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_PERIOD)) {
            stmt.setDate(1, Date.valueOf(odDatuma));
            stmt.setDate(2, Date.valueOf(doDatuma));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToObracunDefinicija(rs));
                }
            }
        }
        return result;
    }

    /**
     * Mapira ResultSet red na ObracunDefinicija objekat.
     */
    private ObracunDefinicija mapResultSetToObracunDefinicija(ResultSet rs) throws SQLException {
        ObracunDefinicija od = new ObracunDefinicija();

        od.setIdObracun(getIntOrNull(rs, "ID_Obracun"));
        od.setNazivObracuna(rs.getString("NAZIV_OBRACUNA"));
        od.setOvp(getIntOrNull(rs, "OVP"));
        od.setMesec(rs.getString("Mesec"));
        od.setRbObracuna(getIntOrNull(rs, "RBObracuna"));

        // Datumi
        Timestamp datumObracuna = rs.getTimestamp("DatumObracuna");
        od.setDatumObracuna(datumObracuna != null ? datumObracuna.toLocalDateTime().toLocalDate() : null);

        Timestamp datumSlanja = rs.getTimestamp("DatumSlanja");
        od.setDatumSlanja(datumSlanja != null ? datumSlanja.toLocalDateTime().toLocalDate() : null);

        od.setOznakaZaKonacnu(rs.getString("OznakaZaKonacnu"));

        // Veze
        od.setLnkPoslodavac(getIntOrNull(rs, "lnkPoslodavac"));
        od.setLnkObracunPrijava(getIntOrNull(rs, "lnkObracunPrijava"));
        od.setLnkPromenaStope(getIntOrNull(rs, "lnkPromenaStope"));

        // Fond sati
        od.setBrojDana(getIntOrNull(rs, "BrojDana"));
        od.setFondSati(getIntOrNull(rs, "FondSati"));

        // Finansijski podaci
        od.setNn(rs.getBigDecimal("NN"));
        od.setNv(rs.getBigDecimal("NV"));

        // Ostalo
        od.setNapomena(rs.getString("Napomena"));
        od.setPuId(getIntOrNull(rs, "PU_ID"));
        od.setmId(getIntOrNull(rs, "mID"));

        return od;
    }

    private Integer getIntOrNull(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }
}
