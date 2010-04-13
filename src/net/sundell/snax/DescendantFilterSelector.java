package net.sundell.snax;

/**
 * Matches any descendant node that satisfies a specified {@link ElementFilter}.
 */
class DescendantFilterSelector<T> extends ElementFilterSelector<T> {

    DescendantFilterSelector(NodeModelBuilder<T> context, ElementFilter filter) {
        super(context, filter);
    }
    
    DescendantFilterSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, 
                             ElementFilter filter) {
        super(context, parent, filter);
    }

    @Override
    protected NodeState<T> addState(NodeState<T> baseState) {
        return baseState.addDescendantRule(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof DescendantFilterSelector)) return false;
        return getFilter().equals(((DescendantFilterSelector)o).getFilter());
    }
    
    @Override
    public String toString() {
        return "descendant-filter(" + getFilter() + ")";
    }

}
