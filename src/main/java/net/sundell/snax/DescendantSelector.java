package net.sundell.snax;

import java.util.Collections;
import java.util.List;

import javax.xml.stream.events.StartElement;

/**
 * Matches any descendant node.
 */
class DescendantSelector<T> extends ElementSelector<T> {
    
    DescendantSelector(NodeModelBuilder<T> context, List<ElementConstraint> constraints) {
        super(context, constraints);
    }

    DescendantSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, 
                    List<ElementConstraint> constraints) {
        super(context, parent, constraints);
    }
    
    DescendantSelector(NodeModelBuilder<T> context, ElementConstraint constraint) {
        super(context, Collections.singletonList(constraint));
    }
    
    DescendantSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, 
            ElementConstraint constraint) {
        super(context, parent, Collections.singletonList(constraint));
    }
    
    // TODO: refactor with ChildSelector
    @Override
    protected boolean matches(StartElement element) {
        for (ElementConstraint constraint : getConstraints()) {
            if (!constraint.matches(element)) return false;
        }
        return true;
    }

    @Override
    protected NodeState<T> addState(NodeState<T> baseState) {
        return baseState.addDescendantRule(this);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof DescendantSelector) && super.equals(o);
    }
    
    @Override
    public String toString() {
        return "descendent()";
    }
}
