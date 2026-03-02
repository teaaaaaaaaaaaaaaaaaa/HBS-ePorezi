package rs.hbs.zarade.db;

/**
 * Izuzetak koji se baca kada konfiguracija baze nije ispravna.
 */
public class DatabaseConfigException extends Exception {

    public DatabaseConfigException(String message) {
        super(message);
    }

    public DatabaseConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
