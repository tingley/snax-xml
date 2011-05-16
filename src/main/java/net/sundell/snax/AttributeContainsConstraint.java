package net.sundell.snax;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

class AttributeContainsConstraint implements ElementConstraint {
    private QName qname;
    private String containsValue;
    private boolean shouldMatch;

    AttributeContainsConstraint(QName qname, String containsValue, boolean shouldMatch) {
        this.qname = qname;
        this.containsValue = containsValue;
        this.shouldMatch = shouldMatch;
    }

    @Override
    public boolean matches(StartElement element) {
        Attribute attr = element.getAttributeByName(qname);
        if (attr != null && attr.getValue().indexOf(containsValue) != -1) {
            return shouldMatch;
        }
        return !shouldMatch;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof AttributeContainsConstraint)) {
            return false;
        }
        AttributeContainsConstraint c = (AttributeContainsConstraint)o;
        return qname.equals(c.qname) && 
               containsValue.equals(c.containsValue) &&
               shouldMatch == c.shouldMatch;
    }

}
