package net.sundell.snax;

import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

/**
 * Base element selector class.
 * 
 * @param <T>
 */
public abstract class ElementSelector<T> {

    private NodeModelBuilder<T> context;
    private List<ElementConstraint> constraints = Collections.emptyList();
    private ElementSelector<T> parent = null;

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
     * Select an element with a specified QName. 
     * @param name element qname to match
     * @param constraints additional constraints, if any
     * @return element selector
     */
    public ElementSelector<T> element(QName qname, ElementConstraint...constraints) {
        ElementEqualsConstraint nameConstraint = new ElementEqualsConstraint(qname);
        return new ChildSelector<T>(context, this, gatherConstraints(nameConstraint, constraints));
    }

    /**
     * Equivalent to element(new QName("name")). 
     * @param localName element local name to match
     * @param constraints additional constraints, if any
     * @return
     */
    public ElementSelector<T> element(String name, ElementConstraint...constraints) {
        return element(new QName(name), constraints);
    }

    /**
     * Syntactic sugar to allow quick construction of a chain of simple element selectors.
     * elements(name1, name2, name3) is equivalent to element(name1).element(name2).element(name3).
     * @param names element names 
     * @return last element selector in the chain
     */
    public ElementSelector<T> elements(QName...qnames) {
        ElementSelector<T> parent = this;
        for (QName qname : Arrays.asList(qnames)) {
            parent = new ChildSelector<T>(context, parent, 
                    (ElementConstraint)new ElementEqualsConstraint(qname));
        }
        return parent;
    }

    /**
     * Equivalent to elements(new QName("name1"), new QName("name2"), ...).
     * @param localNames element local names 
     * @return last element selector in the chain
     */
    public ElementSelector<T> elements(String...localNames) {
        ElementSelector<T> parent = this;
        for (String localName : Arrays.asList(localNames)) {
            parent = new ChildSelector<T>(context, parent, 
                    (ElementConstraint)new ElementEqualsConstraint(new QName(localName)));
        }
        return parent;
    }

    /**
     * Selector that matches any child element that satisfies the specified constraints.  If no
     * constraints are provided, accepts all child elements.
     * @param constraints element constraints
     * @return element selector
     */
    public ElementSelector<T> child(ElementConstraint...constraints) {
        return new ChildSelector<T>(context, this, Arrays.asList(constraints));
    }
    
    /**
     * Selector that matches any descendant element that satisfies the specified constraints.  If no
     * constraints are provided, accepts all descendant elements.
     * @param constraints element constraints
     * @return element selector
     */
    public ElementSelector<T> descendant(ElementConstraint...constraints) {
        return new DescendantSelector<T>(context, this, Arrays.asList(constraints));
    }
    
    /**
     * Selector that matches any descendant element with a given name that satisfies the 
     * specified constraints.  If no constraints are provided, accepts all descendant elements
     * with the given name.
     * @param qname element QName
     * @param constraints element constraints
     * @return element selector
     */
    public final ElementSelector<T> descendant(QName qname, ElementConstraint...constraints) {
        ElementEqualsConstraint nameConstraint = new ElementEqualsConstraint(qname);
        return new DescendantSelector<T>(context, this, gatherConstraints(nameConstraint, constraints));
    }
    
    /**
     * Equivalent to <code>descendant(new QName("name1"), ...)</code>.
     * @param localName element name (not namespace-qualified)
     * @param constraints element constraints
     * @return element selector
     */
    public final ElementSelector<T> descendant(String localName, ElementConstraint...constraints) {
        return descendant(new QName(localName), constraints);
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
    
    public AttachPoint<T> attachPoint() {
        return new AttachPoint<T>(buildState());
    }
    
    public void addTransition(String localName, AttachPoint<T> target) {
        addTransition(new QName(localName), target);
    }

    public void addTransition(QName name, AttachPoint<T> target) {
        buildState().addTransition(new ElementEqualsConstraint(name), 
                                   target.getNodeState());
    }
    
    /**
     * Add an explicit transition based on all explicit constraints.
     * <b>TODO</b> This isn't quite consistent with the way I use 
     * constraints for other things.  The normal semantics for 
     * selectors is to say child(ElementConstraint...), ie a 
     * node selector + constraints on that selection. 
     * 
     * @param constraint
     * @param target
     */
    public void addTransition(ElementConstraint constraint, AttachPoint<T> target) {
        buildState().addTransition(constraint, target.getNodeState());
    }
    
    NodeState<T> buildState() {
    	NodeState<T> parentState = (parent == null) ?
    			context.getModel().getRoot() : parent.buildState();
    	return addState(parentState);
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
