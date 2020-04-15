package mp.persistence;

import java.sql.SQLException;
import java.util.List;

public interface PersistenceManager {

    void createTables(Class... classes);

    <T> List<T> getAll(Class<T> clazz) throws PersistenceException, SQLException;

    <T> T get(Class<T> type, int id) throws PersistenceException, SQLException;

    <T> List<T> getBy(Class<T> type, String fieldName, Object value) throws SQLException;

    int save(Object value) throws PersistenceException;
}

