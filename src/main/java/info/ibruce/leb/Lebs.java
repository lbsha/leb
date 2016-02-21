package info.ibruce.leb;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import info.ibruce.sardine.Functions;
import info.ibruce.sardine.concurrent.Concurrents;
import info.ibruce.leb.annotation.Observe;
import info.ibruce.sardine.annotation.Annotations;
import info.ibruce.sardine.asserts.Asserts;
import info.ibruce.sardine.collection.Containers;
import info.ibruce.sardine.lang.Strings;
import info.ibruce.sardine.lang.Systems;
import info.ibruce.sardine.reflect.Reflects;

/**
 * Leb Event Bus
 *
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-4-10
 */
public final class Lebs {

    // 观察者，被拆解的观察者
    private final MethodBucket methodBucket = MethodBucket.newInstance();
    private final Dispatcher dispatcher = Dispatcher.newInstance();

    private Lebs() {
    }

    public static Lebs newInstance() {
        return new Lebs();
    }

    public static Lebs singleton() {
        return LebsHolder.singletonLebs;
    }

    static private class LebsHolder {
        static private final Lebs singletonLebs = newInstance();
    }

    public void observer(final Object observer) {
        split(observer);
    }

    public void observers(final Object... observers) {
        for (Object obj : observers) {
            split(obj);
        }
    }

    private void split(final Object anything) {
        Asserts.notNull(anything);
        if (anything.getClass().isArray()) {
            for (Object obj : (Object[]) anything) {
                split(obj);
            }
        } else if (anything instanceof Iterator<?>) {
            Iterator<?> it = (Iterator<?>) anything;
            while (it.hasNext()) {
                split(it.next());
            }
        } else if (anything instanceof Iterable<?>) {
            split(((Iterable<?>) anything).iterator());
        } else if (anything instanceof Map<?, ?>) {
            split(((Map<?, ?>) anything).values());
        } else {
            prepare(anything);
        }
    }

    // TODO 启动一个整理线程
    private void prepare(final Object observer) {
        final Class<?> clazz = observer.getClass();
        for (Method method : clazz.getMethods()) {
            Observe observe = Annotations.annotation(method, Observe.class);
            if (observe == null)
                continue;
            String hint = Strings.emptyTo(observe.hint(), method.getName()); // 如果hint为空则hint就是方法名
            methodBucket.put(hint, MethodWrapper.builder().build(observer, method).build());
        }
    }

    // notice，无返回值
    public <T> void notice(final String hint, final Object... arguments) throws LebException {// notify是Object类方法，可以重载使用，但是不方便语法提示
        final Collection<MethodWrapper> wrappers = methodBucket.get(hint);
        final Collection<Callable<T>> tasks = filter(wrappers, arguments);
        dispatcher.dispatch(tasks);
    }

    public <T> void noticeOne(final String hint, final Object... arguments) throws LebException {
        final Collection<MethodWrapper> wrappers = methodBucket.get(hint);
        final Collection<Callable<T>> tasks = filter(wrappers, arguments);
        if (!tasks.isEmpty()) // return tasks.isEmpty() ? null : dispatcher.dispatch(tasks.iterator().next());
            dispatcher.dispatch(tasks.iterator().next());
    }

    // 正则匹配不允许有返回值
    public <T> void noticeRegex(final String hintExpression, final Object... arguments) throws LebException {
        final Collection<String> keys = methodBucket.keys();
        for (String key : keys) {
            if (Pattern.matches(hintExpression, key))
                notice(key, arguments);
        }
    }

    // 广播不允许有返回值
    public <T> void broadcast(final Object... arguments) throws LebException {
        final Collection<Collection<MethodWrapper>> wrappersCollection = methodBucket.values();
        Functions.forall(wrappersCollection, new Functions.IAction<Collection<MethodWrapper>>() {
            public void act(Collection<MethodWrapper> t) {
                final Collection<Callable<T>> tasks = filter(t, arguments);
                dispatcher.dispatch(tasks);
            }
        });
    }

    // 同步，带返回值
    public <T> Collection<T> noticeSync(final String hint, final Object... arguments) throws LebException {
        final Collection<MethodWrapper> wrappers = methodBucket.get(hint);
        final Collection<Callable<T>> tasks = filter(wrappers, arguments);
        final Collection<Future<T>> futures = dispatcher.dispatch(tasks);
        return Functions.forall(futures, new Functions.IActionR<Future<T>, T>() {
            public T act(Future<T> t) {
                try {
                    return Concurrents.future(t);
                } catch (Exception e) {
                    throw new LebException(e);
                }
            }
        });
    }

    private <T> Collection<Callable<T>> filter(final Collection<MethodWrapper> wrappers, final Object... arguments) {
        final Class<?>[] argumentClasses = Reflects.findClasses(arguments);
        final Collection<Callable<T>> tasks = Containers.newArrayList(wrappers.size());
        for (MethodWrapper wrapper : wrappers) {
            if (!wrapper.matching(argumentClasses))
                continue;
            Callable<T> callable = wrapper.toCallable(arguments);
            tasks.add(callable);
        }
        return tasks;
    }

    /**
     * 日志
     */
    static class Status {
    }

    /**
     * 转发器
     */
    static class Dispatcher {
        // 认为是IO密集型，默认JVM处理器个数的两倍
        final int poolSize = Systems.processors() * 2;
        final ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        <T> Collection<Future<T>> dispatch(final Collection<Callable<T>> tasks) {
            return Functions.forall(tasks, new Functions.IActionR<Callable<T>, Future<T>>() {
                public Future<T> act(Callable<T> t) {
                    return executor.submit(t);
                }
            });
        }

        <T> Future<T> dispatch(final Callable<T> task) {
            return executor.submit(task);
        }

        static Dispatcher newInstance() {
            return new Dispatcher();
        }
    }

    /**
     * 方法桶
     */
    static class MethodBucket {
        final ConcurrentMap<String, Collection<MethodWrapper>> bucket = Containers.newConcurrentHashMap();

        Collection<MethodWrapper> get(String key) {
            if (!bucket.containsKey(key))
                throw new LebException(String.format("hint %s is not found!", key));
            return bucket.get(key);
        }

        Collection<Collection<MethodWrapper>> values() {
            return bucket.values();
        }

        Collection<String> keys() {
            return bucket.keySet();
        }

        void put(String key, MethodWrapper e) {
            Collection<MethodWrapper> wrappers = bucket.get(key);
            if (wrappers == null) {
                wrappers = Containers.newCopyOnWriteArraySet();
                wrappers = bucket.putIfAbsent(key, wrappers) == null ? wrappers : bucket.get(key);
            }
            wrappers.add(e);
        }

        static MethodBucket newInstance() {
            return new MethodBucket();
        }
    }

    /**
     * 方法包装器
     */
    static class MethodWrapper {
        private Method method;
        private Object observer;

        boolean matching(final Class<?>[] argumentClasses) {
            final Class<?>[] parameterClasses = method.getParameterTypes();
            return Arrays.equals(parameterClasses, argumentClasses);
        }

        <T> Callable<T> toCallable(final Object... arguments) {
            return new Callable<T>() {
                public T call() throws Exception {
                    return Reflects.invoke(method, observer, arguments);
                }
            };
        }

        public
        @Override
        boolean equals(Object obj) {
            if (!(obj instanceof MethodWrapper))
                return false;
            return this.observer.equals(((MethodWrapper) obj).observer)
                    && this.method.equals(((MethodWrapper) obj).method);
        }

        public
        @Override
        int hashCode() {
            return this.observer.hashCode() ^ this.method.hashCode();
        }

        static MethodWrapperBuilder builder() {
            return new MethodWrapperBuilder();
        }

        static class MethodWrapperBuilder {
            MethodWrapper wrapper;

            MethodWrapperBuilder() {
                wrapper = new MethodWrapper();
            }

            MethodWrapperBuilder build(Object observer, Method method) {
                wrapper.observer = observer;
                wrapper.method = method;
                return this;
            }

            MethodWrapper build() {
                return wrapper;
            }
        }
    }

    /**
     * LebException.getCause()判断
     * <p>
     * <li>如果是业务异常，直接就是被调用方法抛出的业务异常实例
     * <li>如果是反射异常，那么是已经过SardineException包装，再次调用SardineException.getCause()可以获得具体实例
     */
    public static class LebException extends RuntimeException {
        LebException(String message) {
            super(message);
        }

        LebException(Throwable cause) {
            super(cause);
        }

        private static final long serialVersionUID = 1L;
    }
}