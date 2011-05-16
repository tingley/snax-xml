package net.sundell.snax;

import javax.xml.namespace.QName;

/**
 * An attribute value test.  This can be used to filter attributes by
 * value using an {@link AttributeMatcher}.  For example, to select
 * <code>foo</foo> elements with a numeric ID, you might do:
 * 
 * <pre>  element("foo", with("id").filter(new AttributeFilter() {
 *     public boolean matches(QName attrName, String attributeValue) {
 *         return Pattern.matches("\\d+", attributeValue);
 *     }
 *   }));</pre>
 * @see AttributeMatcher#filter(AttributeFilter)
 */
public interface AttributeFilter {
    boolean matches(QName attrName, String attributeValue);
}
