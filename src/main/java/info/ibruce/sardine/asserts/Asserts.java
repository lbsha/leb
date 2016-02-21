package info.ibruce.sardine.asserts;

/**
 * introduction
 *
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-4-10
 */
public final class Asserts {

    public static <T> T notNull(T o) {
        if (o == null)
            throw new NullPointerException("argument should not be null !");
        return o;
    }

    public static <T> T[] notNull(@SuppressWarnings("unchecked") T... os) {
        for (int i = 0, n = os.length; i < n; i++) {
            if (os[i] == null)
                throw new NullPointerException(String.format("argument %s should not be null !", i));
        }
        return os;
    }

    public static <T> T notNull(T o, ICallbackRuntimeException<?> callbackRuntimeException) {
        if (o == null)
            throw callbackRuntimeException.callback();
        return o;
    }

    public static interface ICallbackRuntimeException<T extends RuntimeException> {
        T callback() throws RuntimeException;
    }
}
