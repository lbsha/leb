package info.ibruce.sardine;

import java.util.Collection;
import java.util.Iterator;

import info.ibruce.sardine.asserts.Asserts;
import info.ibruce.sardine.collection.Containers;

/**
 * 方法块
 * <p>
 * <ol>
 * <li>该类坚决不抛异常</li>
 * </ol>
 *
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-4-24
 */
public final class Functions {

    static private final double DEFAULT_LOAD_FACTOR = 1.75D;

    /**
     * 过滤出满足条件的集合
     *
     * @param collection 原集合
     * @param condition 条件表达式
     * @return
     */
    public static <T> Collection<T> filter(Collection<T> collection, ICondition<T> condition) {
        Asserts.notNull(collection, condition);
        final Collection<T> result = Containers.newArrayList(collection.size());
        for (Iterator<T> it = collection.iterator(); it.hasNext(); ) {
            T t = it.next();
            if (condition.cnd(t))
                result.add(t);
        }
        return result;
    }

    public static <T> T filterOne(Collection<T> collection, ICondition<T> condition) {
        Asserts.notNull(collection, condition);
        for (Iterator<T> it = collection.iterator(); it.hasNext(); ) {
            T t = it.next();
            if (condition.cnd(t))
                return t;
        }
        return null;
    }

    public static <T> Collection<T> remove(Collection<T> collection, ICondition<T> condition) {
        Asserts.notNull(collection, condition);
        final Collection<T> result = Containers.newArrayList(collection.size());
        for (Iterator<T> it = collection.iterator(); it.hasNext(); ) {
            T t = it.next();
            if (condition.cnd(t))
                continue;
            result.add(t);
        }
        return result;
    }

    public static <T> Collection<T> foreach(Collection<T> collection, ICndAction<T> cndAction) {
        Asserts.notNull(collection, cndAction);
        final Collection<T> result = Containers.newArrayList(collection.size());
        for (Iterator<T> it = collection.iterator(); it.hasNext(); ) {
            T t = it.next();
            if (cndAction.cnd(t)) {
                cndAction.act(t);
                result.add(t);
            }
        }
        return result;
    }

    public static <T> Collection<T> forall(Collection<T> collection, ICndActions<T> cndActions) {
        Asserts.notNull(collection, cndActions);
        final Collection<T> result = Containers.newArrayList(collection.size());
        for (Iterator<T> it = collection.iterator(); it.hasNext(); ) {
            T t = it.next();
            if (cndActions.cnd(t)) {
                cndActions.leftAct(t);
            } else {
                cndActions.rightAct(t);
            }
            result.add(t);
        }
        return result;
    }

    public static <T, R> Collection<R> foreach(Collection<T> collection, ICndActionR<T, R> cndActionR) {
        Asserts.notNull(collection, cndActionR);
        final Collection<R> result = Containers.newArrayList(collection.size());
        for (Iterator<T> it = collection.iterator(); it.hasNext(); ) {
            T t = it.next();
            if (cndActionR.cnd(t)) {
                R r = cndActionR.act(t);
                result.add(r);
            }
        }
        return result;
    }

    public static <T, R> Collection<R> foreach(Collection<T> collection, ICndActionRc<T, R> cndActionRc) {
        Asserts.notNull(collection, cndActionRc);
        final Collection<R> result = Containers.newArrayList((int) (collection.size() * DEFAULT_LOAD_FACTOR));
        for (T t : collection) {
            if (cndActionRc.cnd(t)) {
                Collection<R> r = cndActionRc.act(t);
                result.addAll(r);
            }
        }
        return result;
    }

    public static <T> Collection<T> forall(Collection<T> collection, IAction<T> action) {
        Asserts.notNull(collection, action);
        final Collection<T> result = Containers.newArrayList(collection.size());
        for (Iterator<T> it = collection.iterator(); it.hasNext(); ) {
            T t = it.next();
            action.act(t);
            result.add(t);
        }
        return result;
    }

    public static <T, R> Collection<R> forall(Collection<T> collection, IActionR<T, R> actionR) {
        Asserts.notNull(collection, actionR);
        final Collection<R> result = Containers.newArrayList(collection.size());
        for (Iterator<T> it = collection.iterator(); it.hasNext(); ) {
            T t = it.next();
            R r = actionR.act(t);
            result.add(r);
        }
        return result;
    }

    /**
     * 计算结果集个数
     *
     * @param collection 原集合
     * @param actionR 计算表达式
     * @return
     */
    public static <T, R> int count(Collection<T> collection, IActionR<T, R> actionR) {
        Asserts.notNull(collection, actionR);
        final Collection<R> counter = Containers.newHashSet(collection.size());
        for (Iterator<T> it = collection.iterator(); it.hasNext(); ) {
            T t = it.next();
            R r = actionR.act(t);
            counter.add(r);
        }
        return counter.size();
    }

    public static <T, R> Collection<R> forall(Collection<T> collection, IActionRc<T, R> actionRc) {
        Asserts.notNull(collection, actionRc);
        final Collection<R> result = Containers.newArrayList((int) (collection.size() * DEFAULT_LOAD_FACTOR));
        for (Iterator<T> it = collection.iterator(); it.hasNext(); ) {
            T t = it.next();
            Collection<R> r = actionRc.act(t);
            result.addAll(r);
        }
        return result;
    }

    public static <T extends Comparable<T>> T max(Collection<T> collection) {
        return max(collection, new IExpression<T, T>() {
            public T exp(T t) {
                return t;
            }
        });
    }

    public static <T extends Comparable<T>> T min(Collection<T> collection) {
        return min(collection, new IExpression<T, T>() {
            public T exp(T t) {
                return t;
            }
        });
    }

    public static <T, E extends Comparable<E>> T max(Collection<T> collection, IExpression<T, E> expression) {
        return pick(collection, expression, true);
    }

    public static <T, E extends Comparable<E>> T min(Collection<T> Collection, IExpression<T, E> expression) {
        return pick(Collection, expression, false);
    }

    static private final <T, E extends Comparable<E>> T pick(Collection<T> collection, IExpression<T, E> expression,
                                                             boolean isMax) {
        Asserts.notNull(collection, expression);
        T pick = null;
        for (Iterator<T> it = collection.iterator(); it.hasNext(); ) {
            T t = it.next();

            if (pick == null) {
                pick = t;
            }
            E eS = expression.exp(pick);
            E eT = expression.exp(t);

            if (isMax) {
                if (eT.compareTo(eS) > 0) {
                    pick = t;
                }

            } else if (eT.compareTo(eS) < 0) {
                pick = t;
            }
        }
        return pick;
    }

    public static <T> T act(T t, IAction<T> action) {
        action.act(t);
        return t;
    }

    /************************************************************************************************************/

    public static interface ICndActionRc<T, R> extends ICondition<T> {
        public Collection<R> act(T t);
    }

    public static interface ICndActionR<T, R> extends ICondition<T> {
        public R act(T t);
    }

    public static interface ICndActions<T> extends ICondition<T> {
        public void leftAct(T t);

        public void rightAct(T t);
    }

    public static interface ICndAction<T> extends ICondition<T> {
        public void act(T t);
    }

    public static interface IActionRc<T, R> {
        public Collection<R> act(T t);
    }

    public static interface IActionR<T, R> {
        public R act(T t);
    }

    public static interface IAction<T> {
        public void act(T t);
    }

    public static interface IExpression<T, E> {
        public E exp(T t);
    }

    public static interface ICondition<T> {
        public boolean cnd(T t);
    }
}