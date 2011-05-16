package net.sundell.snax;

import javax.xml.stream.events.StartElement;

interface NodeTest<T> {
    boolean matches(StartElement element);
}
