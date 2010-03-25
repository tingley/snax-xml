package net.sundell.snax;

import javax.xml.stream.events.StartElement;

public interface ElementConstraint {
    boolean matches(StartElement element);
}
