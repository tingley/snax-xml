package net.sundell.snax;

import javax.xml.stream.events.StartElement;

/**
 * Circular states return themselves as a default transition.  They are
 * used in constructing state for descendant() selectors.
 */
class CircularState<T> extends NodeState<T> {

    CircularState() {       
    }
    
    NodeState<T> follow(StartElement element) {
        assert (element != null);
        for (NodeTransition<T> transition : getTransitions()) {
            if (transition.getTest().matches(element)) {
                return transition.getTarget();
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return "CircularState[" + getTransitions().size() + "]";
    }

}
