package net.sundell.snax;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

/**
 * Constraint that bundles a QName with other constraints.
 */
class ElementEqualsConstraint implements ElementConstraint {

    private QName qname;
    private List<ElementConstraint> constraints = new ArrayList<ElementConstraint>();
    
    ElementEqualsConstraint(QName qname) {
        this.qname = qname;
    }

    ElementEqualsConstraint(QName qname, List<ElementConstraint> constraints) {
        this.qname = qname;
        this.constraints = constraints;
    }

    protected QName getQName() {
        return qname;
    }
    
    @Override
    public boolean matches(StartElement element) {
        if (!qname.equals(element.getName())) {
            return false;
        }
        for (ElementConstraint constraint : constraints) {
            if (!constraint.matches(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof ElementEqualsConstraint)) return false;
        ElementEqualsConstraint c = (ElementEqualsConstraint)o;
        return qname.equals(c.qname) &&
               constraints.equals(c.constraints);
    }
    
    // TODO - include constraints
    @Override
    public String toString() {
        return "equals(" + qname + ")";
    }
}
