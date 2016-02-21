package org.buru.sardine.lang;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * introduction
 * 
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-3-23
 */
public class Times {

	static private final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.sss";

	public static long currentMilliseconds() {
		return System.currentTimeMillis();
	}

	public static long currentNanoseconds() {
		return System.nanoTime();
	}

	public static String now() {
		final SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_SSS);
		return format.format(new Date());
	}

	public static String format(Date date) {
		final SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_SSS);
		return format.format(date);
	}
}
