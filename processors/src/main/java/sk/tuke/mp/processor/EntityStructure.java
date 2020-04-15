package sk.tuke.mp.processor;

import java.util.ArrayList;
import java.util.List;

public class EntityStructure {
    private String name;
    private List<EntityColumn> entityColumns = new ArrayList<>();
    private List<EntityColumn> foreignKeys = new ArrayList<>();
    private EntityColumn primaryKey;
    private String sqlCreate;
    private String sqlReference = "";

    public EntityStructure() {}

    public void createSQL() {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE IF NOT EXISTS ").append(name).append(" ( ").append(primaryKey.getName()).append(" ").append(getType(primaryKey))
                .append(" primary key,")
                .append(getColumns(entityColumns));

        this.foreignKeys.forEach(entity -> sb.append(",FOREIGN KEY (").append(entity.getName()).append(") REFERENCES ").append(entity.getName()).append("(id)"));

        this.sqlCreate = sb.append(")").toString();
    }

    public void referencesSQL(String name) {
        StringBuilder sb = new StringBuilder();

        if (!this.sqlReference.isEmpty()) {
            sb.append(";");
        }

        sb.append("ALTER TABLE ").append(this.name).append(" ADD FOREIGN KEY (");
        sb.append(name).append(") REFERENCES ").append(name).append("(id)");

        this.sqlReference += sb.toString();
    }

    private String getType(EntityColumn entityColumn) {
        if (entityColumn.getType().equals(Integer.TYPE.toString())) {
            return "INTEGER";
        } else if (entityColumn.getType().equals(String.class.getName())) {
            return "VARCHAR(" + entityColumn.getLength() + ")";
        } else if (entityColumn.getType().equals(Double.class.getName())) {
            return "FLOAT";
        }

        return "INTEGER";
    }

    private String getColumns(List<EntityColumn> columns) {
        StringBuilder sb = new StringBuilder();
        columns.stream().filter(EntityColumn::isStore).forEach(column -> {
            if (sb.length() != 0) {
                sb.append(",");
            }
            sb.append(column.getName()).append(" ").append(getType(column));
            if (column.isNullable()) {
                sb.append(" NOT NULL");
            }
        });

        return sb.toString();
    }

    public String getName() {
        return name.toUpperCase();
    }

    public void setName(String name) {
        this.name = name.replace(".", "_");
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns;
    }

    public void setEntityColumns(List<EntityColumn> entityColumns) {
        this.entityColumns = entityColumns;
    }

    public List<EntityColumn> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<EntityColumn> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public EntityColumn getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(EntityColumn primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getSqlCreate() {
        return sqlCreate;
    }

    public void setSqlCreate(String sqlCreate) {
        this.sqlCreate = sqlCreate;
    }

    public String getSqlReference() {
        return sqlReference;
    }

    public void setSqlReference(String sqlReference) {
        this.sqlReference = sqlReference;
    }
}
