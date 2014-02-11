package net.sundell.snax;

import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

/**
 * Base element selector class.
 * 
 * @param <T>
 */
public abstract class ElementSelector<T> extends Selectable<T> {

    private NodeModelBuilder<T> context;
    private List<ElementConstraint> constraints = Collections.emptyList();
    private ElementSelector<T> parent = null;
    private NodeState<T> nodeState = null;

    ElementSelector(NodeModelBuilder<T> context, ElementSelector<T> parent) {
        this.context = context;
        this.parent = parent;
    }

    ElementSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, 
                    List<ElementConstraint> constraints) {
        this.context = context;
        this.parent = parent;
        this.constraints = constraints;
    }
    
    @Override
    NodeModelBuilder<T> getContext() {
        return context;
    }
    
    @Override
    ElementSelector<T> getCurrentSelector() {
        return this;
    }

    /**
     * Test whether this element satisfies the selector condition, 
     * not including any conditions imposed by constraints (which
     * are checked separately).
     * @param element
     * @return
     */
    boolean matches(StartElement element) {
        for (ElementConstraint constraint : getConstraints()) {
            if (!constraint.matches(element)) return false;
        }
        return true;
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ElementSelector)) return false;
        ElementSelector e = (ElementSelector)o;
        return constraints.equals(e.constraints);
    }

    /**
     * Attach an ElementHandler to this selector or chain of selectors.  The attached
     * handler will receive notifications for every selected element.
     * @param handler element handler
     */
    public void attach(ElementHandler<T> handler) {
    	NodeState<T> state = buildState();
    	context.addElementHandler(state, handler);
    }
    
    /**
     * Create a transition to another node state represented by its
     * {@link ElementSelector}.
     * 
     * @param localName Local name of the element on which to transition
     *                  (namespace is ignored)
     * @param target target node state to which the transition leads
     */
    public void addTransition(String localName, ElementSelector<T> target) {
        addTransition(new QName(localName), target);
    }

    /**
     * Create a transition to another node state represented by its
     * {@link ElementSelector}.
     * 
     * @param name QName of the element on which to transition
     * @param target target node state to which the transition leads
     */
    public void addTransition(QName name, ElementSelector<T> target) {
        buildState().addTransition(new ElementEqualsConstraint(name), 
                                   target.buildState());
    }
    
    /**
     * Add an explicit transition based on an explicit constraints.
     * <p>
     * <b>TODO</b> This isn't quite consistent with the way I use 
     * constraints for other things.  The normal semantics for 
     * selectors is to say child(ElementConstraint...), ie a 
     * node selector + constraints on that selection. 
     * 
     * @param constraint
     * @param target
     */
    public void addTransition(ElementConstraint constraint, ElementSelector<T> target) {
        buildState().addTransition(constraint, target.buildState());
    }
    
    NodeState<T> buildState() {
        if (nodeState == null) {
        	NodeState<T> parentState = (parent == null) ?
        			context.getModel().getRoot() : parent.buildState();
        	nodeState = addState(parentState);
        }
        return nodeState;
    }
    
    /**
     * Add a transition, with a test corresponding to this selector,
     * to specified state.
     * @param baseState state to which the transition should be added
     * @return target state for the transition
     */
    abstract NodeState<T> addState(NodeState<T> baseState);
    
    List<ElementConstraint> getConstraints() {
    	return constraints;
    }
    
    // XXX
    static List<ElementConstraint> gatherConstraints(ElementConstraint head, ElementConstraint[] rest) {
        List<ElementConstraint> constraints = new ArrayList<ElementConstraint>(1 + rest.length);
        constraints.add(head);
        constraints.addAll(Arrays.asList(rest));
        return constraints;
    }
    
    static class ElementSelectorTest<T> implements ElementConstraint {
        private ElementSelector<T> selector;
        ElementSelectorTest(ElementSelector<T> selector) {
            this.selector = selector;
        }
        
        @Override
        public boolean matches(StartElement element) {
            for (ElementConstraint constraint : selector.constraints) {
                if (!constraint.matches(element)) {
                    return false;
                }
            }
            return true;
        }
        
        protected ElementSelector<T> getSelector() {
            return selector;
        }
        
        @Override
        @SuppressWarnings("rawtypes")
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null || !(o instanceof ElementSelectorTest)) {
                return false;
            }
            return selector.equals(((ElementSelectorTest)o).selector);
        }
        
        @Override
        public String toString() {
             return "ElementSelectorTest(" + selector + ")";
        }
    }
}
