package rs.hbs.zarade.db;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * DAO za pisanje u log tabelu _Log.
 * Baza: data_zarade.mdb (Main DB)
 *
 * Struktura mapirana iz VBA clsEventLoger2A.cls:
 *   PC, WinUser, UserID, UserTXT, Datum, Forma,
 *   TabCode, ItemID, ActionType, Module, msgExtra, msgErrNum, msg, msgPromene
 *
 * ActionType vrednosti: LOGIN, QUIT, ADD, SAVE, DELETE, PRINT, XML
 */
public class LogDao {

    private static final String APP_NAME = "ePorezi";

    private static final String INSERT_SQL =
        "INSERT INTO _Log (PC, WinUser, UserID, UserTXT, Datum, Forma, TabCode, ItemID, ActionType, Module, msgExtra, msg) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Upisuje događaj u _Log tabelu.
     *
     * @param conn       konekcija ka main bazi
     * @param userId     ID korisnika (Staff.IDUser), može biti null
     * @param userTxt    ime korisnika
     * @param forma      naziv ekrana/forme
     * @param tabCode    naziv tabele ili procesa (npr. "Primaoci", "LOGIN")
     * @param itemId     ID zapisa koji se menja (0 ako nije relevantno)
     * @param actionType tip akcije: LOGIN, QUIT, ADD, SAVE, DELETE, PRINT, XML
     * @param msg        opis događaja
     */
    public static void log(Connection conn,
                           Integer userId, String userTxt,
                           String forma, String tabCode,
                           long itemId, String actionType,
                           String msg) {
        if (conn == null) return;
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, getComputerName());
            ps.setString(2, System.getProperty("user.name", ""));
            if (userId != null) ps.setInt(3, userId); else ps.setNull(3, java.sql.Types.INTEGER);
            ps.setString(4, nvl(userTxt));
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, nvl(forma));
            ps.setString(7, nvl(tabCode));
            ps.setLong(8, itemId);
            ps.setString(9, nvl(actionType));
            ps.setString(10, APP_NAME);
            ps.setString(11, "");
            ps.setString(12, nvl(msg));
            ps.executeUpdate();
        } catch (Exception e) {
            // Log greška ne sme da prekine rad aplikacije
            System.err.println("_Log upis nije uspeo: " + e.getMessage());
        }
    }

    private static String getComputerName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return System.getenv("COMPUTERNAME") != null ?
                    System.getenv("COMPUTERNAME") : "UNKNOWN";
        }
    }

    private static String nvl(String s) {
        return s != null ? s : "";
    }
}
