package org.buru.leb;

import java.util.Date;

import org.buru.sardine.log.Logs;
import org.junit.Test;

public class HelloMain {
	@Test
	public void test() throws InterruptedException {

		Lebs lebs = Lebs.singleton();
		lebs.observers(new Hello(lebs));

		// String now = lebs.noticeOne("sayTime", new Date());
		// Logs.p(now);
		// lebs.notice("say");
		// lebs.notice("sayTime");
		// lebs.notice("sayTime", new Date());
		// lebs.notice("bye");
		// try {
		// lebs.broadcast(new Date());
		// } catch (Exception e) {
		// Logs.p(e);
		// }
		// lebs.notice("hello");
		lebs.notice("say", "jolene");

		lebs.notice("sayHello");
		lebs.notice("sayHello", "jolene");
		lebs.notice("sayTime");
		lebs.notice("sayTime", new Date());

		// lebs.notice("count", 0);

		// 以s开头的，并且无参的
		// lebs.noticeRegex("^(s).*$");

		Thread.sleep(1000 * 100);
	}
}
