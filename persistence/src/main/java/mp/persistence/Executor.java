package mp.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Executor {

    private Generator generator;
    private Connection connection;
    private PreparedStatement preparedStatement;

    public Executor(Connection connection) {
        this.generator = new Generator();
        this.connection = connection;
    }

    public void createTable(String sql) {
        try {
            this.preparedStatement = connection.prepareStatement(sql);
            System.out.println("EXECUTE STATEMENT: " + sql);

            this.preparedStatement.executeUpdate();
            this.preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createReferences(String sql) {
        try {
            this.preparedStatement = connection.prepareStatement(sql);

            System.out.println("EXECUTE STATEMENT: " + sql);

            this.preparedStatement.executeUpdate();
            this.preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int save(String tableName, Map<String, Object> columns) {
        List<Map.Entry<String, Object>> objects = new ArrayList<>(columns.entrySet());
        String sql = this.generator.save(tableName, columns);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            createStatement(objects, preparedStatement);
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();

            return resultSet.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public int update(String tableName, HashMap<String, Object> columns, String primaryField, Object value) {
        List<Map.Entry<String, Object>> objects = new ArrayList<>(columns.entrySet());
        String sql = this.generator.update(tableName, columns, primaryField);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            createStatement(objects, preparedStatement);

            preparedStatement.setInt(objects.size() + 1, (Integer) value);
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();

            return (int) value;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void createStatement(List<Map.Entry<String, Object>> objects, PreparedStatement preparedStatement) {
        AtomicInteger i = new AtomicInteger();
        objects.forEach(object -> {
            if (object.getValue() instanceof String) {
                try {
                    preparedStatement.setString(i.get() + 1, (String) object.getValue());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else if (object.getValue() instanceof Integer) {
                try {
                    preparedStatement.setInt(i.get() + 1, (Integer) object.getValue());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            } else if (object.getValue() instanceof Boolean) {
                try {
                    preparedStatement.setBoolean(i.get() + 1, (Boolean) object.getValue());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                try {
                    preparedStatement.setNull(i.get() + 1, Types.VARCHAR);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            i.getAndIncrement();
        });
    }

    public ResultSet selectAll(String tableName) throws SQLException {
        String sql = this.generator.selectAll(tableName);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        return preparedStatement.executeQuery();
    }

    public ResultSet selectById(String tableName, String primaryField, Object value) throws SQLException {
        String sql = this.generator.selectById(tableName, primaryField);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, (int) value);
        return preparedStatement.executeQuery();
    }

    public ResultSet selectBy(String tableName, String primaryFieldName, Object value) throws SQLException {
        String sql = this.generator.selectBy(tableName, primaryFieldName);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, (String) value);
        return preparedStatement.executeQuery();
    }

    public ResultSet selectByForeign(String tableName, String tableNameForeign, String fieldName, String fieldNameForeign, String primaryFieldName, Object value) {
        try {
            String sqlSelect = this.generator.selectByForeign(tableName, tableNameForeign, fieldName, fieldNameForeign, primaryFieldName);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect);
            preparedStatement.setInt(1, (int) value);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
