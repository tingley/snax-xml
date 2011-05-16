package net.sundell.snax;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

class AttributeMatchesConstraint implements ElementConstraint {

    private QName qname;
    private Pattern pattern;
    private boolean shouldMatch;

    AttributeMatchesConstraint(QName qname, String pattern, boolean shouldMatch) {
        this.qname = qname;
        this.pattern = Pattern.compile(pattern);
        this.shouldMatch = shouldMatch;
    }

    @Override
    public boolean matches(StartElement element) {
        Attribute attr = element.getAttributeByName(qname);
        if (attr != null) {
            Matcher m = pattern.matcher(attr.getValue());
            if (m.matches()) {
                return shouldMatch;
            }
        }
        return !shouldMatch;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof AttributeMatchesConstraint)) {
            return false;
        }
        AttributeMatchesConstraint c = (AttributeMatchesConstraint)o;
        return qname.equals(c.qname) && 
               pattern.equals(c.pattern) &&
               shouldMatch == c.shouldMatch;
    }
}
