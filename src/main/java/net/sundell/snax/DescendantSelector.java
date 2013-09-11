package net.sundell.snax;

import java.util.Collections;
import java.util.List;

/**
 * Matches any descendant node with the specified constraints.
 */
class DescendantSelector<T> extends ElementSelector<T> {
    
    DescendantSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, 
                    List<ElementConstraint> constraints) {
        super(context, parent, constraints);
    }
    
    DescendantSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, 
            ElementConstraint constraint) {
        super(context, parent, Collections.singletonList(constraint));
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
