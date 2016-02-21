package org.buru.sardine.collection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * introduction
 * 
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-5-18
 */
public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {
	private final int maxSize;
	static final float DEFAULT_LOAD_FACTOR = 0.75F;

	public LRUHashMap(int initialSize, int maxSize) {
		super(initialSize, 0.75F, true);
		this.maxSize = maxSize;
	}

	public LRUHashMap(int maxSize) {
		this(maxSize, maxSize);
	}

	public int getMaxSize() {
		return this.maxSize;
	}

	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > this.maxSize;
	}

	private static final long serialVersionUID = 8224204177290776177L;
}