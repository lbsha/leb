package info.ibruce.sardine.concurrent;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import info.ibruce.sardine.exception.SardineException;
import info.ibruce.sardine.log.Logs;

/**
 * introduction
 *
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-4-17
 */
public class Concurrents {

    public static <T> T future(final Future<T> future) throws Exception {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Logs.p(e);
            // 清除中断状态
            // if (Thread.currentThread().isInterrupted())
            // Thread.interrupted();
            // 保留中断状态，以便高层处理
            Thread.currentThread().interrupt();
            throw new SardineException(e);
        } catch (ExecutionException e) {
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

    public static <T> T invokeAny(final ExecutorService executor, final Collection<? extends Callable<T>> tasks)
            throws Exception {
        try {
            return executor.invokeAny(tasks);
        } catch (InterruptedException e) {
            Logs.p(e);
            // 保留中断状态，以便高层处理
            Thread.currentThread().interrupt();
            throw new SardineException(e);
        } catch (ExecutionException e) {
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

    public static <T> Collection<Future<T>> invokeAll(final ExecutorService executor,
                                                      final Collection<? extends Callable<T>> tasks) throws SardineException {
        try {
            return executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Logs.p(e);
            // 保留中断状态，以便高层处理
            Thread.currentThread().interrupt();
            throw new SardineException(e);
        }
    }
}
