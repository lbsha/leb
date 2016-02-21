package org.buru.sardine.log;

import java.io.PrintStream;

import org.buru.sardine.lang.Times;

/**
 * introduction
 * 
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-3-23
 */
public/* final */class Logs {

	static private final PrintStream consoleInfo = System.out;
	static private final PrintStream consoleError = System.err;

	public static void pt(String timeTemplate) {
		printString(timeTemplate, Times.now());
	}

	public static void p(Object obj) {
		// TODO:判断obj的类型，如数组等
		printString(String.valueOf(obj));
	}

	public static void p(String messageTemplate, Object... args) {
		printString(messageTemplate, args);
	}

	public static void p(Throwable t, String messageTemplate, Object... args) {
		printString(messageTemplate, args);
		printException(t);
	}

	public static void p(Throwable t) {
		printException(t);
	}

	/**
	 * 带格式的
	 * 
	 * @param messageTemplate
	 * @param args
	 */
	public static void p(Class<?> clazz, String messageTemplate, Object... args) {
		messageTemplate = String.format("%s:%s:", Times.now(), clazz.getName()) + messageTemplate;
		printString(messageTemplate, args);
	}

	static private void printString(String format, Object... args) {
		consoleInfo.println(String.format(format, args));
	}

	static private void printException(Throwable t) {
		t.printStackTrace(consoleError);
	}
}
