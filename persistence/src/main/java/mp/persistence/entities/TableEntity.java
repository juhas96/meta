package mp.persistence.entities;

import mp.persistence.Executor;
import sk.tuke.mp.processor.EntityStructure;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class TableEntity {

    private Executor executor;

    public TableEntity(Connection connection) {
        this.executor = new Executor(connection);
    }



}
