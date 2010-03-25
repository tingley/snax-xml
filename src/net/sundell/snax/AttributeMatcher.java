package net.sundell.snax;

import javax.xml.namespace.QName;

public class AttributeMatcher {

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

}
