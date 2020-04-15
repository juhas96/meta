package mp.persistence;

import java.util.Map;

public class Generator {

    private StringBuilder sb;

    public Generator() {
        sb = new StringBuilder();
    }

    public String save(String tableName, Map<String, Object> columns) {
        if (sb.length() > 0) {
            sb = new StringBuilder();
        }
        sb.append("INSERT INTO ").append(tableName).append(" (");

        columns.forEach((key, value) -> sb.append(key).append(", "));

        sb.deleteCharAt(sb.length() - 2);
        sb.append(") VALUES (");
        columns.forEach((key, value) -> sb.append(" ?,"));

        sb.deleteCharAt(sb.length() -1);
        sb.append(")");

        System.out.println("GENERATED: " + sb.toString());

        return sb.toString();
    }

    public String update(String tableName, Map<String, Object> columns, String primaryField) {
        if (sb.length() > 0) {
            sb = new StringBuilder();
        }

        sb.append("UPDATE ").append(tableName).append(" SET ");

        columns.forEach((key, value) -> sb.append(key).append(" = ?, "));

        sb.deleteCharAt(sb.length() -2);
        sb.append(" WHERE ").append(primaryField).append(" =?");

        System.out.println("GENERATED: " + sb.toString());

        return sb.toString();
    }

    String selectAll(String tableName) {
        System.out.println("GENERATED: " + "SELECT * FROM "  + tableName);
        return "SELECT * FROM " + tableName;
    }

    String selectById(String tableName, String primaryField) {
        System.out.println("GENERATED: " + "SELECT * FROM " + tableName + " WHERE " + primaryField + " = ?");
        return "SELECT * FROM " + tableName + " WHERE " + primaryField + " = ?";
    }

    String selectBy(String tableName, String fieldName) {
        if (sb.capacity() > 0) {
            sb = new StringBuilder();
        }
        sb.append("SELECT * FROM ").append(tableName).append(" WHERE ").append(tableName).append(".").append(fieldName).append("= ?");

        System.out.println("GENERATED: " + sb.toString());

        return sb.toString();
    }

    public String selectByForeign(String table, String tableForeign, String fieldName, String fieldNameForeign, String primaryName) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ")
                .append(table)
                .append(" JOIN ")
                .append(tableForeign)
                .append(" ON ")
                .append(table).append(".").append(fieldName)
                .append(" = ")
                .append(tableForeign).append(".").append(fieldNameForeign)
                .append(" WHERE ")
                .append(tableForeign).append(".").append(primaryName)
                .append("= ?");

        return sb.toString();
    }
}
