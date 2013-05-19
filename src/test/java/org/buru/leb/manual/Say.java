package org.buru.leb.manual;

import java.util.Date;

import org.buru.leb.annotation.Observe;
import org.buru.sardine.lang.Times;
import org.buru.sardine.log.Logs;

/**
 * introduction
 * 
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-5-19
 */
public class Say {
	@Observe(hint = "say")
	public void sayHello() {
		Logs.p("hello leb");
	}

	@Observe(hint = "say")
	public void sayTo(String who) {
		Logs.p("hello " + who);
	}

	@Observe
	public void bye() {
		Logs.p("bye leb");
	}

	@Observe(hint = "say")
	public void sayTime(Date date) {
		Logs.p(Times.format(date));
	}
}
