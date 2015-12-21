snax-xml
--------

SNAX exists to simplify XML parsing using the StAX (`javax.xml.stream`) event
API by replacing hand-coded state machines with concise, descriptive code to
select the content you care about. SNAX is small, simple, adds very little
overhead to StAX, and contains no additional dependencies.

SNAX uses a simple EDSL to specify a set of element `selectors`, to which you
attach callback interfaces. SNAX then uses this information to build a reusable
`NodeModel` object, against which it parses the XML. `XMLEvent` data will be
routed to the right pieces of code automatically. The EDSL is conceptually
similar to basic XPath, although it is greatly simplified for the purposes of
stream processing. The [Getting Started](https://github.com/tingley/snax-xml/wiki/Getting-Started) page has a better explanation.

Building
========

SNAX builds with [maven](http://maven.apache.org).

To use SNAX in your own project, add it as a dependency:
```xml
    <dependency>
      <groupId>net.sundell.snax</groupId>
      <artifactId>snax</artifactId>
      <version>0.10</version>
    </dependency>
```

Downloads
=========

Check the [Releases page](https://github.com/tingley/snax-xml/releases).
