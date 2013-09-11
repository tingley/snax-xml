package net.sundell.snax;

import javax.xml.stream.events.StartElement;

/**
 * A constraint that indicates whether or not an element
 * is eligible to follow a given state transition.
 */
public interface ElementConstraint {
    /**
     * Test if an element satisfies this constraint.
     * @param element element to test
     * @return whether the test was satisfied
     */
    boolean matches(StartElement element);
}
