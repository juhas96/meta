package mp.persistence.proxy;

import mp.persistence.PersistenceManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class CustomInvocationHandlerImpl implements InvocationHandler {

    private Object target;
    private PersistenceManager persistenceManager;
    private Class<?> aClass;
    private int id;

    public CustomInvocationHandlerImpl(PersistenceManager persistenceManager, Class<?> aClass, int id) {
        this.persistenceManager = persistenceManager;
        this.aClass = aClass;
        this.id = id;
        this.target = null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (target != null) {
            return method.invoke(target, args);
        } else {
            return (target = persistenceManager.get(this.aClass, this.id)) == null ? null : method.invoke(target, args);
        }
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
