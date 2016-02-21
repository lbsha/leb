package org.buru.sardine.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 集合工厂
 * <p>
 * <ol>
 * <li>集合选择策略：http://www.javamex.com/tutorials/collections/how_to_choose_2.shtml</li>
 * </ol>
 * 
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-4-10
 */
public final class Containers {
	/**
	 ***************************************************************** LIST **********************************************************************
	 * 
	 * @return
	 */
	protected static <E> List<E> newList() {
		throw new NoSuchMethodError("this method is not implemented !");
	}

	/**
	 * non thread safe / fixed order
	 * 
	 * @return
	 */
	public static <E> ArrayList<E> newArrayList() {
		return new ArrayList<E>();
	}

	public static <E> ArrayList<E> newArrayList(int initialCapacity) {
		return new ArrayList<E>(initialCapacity);
	}

	public static <E> ArrayList<E> newArrayList(Collection<? extends E> c) {
		return new ArrayList<E>(c);
	}

	public static <E> ArrayList<E> newArrayList(@SuppressWarnings("unchecked") E... e) {
		return new ArrayList<E>(Arrays.asList(e));
	}

	public static <E> LinkedList<E> newLinkedList() {
		return new LinkedList<E>();
	}

	/**
	 * thread safe
	 * 
	 * @return
	 */
	public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
		return new CopyOnWriteArrayList<E>();
	}

	/**
	 * thread safe / fixed order
	 * 
	 * @return
	 */
	public static <E> Vector<E> newVector() {
		return new Vector<E>();
	}

	public static <E> Vector<E> newVector(int initialCapacity) {
		return new Vector<E>(initialCapacity);
	}

	/**
	 ***************************************************************** SET **********************************************************************
	 * <table>
	 * <tr>
	 * <td>Ordering of keys</td>
	 * <td>Non-concurrent</td>
	 * <td>Concurrent</td>
	 * </tr>
	 * <tr>
	 * <td>No particular order</td>
	 * <td>HashSet</td>
	 * <td>—</td>
	 * </tr>
	 * <tr>
	 * <td>Sorted</td>
	 * <td>TreeSet</td>
	 * <td>ConcurrentSkipListSet</td>
	 * </tr>
	 * <tr>
	 * <td>Fixed</td>
	 * <td>LinkedHashSet</td>
	 * <td>CopyOnWriteArraySet</td>
	 * </tr>
	 * </table>
	 * 
	 * @return
	 */
	protected static <E> Set<E> newSet() {
		throw new NoSuchMethodError("this method is not implemented !");
	}

	public static <E> HashSet<E> newHashSet() {
		return new HashSet<E>();
	}

	public static <E> HashSet<E> newHashSet(int initialCapacity) {
		return new HashSet<E>(initialCapacity);
	}

	public static <E> TreeSet<E> newTreeSet() {
		return new TreeSet<E>();
	}

	public static <E> LinkedHashSet<E> newLinkedHashSet() {
		return new LinkedHashSet<E>();
	}

	public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
		return new CopyOnWriteArraySet<E>();
	}

	public static <E> ConcurrentSkipListSet<E> newConcurrentSkipListSet() {
		return new ConcurrentSkipListSet<E>();
	}

	/**
	 ***************************************************************** MAP **********************************************************************
	 * <table>
	 * <tr>
	 * <td>Ordering of keys</td>
	 * <td>Non-concurrent</td>
	 * <td>Concurrent</td>
	 * </tr>
	 * <tr>
	 * <td>No particular order</td>
	 * <td>HashMap</td>
	 * <td>ConcurrentHashMap</td>
	 * </tr>
	 * <tr>
	 * <td>Sorted</td>
	 * <td>TreeMap</td>
	 * <td>ConcurrentSkipListMap</td>
	 * </tr>
	 * <tr>
	 * <td>Fixed</td>
	 * <td>LinkedHashMap</td>
	 * <td>—</td>
	 * </tr>
	 * </table>
	 * 
	 * @return
	 */
	protected static <K, V> Map<K, V> newMap() {
		throw new NoSuchMethodError("this method is not implemented !");
	}

	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}

	public static <K, V> TreeMap<K, V> newTreeMap() {
		return new TreeMap<K, V>();
	}

	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
		return new LinkedHashMap<K, V>();
	}

	public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
		return new ConcurrentHashMap<K, V>();
	}

	public static <K, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap() {
		return new ConcurrentSkipListMap<K, V>();
	}

	public static <K, V> LRUHashMap<K, V> newLRUHashMap(final int maxSize) {
		return new LRUHashMap<K, V>(maxSize);
	}

	/**
	 ***************************************************************** QUEUE **********************************************************************
	 * 
	 * @return
	 */
	protected static <E> Queue<E> newQueue() {
		throw new NoSuchMethodError("this method is not implemented !");
	}

	/****************************************************************** HELP **********************************************************************/

	/**
	 * 元素顺序
	 */
	/* public */static enum Order {
		/* 无序，排序，放入顺序 */
		NONE, SORTED, FIXED;
	}

	/**
	 * 线程安全
	 */
	/* public */static enum Safe {
		/* 非线程安全，线程安全 */
		NONE_THREADSAFE, THREADSAFE;
	}
}
