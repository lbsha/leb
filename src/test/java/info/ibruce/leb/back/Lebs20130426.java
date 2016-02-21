package info.ibruce.leb.back;

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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import info.ibruce.sardine.Functions;
import info.ibruce.sardine.concurrent.Concurrents;
import info.ibruce.leb.annotation.Observe;
import info.ibruce.sardine.annotation.Annotations;
import info.ibruce.sardine.asserts.Asserts;
import info.ibruce.sardine.collection.Containers;
import info.ibruce.sardine.lang.Strings;
import info.ibruce.sardine.lang.Systems;
import info.ibruce.sardine.log.Logs;
import info.ibruce.sardine.reflect.Reflects;

/**
 * Leb Event Bus
 *
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-4-10
 */
public final class Lebs20130426 {
    // 观察者，被拆解的观察者
    private final MethodBucket methodBucket = MethodBucket.newInstance();
    private final Dispatcher dispatcher = Dispatcher.newInstance();

    private Lebs20130426() {
    }

    public static Lebs20130426 newInstance() {
        return new Lebs20130426();
    }

    public static Lebs20130426 singleton() {
        return LebsHolder.singletonLebs;
    }

    static private class LebsHolder {
        static private final Lebs20130426 singletonLebs = newInstance();
    }

    public void observer(final Object observer) {
        parseObserver(observer);
    }

    public void observers(final Object... observers) {
        for (Object obj : observers) {
            parseObserver(obj);
        }
    }

    // public void observers(final Class<?>... noArgsConstructorObserverClasses) {
    // for (Class<?> clazz : noArgsConstructorObserverClasses) {
    // parseObserver(Reflects.newInstance(clazz));
    // }
    // }

    private void parseObserver(final Object anything) {
        Asserts.notNull(anything);
        if (anything.getClass().isArray()) {
            for (Object obj : (Object[]) anything) {
                parseObserver(obj);
            }
        } else if (anything instanceof Iterator<?>) {
            Iterator<?> it = (Iterator<?>) anything;
            while (it.hasNext()) {
                parseObserver(it.next());
            }
        } else if (anything instanceof Iterable<?>) {
            parseObserver(((Iterable<?>) anything).iterator());
        } else if (anything instanceof Map<?, ?>) {
            parseObserver(((Map<?, ?>) anything).values());
        } else {
            prepareBucket(anything);
        }
    }

    // TODO 启动一个整理线程
    private void prepareBucket(final Object observer) {
        Functions.foreach(Containers.newArrayList(observer.getClass().getMethods()), new Functions.ICndAction<Method>() {
            Observe observe;

            public boolean cnd(Method t) {
                return (observe = Annotations.annotation(t, Observe.class)) != null;
            }

            public void act(Method t) {
                String hint = Strings.emptyTo(observe.hint(), t.getName());// 如果hint为空则hint就是方法名
                methodBucket.put(hint, MethodWrapper.builder().build(observer, t).build());
            }
        });
    }

    // notice的时候，允许有返回值
    public void notice(final String hint, final Object... arguments) {// notify是Object类方法，可以重载使用，但是不方便语法提示
        final Collection<MethodWrapper> wrappers = methodBucket.get(hint);
        final Collection<Callable<Object>> tasks = toCallable(wrappers, arguments);
        dispatcher.dispatch(tasks);
    }

    // 只执行一个
    public <T> T noticeOne(final String hint, final Object... arguments) throws LebException {
        final Collection<MethodWrapper> wrappers = methodBucket.get(hint);
        final Collection<Callable<T>> tasks = toCallable(wrappers, arguments);
        if (tasks.isEmpty())
            return null;
        Future<T> future = dispatcher.dispatch(tasks.iterator().next());
        try {
            return Concurrents.future(future);
        } catch (Exception e) {
            throw new LebException(e);
        }
    }

    // // 都尝试执行，但只有一个返回，其他的被中断
    // public <T> T noticeAny(final String hint, final Object... arguments) throws LebException {
    // final Collection<MethodWrapper> wrappers = methodBucket.get(hint);
    // final Collection<Callable<T>> tasks = toCallable(wrappers, arguments);
    // try {
    // return dispatcher.dispatchAny(tasks);
    // } catch (Exception e) {
    // throw new LebException(e);
    // }
    // }
    //
    // public <T> Collection<Future<T>> noticeAll(final String hint, final Object... arguments) {
    // final Collection<MethodWrapper> wrappers = methodBucket.get(hint);
    // final Collection<Callable<T>> tasks = toCallable(wrappers, arguments);
    // return dispatcher.dispatchAll(tasks);
    // }

    // 正则匹配不允许有返回值
    public void noticeRegex(final String hintExpression, final Object... arguments) {
        final Collection<String> keys = methodBucket.keys();
        Functions.foreach(keys, new Functions.ICndAction<String>() {
            public boolean cnd(String t) {
                return Pattern.matches(hintExpression, t);
            }

            public void act(String t) {
                notice(t, arguments);
            }
        });
    }

    // 广播不允许有返回值
    public void broadcast(final Object... arguments) {
        final Collection<Collection<MethodWrapper>> wrappersCollection = methodBucket.values();
        Functions.forall(wrappersCollection, new Functions.IAction<Collection<MethodWrapper>>() {
            public void act(Collection<MethodWrapper> t) {
                final Collection<Callable<Object>> tasks = toCallable(t, arguments);
                dispatcher.dispatch(tasks);
            }
        });
    }

    private <T> Collection<Callable<T>> toCallable(final Collection<MethodWrapper> wrappers, final Object... arguments) {
        return Functions.foreach(wrappers, new Functions.ICndActionR<MethodWrapper, Callable<T>>() {
            // 过滤掉一部分参数不匹配的
            public boolean cnd(MethodWrapper t) {
                return t.matching(arguments);
            }

            public Callable<T> act(MethodWrapper t) {
                return t.toCallable(arguments);
            }
        });
    }

    static class Dispatcher {
        // 默认JVM处理器个数的两倍
        final int poolSize = Systems.processors() * 2;
        final ExecutorService executor = Executors.newFixedThreadPool(poolSize);// CachedThreadPool();
        AtomicInteger counter = new AtomicInteger(0);

        <T> Collection<Future<T>> dispatch(final Collection<Callable<T>> tasks) {
            Logs.p(counter.getAndIncrement());
            return Functions.forall(tasks, new Functions.IActionR<Callable<T>, Future<T>>() {
                public Future<T> act(Callable<T> t) {
                    return executor.submit(t);
                }
            });
        }

        <T> Future<T> dispatch(final Callable<T> task) {
            Logs.p(counter.getAndIncrement());
            return executor.submit(task);
        }

        // <T> T dispatchAny(final Collection<Callable<T>> tasks) throws Exception {
        // Logs.p(counter.getAndIncrement());
        // return Concurrents.invokeAny(executor, tasks);
        // }
        //
        // <T> Collection<Future<T>> dispatchAll(final Collection<Callable<T>> tasks) {
        // Logs.p(counter.getAndIncrement());
        // return Concurrents.invokeAll(executor, tasks);
        // }

        public static Dispatcher newInstance() {
            return new Dispatcher();
        }
    }

    static class MethodBucket {

        private final ConcurrentMap<String, Collection<MethodWrapper>> bucket = Containers.newConcurrentHashMap();

        public Collection<MethodWrapper> get(String key) {
            if (!bucket.containsKey(key))
                throw new LebException(String.format("hint %s is not found!", key));
            return bucket.get(key);
        }

        public Collection<Collection<MethodWrapper>> values() {
            return bucket.values();
        }

        public Collection<String> keys() {
            return bucket.keySet();
        }

        public void put(String key, MethodWrapper e) {
            Collection<MethodWrapper> wrappers = bucket.get(key);
            if (wrappers == null) {
                wrappers = Containers.newCopyOnWriteArraySet();
                wrappers = bucket.putIfAbsent(key, wrappers) == null ? wrappers : bucket.get(key);
            }
            wrappers.add(e);
        }

        public static MethodBucket newInstance() {
            return new MethodBucket();
        }
    }

    static class MethodWrapper {
        private Method method;
        private Object observer;

        boolean matching(final Object[] arguments) {
            final Class<?>[] parameterClasses = method.getParameterTypes();
            final Class<?>[] argumentClasses = Reflects.findClasses(arguments);
            return Arrays.equals(parameterClasses, argumentClasses);
        }

        <T> Callable<T> toCallable(final Object... arguments) {
            return new Callable<T>() {
                public T call() throws Exception {
                    return Reflects.invoke(method, observer, arguments);
                }
            };
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MethodWrapper))
                return false;
            return this.observer.equals(((MethodWrapper) obj).observer)
                    && this.method.equals(((MethodWrapper) obj).method);
        }

        @Override
        public int hashCode() {
            return this.observer.hashCode() ^ this.method.hashCode();
        }

        public static MethodWrapperBuilder builder() {
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