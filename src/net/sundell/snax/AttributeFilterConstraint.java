package net.sundell.snax;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

class AttributeFilterConstraint implements ElementConstraint {
    private QName qname;
    private AttributeFilter filter;
    
    AttributeFilterConstraint(QName qname, AttributeFilter filter) {
        this.qname = qname;
        this.filter = filter;
    }
    
    @Override
    public boolean matches(StartElement element) {
        Attribute attr = element.getAttributeByName(qname);
        return (attr != null && filter.matches(qname, attr.getValue()));
   }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof AttributeFilterConstraint)) {
            return false;
        }
        AttributeFilterConstraint c = (AttributeFilterConstraint)o;
        return qname.equals(c.qname) && 
               filter.equals(c.filter);
    }

}
