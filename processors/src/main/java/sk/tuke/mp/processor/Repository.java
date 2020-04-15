package sk.tuke.mp.processor;

import javax.lang.model.element.Element;
import java.util.HashMap;
import java.util.Map;

public class Repository {

    private Map<String, EntityStructure> tables = new HashMap<>();

    public Repository() {
    }

    public EntityStructure entityPersist(Element element) throws Exception {
        if (!this.tables.containsKey(element)) {
            EntityStructure entityStructure = EntityAnalyser.getInstance().analyse(element);
            if (entityStructure == null) {
                throw  new PersistenceException("Class can't be persist");
            } else {
                this.tables.put(element.asType().toString(), entityStructure);
                return this.tables.get(element.asType().toString());
            }
        }

        return this.tables.get(element.asType().toString());
    }

    public EntityStructure getBy(String name) {
        return this.tables.get(name);
    }

    public Map<String, EntityStructure> getTables() {
        return tables;
    }

    public void setTables(Map<String, EntityStructure> tables) {
        this.tables = tables;
    }
}
