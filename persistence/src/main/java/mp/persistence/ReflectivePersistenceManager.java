package mp.persistence;

import com.google.gson.Gson;
import mp.persistence.entities.TableEntity;
import mp.persistence.proxy.CustomInvocationHandlerImpl;
import sk.tuke.mp.processor.EntityStructure;
import sk.tuke.mp.processor.Repository;

import javax.persistence.Id;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReflectivePersistenceManager implements PersistenceManager {

    private Connection connection;
    private Repository repository = new Repository();
    private List<EntityStructure> entityStructures = new ArrayList<>();
    private Executor executor;

    public ReflectivePersistenceManager(Connection connection) {
        this.connection = connection;
        this.executor = new Executor(connection);
    }

    @Override
    public void createTables(Class... classes) {
        Gson gson = new Gson();
        try {
            this.repository = gson.fromJson(new FileReader("processors/src/main/resources/output.json"), Repository.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (Class aClass : classes) {
            EntityStructure entityStructure = this.repository.getBy(aClass.getName());
            executor.createTable(entityStructure.getSqlCreate());
            this.entityStructures.add(entityStructure);
        }
        System.out.println("CREATING TABLES\n");
    }

    @Override
    public <T> List<T> getAll(Class<T> clazz) throws PersistenceException, SQLException {
        ArrayList objects = new ArrayList();

        EntityStructure entityStructure = this.repository.getBy(clazz.getName());
        DataUtils utils = new DataUtils(clazz, entityStructure);

        ResultSet resultSet = executor.selectAll(entityStructure.getName());

        while (resultSet.next()) {
            try {
                objects.add(utils.createObject(resultSet, entityStructure, clazz, ReflectivePersistenceManager.this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return objects;
    }

    @Override
    public <T> T get(Class<T> type, int id) throws PersistenceException, SQLException {
        EntityStructure entityStructure = this.repository.getBy(type.getName());
        DataUtils utils = new DataUtils(type, entityStructure);

        ResultSet resultSet = executor.selectById(entityStructure.getName(), entityStructure.getPrimaryKey().getName(), id);
        while (resultSet.next()) {
            try {
                return utils.createObject(resultSet, entityStructure, type, ReflectivePersistenceManager.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public <T> List<T> getBy(Class<T> type, String fieldName, Object value) throws SQLException {
        ArrayList objects = new ArrayList();
        EntityStructure entityStructure = this.repository.getBy(type.getName());
        DataUtils utils = new DataUtils(type, entityStructure);

        ResultSet resultSet = executor.selectBy(entityStructure.getName(), fieldName, value);

        while (resultSet.next()) {
            try {
                objects.add(utils.createObject(resultSet, entityStructure, type, ReflectivePersistenceManager.this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return objects;
    }

    @Override
    public int save(Object value) throws PersistenceException {
        Optional<Field> field = getId(value.getClass());

        if (!field.isPresent()) {
            value.toString();
            value = ((CustomInvocationHandlerImpl) Proxy.getInvocationHandler(value)).getTarget();

            field = getId(value.getClass());
        }

        Field idField = field.get();
        idField.setAccessible(true);

        EntityStructure entityStructure = this.repository.getBy(value.getClass().getName());
        DataUtils utils = new DataUtils(value.getClass(), entityStructure);
        LinkedHashMap<String, Object> stringObjectLinkedHashMap = utils.mapColumnValue(value, ReflectivePersistenceManager.this);
        int newId = 0;

        try {
            if ((Integer) idField.get(value) != 0) {
                newId = executor.update(entityStructure.getName(), stringObjectLinkedHashMap, idField.getName(), idField.get(value));
            } else {
                newId = executor.save(entityStructure.getName(), stringObjectLinkedHashMap);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            idField.set(value, newId);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return newId;
    }

    private Optional<Field> getId(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter(it -> it.getDeclaredAnnotation(Id.class) != null).findFirst();
    }

//    private Connection connection;
//    private DataUtils dataUtils;
//    List<Table> tables = new ArrayList<>();
//
//    public ReflectivePersistenceManager(Connection connection) {
//        this.connection = connection;
//        this.dataUtils = new DataUtils(this.connection);
//    }
//
//    @Override
//    public void createTables(Class... classes) {
//        Arrays.stream(classes).forEach(it -> tables.add(new Table(it)));
//
//        tables.forEach(it -> {
//            try {
//                dataUtils.setReferences(tables);
//                dataUtils.createTable(it);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public <T> List<T> getAll(Class<T> clazz) throws PersistenceException {
//        try {
//            return dataUtils.getAll(clazz);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    @Override
//    public <T> T get(Class<T> type, int id) throws PersistenceException {
//        try {
//            return dataUtils.get(type, id);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    @Override
//    public <T> List<T> getBy(Class<T> type, String fieldName, Object value) {
//        try {
//            return dataUtils.getBy(type, fieldName, value);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    @Override
//    public int save(Object value) throws PersistenceException {
//        try {
//            return dataUtils.save(value);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
}
