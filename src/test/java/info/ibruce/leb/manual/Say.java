package info.ibruce.leb.manual;

import java.util.Date;

import info.ibruce.leb.annotation.Observe;
import info.ibruce.sardine.lang.Times;
import info.ibruce.sardine.log.Logs;

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
