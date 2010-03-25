package net.sundell.snax;

import java.util.List;

import javax.xml.stream.events.StartElement;

/**
 * Matches any child node.
 */
class ChildSelector<T> extends ElementSelector<T> {
    
    ChildSelector(NodeModelBuilder<T> context, List<ElementConstraint> constraints) {
        super(context, constraints);
    }

    ChildSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, 
                    List<ElementConstraint> constraints) {
        super(context, parent, constraints);
    }

    @Override
    protected boolean matches(StartElement element) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        return (o != null && (o instanceof ChildSelector));
    }
    
    @Override
    public String toString() {
        return "child()";
    }

}
