package net.sundell.snax;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

/**
 * Constraint that bundles a QName with other constraints.
 */
class ElementEqualsConstraint implements ElementConstraint {

    private QName qname;
    
    ElementEqualsConstraint(QName qname) {
        this.qname = qname;
    }

    protected QName getQName() {
        return qname;
    }
    
    @Override
    public boolean matches(StartElement element) {
        return qname.equals(element.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof ElementEqualsConstraint)) return false;
        ElementEqualsConstraint c = (ElementEqualsConstraint)o;
        return qname.equals(c.qname);
    }
    
    @Override
    public String toString() {
        return "equals(" + qname + ")";
    }
}
