package net.sundell.snax;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

/**
 * Matches any descendant node with a given name.
 */
// TODO: refactor with ElementEqualsSelector
public class DescendantEqualsSelector<T> extends DescendantSelector<T> {

    private QName qname;
    
    DescendantEqualsSelector(NodeModelBuilder<T> context, QName qname, List<ElementConstraint> constraints) {
        super(context, constraints);
        this.qname = qname;
    }
    
    DescendantEqualsSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, QName qname, 
                            List<ElementConstraint> constraints) {
        super(context, parent, constraints);
        this.qname = qname;
    }

    @Override
    protected boolean matches(StartElement element) {
        return element.getName().equals(qname);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof DescendantEqualsSelector)) return false;
        return qname.equals(((DescendantEqualsSelector)o).qname);
    }
    
    @Override
    public String toString() {
        return "descendant-equals(" + qname + ")";
    }

}
