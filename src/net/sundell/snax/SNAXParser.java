package net.sundell.snax;

import java.io.Reader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.*;
import javax.xml.stream.events.*;

/**
 * SNAX parser that operates on top of a STaX XMLEventReader.
 */

// TODO: 
// - merge elements() into another form of element()
// - child() and descendent() 
// - note: the best practice for having embedded handlers that build 
//   additional stuff is to have them be parameterized on an interface, 
//   rather than a concrete class.  otherwise, you can't reuse the handler.
// - TODO: I need to formally parameterize a bunch of things to ensure type safety
// - TODO: What about handlers with only side effects?  What are they parameterized with?
public class SNAXParser<T> {

    private static Logger logger = Logger.getLogger(SNAXParser.class.getName());
    
    private XMLInputFactory factory;
    private NodeModel<T> model;

    /**
     * Return a new SNAXParser using the specified model.
     * @param factory XMLInputFactory to use when creating input streams
     * @param model NodeModel that defines the state machine to use when parsing
     * @return a new parser
     */
    public static <T> SNAXParser<T> createParser(XMLInputFactory factory, NodeModel<T> model) {
        return new SNAXParser<T>(factory, model);
    }
    
    private SNAXParser(XMLInputFactory factory, NodeModel<T> model) {
        this.factory = factory;
        this.model = model;
    }
    
    /**
     * Get the NodeModel used by this parser.
     * @return model
     */
    public NodeModel<T> getModel() {
        return model;
    }
    
    /**
     * Set the NodeModel used by this parser.
     * @param model the model to use
     */
    public void setModel(NodeModel<T> model) {
        this.model = model;
    }

    
    private XMLEventReader xmlReader;
    private T data;
    private Stack<NodeState<T>> stack;
    private NodeState<T> currentState;
    private Location currentLocation;
    private StartElement currentElement;
    private boolean done;

    /**
     * Begin incremental parsing of a data stream, represented by a Reader.  This will initialize
     * the parser but will not perform any parsing.  Callers should then use hasMoreEvents() and 
     * processEvent() to consume XML events until the document has been completely parsed.
     * 
     * @param reader XML content to process
     * @param data optional, user-defined object to be passed as an argument to ElementHandlers
     * @throws XMLStreamException if an error occurs during initialization
     */
    public void startParsing(Reader reader, T data) throws XMLStreamException {
        checkState(model != null, "No model was set");
        init(reader, data);
    }
    
    public boolean hasMoreEvents() {
        checkState(xmlReader != null, "startParsing() was never called");
        return xmlReader.hasNext(); 
    }
    
    public XMLEvent processEvent() throws XMLStreamException, SNAXUserException {
        checkState(xmlReader != null, "startParsing() was never called");
        return processEvent(xmlReader.nextEvent());
    }
    
    /**
     * Parse a data stream, represented by a Reader, to completion.  This will process the entire
     * document and trigger any ElementHandler calls that result from applying the selectors defined
     * in the NodeModel.  The data parameter will be passed back as an argument to all ElementHandler
     * calls.
     *  
     * @param reader XML content to process
     * @param data optional, user-defined object to be passed as an argument to ElementHandlers
     * @throws XMLStreamException if an XML error occurs
     * @throws SNAXUserException if an ElementHandler throws an exception
     */
    public void parse(Reader reader, T data) throws XMLStreamException, SNAXUserException {
        init(reader, data);
        for (XMLEvent event = xmlReader.nextEvent(); xmlReader.hasNext(); event = xmlReader.nextEvent()) {
            processEvent(event);
        }
    }
    
    private void init(Reader reader, T data) throws XMLStreamException {
        this.xmlReader = factory.createXMLEventReader(reader);
        this.data = data;
        stack = new Stack<NodeState<T>>();
        currentState = model.getRoot();
        currentLocation = null;
        currentElement = null;
        done = false;
    }
    
    private XMLEvent processEvent(XMLEvent event) throws XMLStreamException, SNAXUserException {       
        try {
            int type = event.getEventType();
            currentLocation = event.getLocation();
            switch (type) {
            case XMLEvent.START_ELEMENT:
                StartElement startEl = event.asStartElement();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("START: " + startEl.getName().getLocalPart());
                }
                checkState(!done, "Element started after end of document");
                NodeState<T> newState = currentState.follow(startEl);
                stack.push(newState);
                newState.handleElementStart(startEl, data);
                currentState = newState;
                currentElement = startEl;
                break;
            case XMLEvent.END_ELEMENT:
                NodeState<T> ended = stack.pop();
                EndElement endEl = event.asEndElement();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("END: " + endEl.getName().getLocalPart());
                }
                ended.handleElementEnd(endEl, data);
                if (stack.empty()) {
                    // End of document!
                    this.done = true;
                }
                else {
                    currentState = stack.peek();
                }
                break;
            case XMLEvent.CHARACTERS:
                currentState.handleContents(currentElement, 
                                            event.asCharacters(), data);
                break;
            case XMLEvent.DTD:
                model.handleDTD((DTD)event, data);
                break;
            case XMLEvent.ENTITY_DECLARATION:
                model.handleEntityDeclaration((EntityDeclaration)event, data);
                break;
            case XMLEvent.ENTITY_REFERENCE:
                model.handleEntityReference((EntityReference)event, data);
                break;
            case XMLEvent.NOTATION_DECLARATION:
                model.handleNotationDeclaration((NotationDeclaration)event, data);
                break;
            }
            return event;
        }
        catch (SNAXUserException e) {
            e.setLocation(currentLocation);
            throw e;
        }
    }

    private void checkState(boolean test, String message) {
        if (!test) {
            throw new IllegalStateException(message);
        }
    }

}
