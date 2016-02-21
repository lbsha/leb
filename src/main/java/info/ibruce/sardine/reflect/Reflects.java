package info.ibruce.sardine.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import info.ibruce.sardine.exception.SardineException;
import info.ibruce.sardine.log.Logs;

/**
 * introduction
 *
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-3-24
 */
@SuppressWarnings("unchecked")
public class Reflects {

    public static <T> T newInstance(Class<T> clazz) throws SardineException {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SardineException(e);
        }
    }

    public static <T> T invoke(Method method, Object clazzInstance, Object... arguments) throws Exception {
        try {
            return (T) method.invoke(clazzInstance, arguments);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            Logs.p(e);
            throw new SardineException(e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof Exception) {
                // 业务异常
                throw (Exception) t;
            } else {
                Logs.p(e);
                throw new SardineException(t);
            }
        }
    }

    public static Class<?>[] findClasses(Object... instances) {
        final int size = instances.length;
        Class<?>[] types = new Class<?>[size];
        for (int i = 0; i < size; i++) {
            Object obj = instances[i];
            types[i] = (obj == null ? null : obj.getClass());
        }
        return types;
    }

    public static <T> Method findMethod(Class<T> clazz, String name, Class<?>... parameterTypes)
            throws SardineException {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new SardineException(e);
        }
    }

    public static <T> Method findMethodIgnoreException(Class<T> clazz, String name, Class<?>... parameterTypes)
            throws SardineException {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            // Logs.p(e, "ignored exception:");
        }
        return null;
    }

    // public static Class<?>[] findParameterTypes(Method method) throws SardineException {
    // return method.getParameterTypes();
    // }

    // public static boolean hasParameters(Method method) throws SardineException {
    // return method.getParameterTypes().length != 0;
    // }
}
