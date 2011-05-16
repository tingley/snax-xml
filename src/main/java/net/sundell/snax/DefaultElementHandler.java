package net.sundell.snax;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

/**
 * A default {@link ElementHandler} implementation where all methods are no-ops.
 * 
 * @author tingley
 */
public class DefaultElementHandler<T> implements ElementHandler<T> {

    /**
     * Called when a selected element starts.  This implementation does nothing.
     * @param element selected element
     * @param data parser data object
     */
    public void startElement(StartElement element, T data) throws SNAXUserException {
    }
    
    /**
     * Called when a selected element ends.  This implementation does nothing.
     * @param element selected element
     * @param data parser data object
     */
    public void endElement(EndElement element, T data) throws SNAXUserException {
    }
    
    /**
     * Called when a selected element contains text content.  This implementation does nothing.
     * @param parent selected element
     * @param contents selected element contents
     * @param data parser data object
     */
    public void characters(StartElement parent, Characters contents, T data) throws SNAXUserException {
    }
    
    /**
     * Called once during the model building phase, prior to parsing.
     * The ElementHandler may optionally use this opportunity to define
     * new element selectors that will be included in the final {@link NodeModel}.
     * Any selectors defined this way will be <b>relative to this element</b>, 
     * allowing for some encapsulation and reuse of model structures.
     * This implementation does nothing.
     * @param builder model builder
     */
    public void build(NodeModelBuilder<T> builder) {
    }
}
