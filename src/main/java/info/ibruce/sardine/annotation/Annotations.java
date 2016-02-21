package info.ibruce.sardine.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import info.ibruce.sardine.collection.Collections3;
import info.ibruce.sardine.collection.Containers;
import info.ibruce.sardine.asserts.Asserts;

/**
 * introduction
 *
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-3-23
 */
public final class Annotations {

    // private final static Cache<Object, Object> AnnotationCache = CacheBuilder.newBuilder().weakKeys().softValues()
    // .initialCapacity(256).maximumSize(1024).expireAfterAccess(10, TimeUnit.HOURS).build();

    /**
     * 获取某个Class上的全部Annotation
     *
     * @param clazz
     * @return
     */
    public static Collection<Annotation> annotations(Class<?> clazz) {
        Asserts.notNull(clazz);
        Collection<Annotation> container = Containers.newHashSet();
        recursiveAnnotations(clazz, container);
        return container;
    }

    static private void recursiveAnnotations(Class<?> clazz, Collection<Annotation> container) {
        if (container == null) {
            container = Containers.newHashSet();
        }
        if (clazz != null) {
            Annotation[] as = clazz.getAnnotations();
            Collections3.addAll(container, as);// 添加

            Class<?> superClazz = clazz.getSuperclass();
            recursiveAnnotations(superClazz, container);
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> interfase : interfaces)
                recursiveAnnotations(interfase, container);
        }
    }

    /**
     * 获取某个Class上某个Annotation
     *
     * @param clazz
     * @param annotation
     * @return
     */
    public static <A extends Annotation> A annotation(Class<?> clazz, Class<A> annotation) {
        Asserts.notNull(clazz);
        return recursiveAnnotation(clazz, annotation);
    }

    static private <A extends Annotation> A recursiveAnnotation(Class<?> clazz, Class<A> annotation) {
        A a = null;
        if (clazz != null) {
            if ((a = clazz.getAnnotation(annotation)) != null)// 找到
                return a;
            Class<?> superClazz = clazz.getSuperclass();
            if ((a = recursiveAnnotation(superClazz, annotation)) != null)
                return a;
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> interfase : interfaces)
                if ((a = recursiveAnnotation(interfase, annotation)) != null)
                    return a;
        }
        return a;
    }

    /**
     * 获取某个Method上的全部Annotation
     *
     * @param method
     * @return
     */
    public static Collection<Annotation> annotations(Method method) {
        Asserts.notNull(method);
        Collection<Annotation> container = Containers.newHashSet();
        recursiveAnnotations(method, container);
        return container;
    }

    static private void recursiveAnnotations(Method method, Collection<Annotation> container) {
        if (container == null) {
            container = Containers.newHashSet();
        }
        if (method != null) {
            Annotation[] as = method.getAnnotations();
            Collections3.addAll(container, as);// 添加

            Class<?> clazz = method.getDeclaringClass();
            Class<?> superClazz = clazz.getSuperclass();
            Method superMethod = getMethod(superClazz, method);
            recursiveAnnotations(superMethod, container);
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> interfase : interfaces) {
                Method interfaceMethod = getMethod(interfase, method);
                recursiveAnnotations(interfaceMethod, container);
            }
        }
    }

    static private Method getMethod(Class<?> clazz, Method method) {
        if (clazz == null || method == null)
            return null;
        String name = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException /* | SecurityException */e) {
            return null;
        } catch (SecurityException e) {
            return null;
        }
    }

    /**
     * 获取某个Method上的某个Annotation
     *
     * @param method
     * @param annotation
     * @return
     */
    public static <A extends Annotation> A annotation(Method method, Class<A> annotation) {
        Asserts.notNull(method);
        return recursiveAnnotation(method, annotation);
    }

    static private <A extends Annotation> A recursiveAnnotation(Method method, Class<A> annotation) {
        A a = null;
        if (method != null) {
            if ((a = method.getAnnotation(annotation)) != null)// 找到
                return a;
            Class<?> clazz = method.getDeclaringClass();
            Class<?> superClazz = clazz.getSuperclass();
            Method superMethod = getMethod(superClazz, method);
            if ((a = recursiveAnnotation(superMethod, annotation)) != null)
                return a;
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> interfase : interfaces) {
                Method interfaceMethod = getMethod(interfase, method);
                if ((a = recursiveAnnotation(interfaceMethod, annotation)) != null)
                    return a;
            }
        }
        return a;
    }
}
