package net.sundell.snax;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

class AttributeEqualsConstraint implements ElementConstraint {

    private String attributeValue;
    private boolean shouldMatch;
    private QName qname;
    
    AttributeEqualsConstraint(QName qname, String attributeValue, boolean shouldMatch) {
        this.qname = qname;
        this.attributeValue = attributeValue;
        this.shouldMatch = shouldMatch;
    }

    @Override
    public boolean matches(StartElement element) {
        Attribute attr = element.getAttributeByName(qname);
        if (attr != null && attr.getValue().equals(attributeValue)) {
            return shouldMatch;
        }
        return !shouldMatch;
    }
    
    @Override
    public boolean equals(Object o) {
    	if (o == this) {
    		return true;
    	}
    	if (o == null || !(o instanceof AttributeEqualsConstraint)) {
    		return false;
    	}
    	AttributeEqualsConstraint c = (AttributeEqualsConstraint)o;
    	return qname.equals(c.qname) && 
    		   attributeValue.equals(c.attributeValue) &&
    		   shouldMatch == c.shouldMatch;
    }
}
