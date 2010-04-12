package net.sundell.snax;

import java.util.List;

import javax.xml.stream.events.StartElement;

/**
 * Matches any descendant node.
 */
public class DescendantSelector<T> extends ElementSelector<T> {
    
    DescendantSelector(NodeModelBuilder<T> context, List<ElementConstraint> constraints) {
        super(context, constraints);
    }

    DescendantSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, 
                    List<ElementConstraint> constraints) {
        super(context, parent, constraints);
    }
    
    @Override
    protected boolean matches(StartElement element) {
        return true;
    }

    @Override
    protected NodeState<T> addState(NodeState<T> baseState) {
        return baseState.addDescendantRule(this);
    }

    static class DescendantSelectorTest<T> extends ElementSelector.ElementSelectorTest<T> {
        public DescendantSelectorTest(DescendantSelector<T> selector) {
            super(selector);
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null || !(o instanceof DescendantSelectorTest)) {
                return false;
            }
            return super.equals(o);
        }
        
        @Override
        public String toString() {
            return "DescendantSelectorTest(" + getSelector() + ")";
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        return (o != null && (o instanceof DescendantSelector));
    }
    
    @Override
    public String toString() {
        return "descendent()";
    }
}
