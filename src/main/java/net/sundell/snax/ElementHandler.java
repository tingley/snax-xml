package net.sundell.snax;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

/**
 * A handler that receives events about elements selected by the 
 * {@link NodeModel}.  For each selected element, attached ElementHandlers
 * will receive start and end notifications. 
 * 
 * Additionally, an ElementHandler may embed additional selectors in the 
 * model with the build() method.  
 */
public interface ElementHandler<T> {
   
    /**
     * Called when a selected element starts.
     * @param element element matching selection criteria
     * @param data parser data object
     */
    public void startElement(StartElement element, T data) throws SNAXUserException;
    
    /**
     * Called when a selected element ends.
     * @param element element matching selection criteria
     * @param data parser data object
     */
    public void endElement(EndElement element, T data) throws SNAXUserException;
    
    /**
     * Called when a selected element contains text content. 
     * @param parent selected element
     * @param characters selected element contents
     * @param data parser data object
     */
    public void characters(StartElement parent, Characters characters, T data) throws SNAXUserException;

    /**
     * Called once during the model building phase, prior to parsing.
     * The ElementHandler may optionally use this opportunity to define
     * new element selectors that will be included in the final {@link NodeModel}.
     * Any selectors defined this way will be <b>relative to this element</b>, 
     * allowing for some encapsulation and reuse of model structures.
     * 
     * @param builder model builder
     */
    public void build(NodeModelBuilder<T> builder);
    

}
