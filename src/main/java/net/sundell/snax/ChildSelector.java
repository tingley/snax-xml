package net.sundell.snax;

import java.util.Collections;
import java.util.List;

/**
 * Matches any child node with the specified constraints.
 */
public class ChildSelector<T> extends ElementSelector<T> {
    private int onlyValue = Integer.MAX_VALUE;

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

    /**
     * Restrict the number of times the node represented by this selector
     * can occur in an XML document. Exceeding this count will cause a
     * {@link SNAXUserException} to be thrown during runtime.
     * @param only
     * @return updated selector
     */
    public ChildSelector<T> only(int only) {
        if (only < 1) {
            throw new IllegalArgumentException("only() value must be positive");
        }
        this.onlyValue = only;
        return this;
    }
    
    @Override
    NodeState<T> buildState() {
        NodeState<T> state = super.buildState();
        state.setOnlyValue(onlyValue);
        return state;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ChildSelector) && super.equals(o) &&
                onlyValue == ((ChildSelector<?>)o).onlyValue;
    }
    
    @Override
    public String toString() {
        return "child()";
    }

}
