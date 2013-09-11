package net.sundell.snax;

import java.util.Collections;
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
    
    ChildSelector(NodeModelBuilder<T> context, ElementConstraint constraint) {
        super(context, Collections.singletonList(constraint));
    }
    
    ChildSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, 
            ElementConstraint constraint) {
        super(context, parent, Collections.singletonList(constraint));
    }

    @Override
    protected boolean matches(StartElement element) {
        for (ElementConstraint constraint : getConstraints()) {
            if (!constraint.matches(element)) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ChildSelector) && super.equals(o);
    }
    
    @Override
    public String toString() {
        return "child()";
    }

}
