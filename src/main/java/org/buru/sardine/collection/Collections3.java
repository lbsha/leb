package org.buru.sardine.collection;

import java.util.Collection;

/**
 * JDK有Collections，GUAVA有Collections2，我只好Collections3
 * 
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-3-23
 */
public class Collections3 {

	@SafeVarargs
	public static <T> void addAll(Collection<T> collections, T... ts) {// 1.7下有警告，1.6下没有
		for (T t : ts) {
			collections.add(t);
		}
	}

}
