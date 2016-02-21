package org.buru.sardine.exception;

/**
 * Sardine工程的Runtime Exception
 * 
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-4-10
 */
public class SardineException extends RuntimeException {

	public SardineException() {
		super();
	}

	public SardineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SardineException(String message, Throwable cause) {
		super(message, cause);
	}

	public SardineException(String message) {
		super(message);
	}

	public SardineException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = -560864409855490687L;
}