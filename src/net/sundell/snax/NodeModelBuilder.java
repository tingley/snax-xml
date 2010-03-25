package net.sundell.snax;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.namespace.QName;

public class NodeModelBuilder<T> {

    private NodeModel<T> model;

    public NodeModelBuilder() {
        this.model = new NodeModel<T>();
    }
    
    NodeModelBuilder(NodeModel<T> model) {
        this.model = model;
    }
    
    /**
     * Select an element with a specified QName. 
     * @param name element qname to match
     * @param constraints additional constraints, if any
     * @return element selector
     */
    public final ElementSelector<T> element(QName qname, ElementConstraint...constraints) {
        return new ElementEqualsSelector<T>(this, qname, Arrays.asList(constraints));
    }

    /**
     * Equivalent to element(new QName("name")). 
     * @param localName element local name to match
     * @param constraints additional constraints, if any
     * @return
     */
    public final ElementSelector<T> element(String localName, ElementConstraint...constraints) {
        return element(new QName(localName), constraints);
    }

    /**
     * Syntactic sugar to allow quick construction of a chain of simple element selectors.
     * elements(name1, name2, name3) is equivalent to element(name1).element(name2).element(name3).
     * @param names element names 
     * @return last element selector in the chain
     */
    public final ElementSelector<T> elements(QName...names) {
        ElementSelector<T> parent = null;
        for (QName name : Arrays.asList(names)) {
            parent = new ElementEqualsSelector<T>(this, parent, name);
        }
        return parent;
    }

    /**
     * Equivalent to elements(new QName("name1"), new QName("name2"), ...).
     * @param localNames element local names 
     * @return last element selector in the chain
     */
    public final ElementSelector<T> elements(String...localNames) {
        ElementSelector<T> parent = null;
        for (String name : Arrays.asList(localNames)) {
            parent = new ElementEqualsSelector<T>(this, parent, new QName(name));
        }
        return parent;
    }
    
    /**
     * Create a selector that invokes an arbitrary filter to determine whether to
     * accept elements.
     * @param filter ElementFilter to test elements
     * @return element selector
     */
    public final ElementSelector<T> element(ElementFilter filter) {
        return new ElementFilterSelector<T>(this, filter);
    }
    
    /**
     * Selector that matches any child element that satisfies the specified constraints.  If no
     * constraints are provided, accepts all child elements.
     * @param constraints element constraints
     * @return child element selector
     */
    public final ElementSelector<T> child(ElementConstraint...constraints) {
        return new ChildSelector<T>(this, Arrays.asList(constraints));
    }
    
    /**
     * Selector that matches any descendant element that satisfies the specified constraints.  If no
     * constraints are provided, accepts all descendant elements.
     * @param constraints element constraints
     * @return child element selector
     */
    public final ElementSelector<T> descendant(ElementConstraint...constraints) {
        return new DescendantSelector<T>(this, Arrays.asList(constraints));
    }
    
    /**
     * Used to match an attribute as part when adding a constraint to an ElementSelector.
     * The returned AttributeMatcher instance should be used to specify the constraint, as in
     *     child(with(attrName).equalTo("attrValue"))
     * @param attributeName QName of the desired attribute
     * @return AttributeMatcher for use in constraint construction
     */
    public final AttributeMatcher with(QName attributeQName) {
        return new AttributeMatcher(attributeQName);
    }

    /**
     * Equivalent to with(new QName(attributeLocalName)).
     * @param attributeLocalName attribute local name
     * @return AttributeMatcher for use in constraint construction
     */
    public final AttributeMatcher with(String attributeLocalName) {
        return new AttributeMatcher(new QName(attributeLocalName));
    }

    public final void attachDTDHandler(DeclarationHandler<T> handler) {
        model.addDTDHandler(handler);
    }
    
    public final NodeModel<T> build() {
     	for (Map.Entry<NodeState<T>, List<ElementHandler<T>>> e : statesWithHandlers.entrySet()) {
    	    NodeState<T> state = e.getKey();
    	    // Run a sub-builder rooted here for each handler
    	    // Inject into model
    	    NodeModel<T> subModel = new NodeModel<T>(state);
    	    NodeModelBuilder<T> subBuilder = new NodeModelBuilder<T>(subModel);
    	    for (ElementHandler<T> handler : e.getValue()) {
    	        handler.build(subBuilder);
    	    }
    	    // A recursive call is necessary to pick up layers of nesting
    	    // beyond the first
    	    subBuilder.build();
    	}
        
    	return model;
    }
    
    // Would like to use google-collect MultiMap here...
    private Map<NodeState<T>, List<ElementHandler<T>>> statesWithHandlers = 
        new HashMap<NodeState<T>, List<ElementHandler<T>>>();
    
    void addElementHandler(NodeState<T> state, ElementHandler<T> handler) {
        state.addElementHandler(handler);
        List<ElementHandler<T>> handlers = statesWithHandlers.get(state);
        if (handlers == null) {
            handlers = new ArrayList<ElementHandler<T>>();
            statesWithHandlers.put(state, handlers);
        }
        handlers.add(handler);
    }
    
    NodeModel<T> getModel() {
    	return model;
    }
}
