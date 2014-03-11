package net.sundell.snax;

import java.util.Arrays;

import javax.xml.namespace.QName;

/**
 * Base class for objects from which element selectors can be built. 
 */
public abstract class Selectable<T> {
    
    abstract NodeModelBuilder<T> getContext();
    
    abstract ElementSelector<T> getCurrentSelector();
    
    /**
     * Select an element with a specified <code>QName</code>. 
     * @param name element qname to match
     * @param constraints additional constraints, if any
     * @return element selector
     */
    public final ChildSelector<T> element(QName qname, ElementConstraint...constraints) {
        ElementEqualsConstraint nameConstraint = new ElementEqualsConstraint(qname);
        return new ChildSelector<T>(getContext(), getCurrentSelector(), 
                ElementSelector.gatherConstraints(nameConstraint, constraints));
    }

    /**
     * Equivalent to <code>element(new QName("name"))</code>. 
     * @param localName element local name to match
     * @param constraints additional constraints, if any
     * @return
     */
    public final ChildSelector<T> element(String localName, ElementConstraint...constraints) {
        return element(new QName(localName), constraints);
    }

    /**
     * Syntactic sugar to allow quick construction of a chain of simple element selectors.
     * <code>elements(name1, name2, name3)</code> is equivalent to 
     * <code>element(name1).element(name2).element(name3)</code>.
     * @param names element names 
     * @return last element selector in the chain
     */
    public final ChildSelector<T> elements(QName...names) {
        ElementSelector<T> parent = getCurrentSelector();
        ChildSelector<T> newSelector = null;
        for (QName name : Arrays.asList(names)) {
            newSelector = new ChildSelector<T>(getContext(), parent, 
                    (ElementConstraint)new ElementEqualsConstraint(name));
            parent = newSelector;
        }
        return newSelector;
    }

    /**
     * Equivalent to <code>elements(new QName("name1"), new QName("name2"), ...)</code>.
     * @param localNames element local names 
     * @return last element selector in the chain
     */
    public final ChildSelector<T> elements(String...localNames) {
        ElementSelector<T> parent = getCurrentSelector();
        ChildSelector<T> newSelector = null;
        for (String name : Arrays.asList(localNames)) {
            newSelector = new ChildSelector<T>(getContext(), parent, 
                    (ElementConstraint)new ElementEqualsConstraint(new QName(name)));
            parent = newSelector;
        }
        return newSelector;
    }
    
    /**
     * Selector that matches any child element that satisfies the specified constraints.  If no
     * constraints are provided, accepts all child elements.
     * @param constraints element constraints
     * @return element selector
     */
    public final ChildSelector<T> child(ElementConstraint...constraints) {
        return new ChildSelector<T>(getContext(), getCurrentSelector(),
                Arrays.asList(constraints));
    }
    
    /**
     * Selector that matches any descendant element that satisfies the specified constraints.  If no
     * constraints are provided, accepts all descendant elements.
     * @param constraints element constraints
     * @return element selector
     */
    public final ElementSelector<T> descendant(ElementConstraint...constraints) {
        return new DescendantSelector<T>(getContext(), getCurrentSelector(), 
                Arrays.asList(constraints));
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
        return new DescendantSelector<T>(getContext(), getCurrentSelector(), 
                ElementSelector.gatherConstraints(nameConstraint, constraints));
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
}
