Tex - A Text Exporter Library
===

A simple and powerful text exporter (and other formats, too).

With this library you could generate that scary integration file acoording to the layout specified by that creepy mainframe people.

It's virtually impossible to think about every way to format data to text, and I don't even begin doing it. Insted, Tex was build of specific and decoupled components you can easily extend.

I know there is another projects out there, but that's my toy! So I made it with learning purposes and also to offer you an simple and extensible text library.

Features
--

* Extensible: you can override every component.
* Flexible: can generate output in many formats, and did I told you it's extensible?
* Fast: not compared with other libraries, but probably better than yout custom code, since it has benn optimized in many ways.
* Ease to use: simple and straightforward configuration and API.
* Best practices: avoid problems with syncrhonization (NumberFormat and SimlpeDateFormat) and others.


Examples
--
```java
Tex tex = new Tex(getParamMap());
tex.registerFileAppender(new File("-output.txt"), false);
tex.registerLayout(getClass(), "layout.xml");
e.exportHeader(getHeaderMap());
for (...) {
    tex.exportDetail(getDetailMap());
}
tex.exportFooter(getFooterMap());
tex.close();
```
