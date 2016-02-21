package org.buru.sardine.lang;

/**
 * introduction
 * 
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-4-10
 */
public class Strings {

	public static boolean equals(String str, String anotherStr) {
		if (str == null)
			return anotherStr == null;

		return str.equals(anotherStr);
	}

	public static boolean isEmpty(String str) {
		return (str == null) || str.isEmpty();
	}

	public static String emptyTo(String str, String to) {
		return isEmpty(str) ? to : str;
	}
}
