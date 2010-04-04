package net.sundell.snax;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

/**
 * An {@link ElementConstraint} that specifies that an element has a particular
 * attribute and allows for various filters to be applied to that attribute's
 * value.
 * 
 * 
 */
public class AttributeMatcher implements ElementConstraint {

    private QName attributeName;

    public AttributeMatcher(QName attributeName) {
        this.attributeName = attributeName;
    }
    
    public ElementConstraint equalTo(String attributeValue) {
        return new AttributeEqualsConstraint(attributeName, attributeValue, true);
    }
    
    public ElementConstraint notEqualTo(String attributeValue) {
        return new AttributeEqualsConstraint(attributeName, attributeValue, false);
    }

    public ElementConstraint contains(String value) {
        return new AttributeContainsConstraint(attributeName, value, true);
    }
    
    public ElementConstraint doesNotContain(String value) {
        return new AttributeContainsConstraint(attributeName, value, false);
    }

    public ElementConstraint matches(String pattern) {
        return new AttributeMatchesConstraint(attributeName, pattern, true);
    }
    
    public ElementConstraint doesNotMatch(String pattern) {
        return new AttributeMatchesConstraint(attributeName, pattern, false);
    }

    public ElementConstraint filter(AttributeFilter filter) {
        return new AttributeFilterConstraint(attributeName, filter);
    }
    
    @Override
    public boolean matches(StartElement element) {
        return (element.getAttributeByName(attributeName) != null);
    }

}
