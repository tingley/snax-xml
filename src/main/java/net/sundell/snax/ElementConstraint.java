package net.sundell.snax;

import javax.xml.stream.events.StartElement;

/**
 * An attribute constraint on a selected element.
 */
public interface ElementConstraint {
    /**
     * Test if an element satisfies this constraint.
     * @param element element to test
     * @return whether the test was satisfied
     */
    boolean matches(StartElement element);
}
