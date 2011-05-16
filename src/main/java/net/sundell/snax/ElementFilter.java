package net.sundell.snax;

import javax.xml.stream.events.StartElement;

/**
 * A filter that can be used to select arbitrary elements
 * during parsing.  When used with an {@link ElementSelector},
 * the filter will be called for each possible element in the
 * selected location.
 */
public interface ElementFilter {
    /**
     * The filter should implement this method to select elements
     * that match its criteria.
     * @param element element that is being considered
     * @return true if the element should be selected by the parser
     */
    public boolean test(StartElement element);
}
