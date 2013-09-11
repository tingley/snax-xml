package net.sundell.snax;

import java.util.Collections;
import java.util.List;

/**
 * Matches any child node with the specified constraints.
 */
class ChildSelector<T> extends ElementSelector<T> {
    
    ChildSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, 
                    List<ElementConstraint> constraints) {
        super(context, parent, constraints);
    }
    
    ChildSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, 
            ElementConstraint constraint) {
        super(context, parent, Collections.singletonList(constraint));
    }
    
    @Override
    NodeState<T> addState(NodeState<T> baseState) {
        ElementConstraint test = new ElementSelectorTest<T>(this);
        return baseState.addTransition(test, new NodeState<T>());
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
