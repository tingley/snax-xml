package net.sundell.snax;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.Location;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.NotationDeclaration;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Runs a series of XML events through a node model, tracking internal state.
 */
class NodeModelExecutor<T> {
    private static Logger logger = Logger.getLogger(NodeModelExecutor.class.getName());

    private T data;
    private Stack<ParseState> stack;
    private ParseState currentState;
    private Location currentLocation;
    private Map<NodeState<T>, Integer> onlyCounts = new HashMap<NodeState<T>, Integer>();
    private boolean done;
    private NodeModel<T> model;

    NodeModelExecutor(NodeModel<T> model, T data) {
        this.model = model;
        this.data = data;
        stack = new Stack<ParseState>();
        onlyCounts.clear();
        currentState = getParseState(model.getRoot(), model.getRoot().getDescendantRules(), null);
        currentLocation = null;
        done = false;
    }

    private ParseState getParseState(NodeState<T> nodeState,
            Iterable<NodeTransition<T>> deferredRules, StartElement element) {
        if (nodeState.getOnlyValue() != NodeState.NO_ONLY_LIMIT) {
            Integer only = onlyCounts.get(nodeState);
            if (only == null) {
                only = nodeState.getOnlyValue();
            }
            if (only-- <= 0) {
                throw new SNAXUserException("Element " + element + " exceeded 'only' value");
            }
            onlyCounts.put(nodeState, only);
        }
        return new ParseState(nodeState, deferredRules, element);
    }

    XMLEvent processEvent(XMLEvent event) throws SNAXUserException {
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

                NodeState<T> nextState = currentState.nodeState.follow(startEl);
                if (nextState.equals(NodeState.EMPTY_STATE)) {
                    // Look for a match among the inherited descendant rules
                    for (NodeTransition<T> rule : currentState.deferredRules) {
                        if (rule.getTest().matches(startEl)) {
                            nextState = rule.getTarget();
                            break;
                        }
                    }
                }
                ParseState newState = getParseState(nextState,
                        CompoundIterable.prepend(nextState.getDescendantRules(),
                                                 currentState.deferredRules),
                        startEl);
                stack.push(newState);
                newState.nodeState.handleElementStart(startEl, data);
                currentState = newState;
                break;
            case XMLEvent.END_ELEMENT:
                ParseState ended = stack.pop();
                EndElement endEl = event.asEndElement();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("END: " + endEl.getName().getLocalPart());
                }
                ended.nodeState.handleElementEnd(endEl, data);
                if (stack.empty()) {
                    // End of document!
                    this.done = true;
                }
                else {
                    currentState = stack.peek();
                }
                break;
            case XMLEvent.CHARACTERS:
                currentState.nodeState.handleContents(currentState.element,
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
        // Anything that was a runtime exception we re-throw unaltered
        catch (RuntimeException e) {
            throw e;
        }
        // Checked exceptions get wrapped
        catch (Exception e) {
            SNAXUserException se = new SNAXUserException(e);
            se.setLocation(currentLocation);
            throw se;
        }
    }

    /**
     * Holder for runtime state.
     */
    class ParseState {
        NodeState<T> nodeState;
        Iterable<NodeTransition<T>> deferredRules;
        StartElement element;
        int onlyLimit;

        ParseState(NodeState<T> nodeState, Iterable<NodeTransition<T>> deferredRules,
                   StartElement element) {
            this.nodeState = nodeState;
            this.onlyLimit = nodeState.getOnlyValue();
            this.deferredRules = deferredRules;
            this.element = element;
        }
    }

    private void checkState(boolean test, String message) {
        if (!test) {
            throw new IllegalStateException(message);
        }
    }
}
