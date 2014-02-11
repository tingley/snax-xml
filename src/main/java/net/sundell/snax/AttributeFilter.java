package net.sundell.snax;

import javax.xml.namespace.QName;

/**
 * An attribute value test.  This can be used to filter attributes by
 * value using an {@link AttributeMatcher}.  For example, to select
 * <code>foo</code> elements with a numeric ID, you might do:
 * 
 * <pre>  element("foo", with("id").filter(new AttributeFilter() {
 *     public boolean matches(QName attrName, String attributeValue) {
 *         return Pattern.matches("\\d+", attributeValue);
 *     }
 *   }));</pre>
 * @see AttributeMatcher#filter(AttributeFilter)
 */
public interface AttributeFilter {
    /**
     * Return true if the attribute name and value match the 
     * criteria selected by this filter.
     * @param attrName attribute name
     * @param attributeValue attribute value
     * @return true if the filter matches this attribute
     */
    boolean matches(QName attrName, String attributeValue);
}
