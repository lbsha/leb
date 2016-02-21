leb
=======

> leb  <- leb event bus  <- liebao event bus

## Hello World

POJO
```
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
```

Client

```
Lebs leb = Lebs.singleton();
leb.observers(new Say());

leb.notice("say");// hello leb
leb.notice("say", "bruce");// hello bruce
leb.notice("bye");// bye leb
leb.notice("say", new Date());// 2013-05-19 13:53:16.016
leb.broadcast("say", 1);// 不执行任何方法，没有匹配上的参数
```