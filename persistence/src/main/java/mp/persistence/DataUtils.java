package mp.persistence;

import mp.persistence.proxy.CustomInvocationHandlerImpl;
import sk.tuke.mp.processor.EntityColumn;
import sk.tuke.mp.processor.EntityStructure;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DataUtils {

    private static List<EntityStructure> entityStructures = new ArrayList<>();
    private Object object;
    private EntityStructure entityStructure;

    public DataUtils(Object object, EntityStructure entityStructure) {
        this.object = object;
        this.entityStructure = entityStructure;
    }

    public <T> T createObject(ResultSet resultSet, EntityStructure entityStructure, Class<T> tClass, PersistenceManager persistenceManager) throws Exception {
        T object = tClass.getDeclaredConstructor().newInstance();

        Object value = null;
        List<EntityColumn> foreignKeyColumns = entityStructure.getForeignKeys();

        Map<String, Field> fields = Arrays.stream(tClass.getDeclaredFields()).collect(Collectors.toMap(Field::getName, f -> f));

        for (EntityColumn column : entityStructure.getEntityColumns()) {
            Field field = fields.get(column.getRealName().toLowerCase());
            if (field != null) {
                field.setAccessible(true);
                if (Modifier.isTransient(field.getModifiers())) continue;
                String val = resultSet.getString(column.getName());
                if (column.getType().equals(Integer.TYPE.toString())) {
                    try {
                        field.set(object, Integer.parseInt(val));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else if (foreignKeyColumns.stream().anyMatch(it -> it.getName().equals(column.getName())) &&
                            foreignKeyColumns.stream().anyMatch(it -> it.getType().equals(column.getType()))) {
                    if (column.getFetchType() == FetchType.LAZY) {
                        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                        if (val == null) {
                            field.set(object, null);
                        } else {
                            value = Proxy.newProxyInstance(manyToOne.targetEntity().getClassLoader(), new Class[] {field.getType()},
                                    new CustomInvocationHandlerImpl(persistenceManager, manyToOne.targetEntity(), Integer.parseInt(val)));
                            field.setAccessible(true);
                            field.set(object, value);
                        }
                    } else if (field.getAnnotation(ManyToOne.class) != null && FetchType.EAGER == column.getFetchType()) {
                        field.set(object, persistenceManager.get(field.getType(), Integer.parseInt(val)));
                    } else {
                        Object o = persistenceManager.get(field.getType(), resultSet.getInt(val));
                        field.set(object, o);
                    }
                } else {
                    if (val == null) {
                        field.set(object, null);
                    } else {
                        field.set(object, field.getType().getConstructor(field.getType()).newInstance(val));
                    }
                }
            }
        }
        return object;
    }

    public LinkedHashMap<String, Object> mapColumnValue(Object o, PersistenceManager persistenceManager) {
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();

        List<EntityColumn> entityColumns = entityStructure.getEntityColumns().stream().filter(EntityColumn::isStore).collect(Collectors.toList());
        List<EntityColumn> foreignKeyColumns = entityStructure.getForeignKeys();

        Class<?> aClass = o.getClass();

        Map<String, Field> classFields = Arrays.stream(aClass.getDeclaredFields()).filter(it -> !Modifier.isTransient(it.getModifiers())).collect(Collectors.toMap(Field::getName, f -> f));
        entityColumns.forEach(column -> {
            Field field = classFields.get(column.getRealName().toLowerCase());
            if (field != null) {
                field.setAccessible(true);
                Object value = null;
                try {
                    value = field.get(o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                if (foreignKeyColumns.stream().anyMatch(it -> it.getName().equals(column.getType())) && foreignKeyColumns.stream().anyMatch(it -> it.getType().equals(column.getType()))) {
                    try {
                        if (value == null) {
                            hashMap.put(column.getName(), null);
                        } else {
                            value.toString();
                            hashMap.put(column.getName(), persistenceManager.save(value));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    hashMap.put(column.getName(), value);
                }
            }
        });
        this.entityStructures.add(entityStructure);
        return hashMap;
    }
}
