package net.sundell.snax;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * A <code>NodeModelBuilder</code> is used to construct a <code>NodeModel</code> which is then
 * usable for parsing.  This is done by creating chains of <code>ElementSelector</code> instances,
 * to which <code>ElementHandlers</code> are attached.  A simple use of <code>NodeModelBuilder</code>
 * could look like this: 
 * <pre>  NodeModel&lt;MyData&gt; model = new NodeModelBuilder&lt;MyData&gt;() {{
 *    descendant(with("id")).attach(new IdHandler());
 *  }}.build()</pre>
 *
 * This selects all elements in the document with the <code>id</code> attribute and
 * send events about those elements to an instance of an <code>ElementHandler</code>
 * called <code>IdHandler</code>. 
 *
 * @param <T> Data object type that will be passed to parse calls
 * @see NodeModel
 * @see SNAXParser
 */
public class NodeModelBuilder<T> {

    private NodeModel<T> model;

    public NodeModelBuilder() {
        this.model = new NodeModel<T>();
    }
    
    NodeModelBuilder(NodeModel<T> model) {
        this.model = model;
    }
    
    /**
     * Select an element with a specified <code>QName</code>. 
     * @param name element qname to match
     * @param constraints additional constraints, if any
     * @return element selector
     */
    public final ElementSelector<T> element(QName qname, ElementConstraint...constraints) {
        return new ChildSelector<T>(this, null,
                new ElementEqualsConstraint(qname, Arrays.asList(constraints)));
    }

    /**
     * Equivalent to <code>element(new QName("name"))</code>. 
     * @param localName element local name to match
     * @param constraints additional constraints, if any
     * @return
     */
    public final ElementSelector<T> element(String localName, ElementConstraint...constraints) {
        return element(new QName(localName), constraints);
    }

    /**
     * Syntactic sugar to allow quick construction of a chain of simple element selectors.
     * <code>elements(name1, name2, name3)</code> is equivalent to 
     * <code>element(name1).element(name2).element(name3)</code>.
     * @param names element names 
     * @return last element selector in the chain
     */
    public final ElementSelector<T> elements(QName...names) {
        ElementSelector<T> parent = null;
        for (QName name : Arrays.asList(names)) {
            parent = new ChildSelector<T>(this, parent, 
                    (ElementConstraint)new ElementEqualsConstraint(name));
        }
        return parent;
    }

    /**
     * Equivalent to <code>elements(new QName("name1"), new QName("name2"), ...)</code>.
     * @param localNames element local names 
     * @return last element selector in the chain
     */
    public final ElementSelector<T> elements(String...localNames) {
        ElementSelector<T> parent = null;
        for (String name : Arrays.asList(localNames)) {
            parent = new ChildSelector<T>(this, parent, 
                    (ElementConstraint)new ElementEqualsConstraint(new QName(name)));
        }
        return parent;
    }
    
    /**
     * Selector that matches any child element that satisfies the specified constraints.  If no
     * constraints are provided, accepts all child elements.
     * @param constraints element constraints
     * @return element selector
     */
    public final ElementSelector<T> child(ElementConstraint...constraints) {
        return new ChildSelector<T>(this, null, Arrays.asList(constraints));
    }
    
    /**
     * Selector that matches any descendant element that satisfies the specified constraints.  If no
     * constraints are provided, accepts all descendant elements.
     * @param constraints element constraints
     * @return element selector
     */
    public final ElementSelector<T> descendant(ElementConstraint...constraints) {
        return new DescendantSelector<T>(this, null, Arrays.asList(constraints));
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
        return new DescendantSelector<T>(this, null,
                new ElementEqualsConstraint(qname, Arrays.asList(constraints)));
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
     * Used to match an attribute when adding a constraint to an <code>ElementSelector</code>.
     * The returned <code>AttributeMatcher</code> instance can be used to specify the constraint, as in
     * <pre>  child(with(attrName).equalTo("attrValue"))</pre>
     * which will match any child node with the specified attribute that has a value of "attrValue".
     * <p>    
     * It may also be used as a constraint on its own, as in
     * <pre>  child(with(attrName))</pre>
     * which will match any child node with the specified attribute.
     * 
     * @param attributeName QName of the desired attribute
     * @return AttributeMatcher for use in constraint construction
     */
    public final AttributeMatcher with(QName attributeQName) {
        return new AttributeMatcher(attributeQName);
    }

    /**
     * Equivalent to <code>with(new QName(attributeLocalName))</code>.
     * @param attributeLocalName attribute local name
     * @return AttributeMatcher for use in constraint construction
     */
    public final AttributeMatcher with(String attributeLocalName) {
        return new AttributeMatcher(new QName(attributeLocalName));
    }

    /**
     * Attach a {@link DeclarationHandler}.
     * @param handler <code>DeclarationHandler</code>
     */
    public final void attachDeclarationHandler(DeclarationHandler<T> handler) {
        model.addDeclarationHandler(handler);
    }
    
    /**
     * Generate a {@link NodeModel} for use in parsing, based on the selectors
     * and handlers attached to this builder.
     * <p>
     * This will also trigger a cascade <code>build()</code> calls on any attached 
     * <code>ElementHandler</code> instances.
     * 
     * @return <code>NodeModel</code> for parsing
     */
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
