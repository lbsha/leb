package org.buru.leb;

import java.util.Date;

import org.buru.leb.annotation.Observe;
import org.buru.sardine.lang.Times;
import org.buru.sardine.log.Logs;

public class Hello {

	Lebs lebs;

	public Hello(Lebs lebs) {
		super();
		this.lebs = lebs;
	}

	@Observe(hint = "hello")
	public void sayHello() {
		Logs.p("hello");
	}

	@Observe(hint = "bye")
	public void sayBye() {
		Logs.p("bye");
	}

	@Observe
	public void bye() {
		Logs.p("byeDefault");
	}

	@Observe(hint = "sayTime")
	public String sayTime(Date date) {
		Logs.p(Times.format(date));
		// throw new NullPointerException();

		return Times.now();
	}

	@Observe(hint = "sayTime")
	public void sayTime() {
		Logs.p(Times.now());
	}

	@Observe(hint = "say")
	public void sayTo(String who) {
		Logs.p("hello " + who);
		lebs.notice("say");
	}

	@Observe(hint = "say")
	public void say() {
		Logs.p("hello bruce");
		// for (int i = 0; i < 2; i++)
		lebs.notice("say", "jolene");
	}

	@Observe(hint = "count")
	public void count(Integer count) {
		Logs.p(count++);
		for (int i = 0; i < count; i++) {
			lebs.notice("say", count.toString());
		}
		lebs.notice("count", count);
	}
}
