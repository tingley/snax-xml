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
    	if (getConstraints().size() > 0) {
    	    // Constrained descendant rule.  Four states are now in play: 
    	    // - The base state [B] transitions to a new state X if the constraints
    	    //   are satisfied; otherwise it transitions to its default [B_d]
    	    // - [X] transitions to *itself* if it still satisfies the constraints, and
    	    //   to its default [X_d] if it does match anything else
	    	// - [B_d] and [X_d] both transition to [X] if the original constraints 
    	    //   were satisfied 
    	    // The extra twist with [X]<->[X_d] is so that, for example, 
    	    //   descendant(with("foo").equalTo("bar"))
    	    // matches both qualifying nodes in:
    	    //   <e1 foo="bar"><e2 foo="bar"/></e1>
	    	NodeState<T> defaultState = baseState.getDefaultState(); 
	    	NodeTest<T> constraint = new DescendantSelectorTest<T>(this);
	    	NodeState<T> newState = baseState.addTransition(constraint, new NodeState<T>());
	    	
	    	// XXX There is a potential rule priority issue here
	    	newState.addTransition(constraint, newState);
	    	
	    	// Wire this state to the new one, and the new state back and forth with its
	    	// own default state
	    	defaultState.addTransition(constraint, newState);
	    	NodeState<T> newStateDefault = newState.getDefaultState();
	    	newStateDefault.addTransition(constraint, newState);
	    	return newState;
    	}
    	else {
    		// Unconstrained descendant rule 
    	    return baseState.getDefaultState();
    	}
    }

    static class DescendantSelectorTest<T> extends ElementSelector.ElementSelectorTest<T> {
        public DescendantSelectorTest(ElementSelector<T> selector) {
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
