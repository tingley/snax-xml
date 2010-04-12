package net.sundell.snax;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * Matches any descendant node with a given name.
 */
public class DescendantEqualsSelector<T> extends ElementEqualsSelector<T> {

    DescendantEqualsSelector(NodeModelBuilder<T> context, QName qname, List<ElementConstraint> constraints) {
        super(context, qname, constraints);
    }
    
    DescendantEqualsSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, QName qname, 
                            List<ElementConstraint> constraints) {
        super(context, parent, qname, constraints);
    }

    @Override
    protected NodeState<T> addState(NodeState<T> baseState) {
        return baseState.addDescendantRule(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof DescendantEqualsSelector)) return false;
        return getQName().equals(((DescendantEqualsSelector)o).getQName());
    }
    
    @Override
    public String toString() {
        return "descendant-equals(" + getQName() + ")";
    }

}
