package org.buru.leb.manual;

import java.util.Date;

import org.buru.leb.Lebs;

public class Client {
	public static void main(String[] args) {
		Lebs leb = Lebs.singleton();
		leb.observers(new Say());

		leb.notice("say");// hello leb
		leb.notice("say", "bruce");// hello bruce
		leb.notice("bye");// bye leb
		leb.notice("say", new Date());// 2013-05-19 13:53:16.016
		leb.broadcast("say", 1);// 不执行任何方法，没有匹配上的参数
	}
}
