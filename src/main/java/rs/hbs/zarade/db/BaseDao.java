package rs.hbs.zarade.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Bazni interfejs za sve DAO klase.
 *
 * @param <T> tip entiteta
 * @param <ID> tip primarnog kljuca
 */
public interface BaseDao<T, ID> {

    /**
     * Pronalazi entitet po primarnom kljucu.
     *
     * @param connection aktivna konekcija
     * @param id primarni kljuc
     * @return Optional sa entitetom ili prazan Optional
     * @throws SQLException ako dodje do greske
     */
    Optional<T> findById(Connection connection, ID id) throws SQLException;

    /**
     * Vraca sve entitete.
     *
     * @param connection aktivna konekcija
     * @return lista svih entiteta
     * @throws SQLException ako dodje do greske
     */
    List<T> findAll(Connection connection) throws SQLException;

    /**
     * Cuva novi entitet.
     *
     * @param connection aktivna konekcija
     * @param entity entitet za cuvanje
     * @return sacuvan entitet (sa generisanim ID-jem ako je primenljivo)
     * @throws SQLException ako dodje do greske
     */
    T save(Connection connection, T entity) throws SQLException;

    /**
     * Azurira postojeci entitet.
     *
     * @param connection aktivna konekcija
     * @param entity entitet za azuriranje
     * @throws SQLException ako dodje do greske
     */
    void update(Connection connection, T entity) throws SQLException;

    /**
     * Brise entitet po primarnom kljucu.
     *
     * @param connection aktivna konekcija
     * @param id primarni kljuc
     * @throws SQLException ako dodje do greske
     */
    void delete(Connection connection, ID id) throws SQLException;

    /**
     * Broji ukupan broj entiteta.
     *
     * @param connection aktivna konekcija
     * @return broj entiteta
     * @throws SQLException ako dodje do greske
     */
    long count(Connection connection) throws SQLException;
}
