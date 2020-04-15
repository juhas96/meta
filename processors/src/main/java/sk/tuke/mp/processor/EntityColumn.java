package sk.tuke.mp.processor;

import javax.persistence.FetchType;

public class EntityColumn {
    private String name;
    private String realName;
    private String type;
    private boolean store;
    private boolean nullable;
    private int length;
    private FetchType fetchType;

    public EntityColumn(String name, String realName, String type, boolean store, int length, FetchType fetchType) {
        this.name = name;
        this.realName = realName;
        this.type = type;
        this.store = store;
        this.length = length;
        this.fetchType = fetchType;
    }

    public EntityColumn() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isStore() {
        return store;
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public void setFetchType(FetchType fetchType) {
        this.fetchType = fetchType;
    }
}
