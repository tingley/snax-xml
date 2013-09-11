package net.sundell.snax;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

/**
 * Note that NodeStates are only equals() if they are the same object.  As a result, 
 * Object.equals() and Object.hashCode() are not overridden by this class.
 */
class NodeState <T>{
    private List<NodeTransition<T>> transitions = new ArrayList<NodeTransition<T>>();
    private List<ElementHandler<T>> elementHandlers = 
                            new ArrayList<ElementHandler<T>>();
    private List<NodeTransition<T>> descendantRules = new ArrayList<NodeTransition<T>>();
    
    public NodeState() { }

    protected NodeState(List<NodeTransition<T>> transitions, List<ElementHandler<T>> elementHandlers,
                        List<NodeTransition<T>> descendantRules) {
        this.transitions = transitions;
        this.elementHandlers = elementHandlers;
        this.descendantRules = descendantRules;
    }
    
    protected List<ElementHandler<T>> getHandlers() {
        return elementHandlers;
    }
    
    protected List<NodeTransition<T>> getTransitions() {
        return transitions;
    }
    
    /**
     * Immutable empty state returned when nodes can not transition anywhere else.
     */
    @SuppressWarnings("unchecked")
    static NodeState EMPTY_STATE = createEmptyState();

    @SuppressWarnings("unchecked")
    private static NodeState createEmptyState() {
        List<NodeTransition> transitions = Collections.emptyList();
        List<ElementHandler> elementHandlers = Collections.emptyList();
        List<ElementHandler> descendantRules = Collections.emptyList();
        return new NodeState(transitions, elementHandlers, descendantRules);
    }
    
    @SuppressWarnings("unchecked")
    private NodeState<T> emptyState() {
        return EMPTY_STATE;
    }

    /**
     * Add a transition to another node state based on the specified test.  Only
     * one transition is allowed for a given test, so if a duplicate test is 
     * added, the existing target node will be returned.
     * @param test
     * @param targetState
     * @return node that the transition leads to
     */
    NodeState<T> addTransition(ElementConstraint test, NodeState<T> targetState) {
    	assert (test != null);
    	assert (targetState != null);
        for (NodeTransition<T> transition : transitions) {
            if (transition.getTest().equals(test)) {
                return transition.getTarget();
            }
        }
        transitions.add(new NodeTransition<T>(test, targetState));
        //System.out.println("Added " + this + " --" + test + "-->" + targetState);
        return targetState;
    }
    
    void addElementHandler(ElementHandler<T> handler) {
        this.elementHandlers.add(handler);
    }

    List<NodeTransition<T>> getDescendantRules() {
        return descendantRules;
    }
    
    NodeState<T> addDescendantRule(ElementSelector<T> selector) {
        // TODO: refactor this use of new
        ElementConstraint test = new ElementSelector.ElementSelectorTest<T>(selector);
        for (NodeTransition<T> rule : descendantRules) {
            if (test.equals(rule.getTest())) {
                return rule.getTarget();
            }
        }
        NodeState<T> target = new NodeState<T>();
        descendantRules.add(new NodeTransition<T>(test, target));
        return target;
    }
    
    /**
     * Attempt to process a node event and transition to a new state.  If no suitable
     * transition is found, the empty state is returned.  
     * @param element
     * @return the new state, or the empty state
     */  
    NodeState<T> follow(StartElement element) {
        assert (element != null);
        for (NodeTransition<T> transition : transitions) {
            if (transition.getTest().matches(element)) {
                return transition.getTarget();
            }
        }
        return emptyState();
    }
    
    void handleElementStart(StartElement element, T data) throws SNAXUserException {
        for (ElementHandler<T> e : elementHandlers) {
            e.startElement(element, data);
        }
    }
    
    void handleContents(StartElement parent, Characters contents, T data) throws SNAXUserException {
        for (ElementHandler<T> e : elementHandlers) {
            e.characters(parent, contents, data);
        }
    }
    
    void handleElementEnd(EndElement element, T data) throws SNAXUserException {
        for (ElementHandler<T> e : elementHandlers) {
            e.endElement(element, data);
        }
    }
    
    @Override
    public String toString() {
    	return "NodeState[" + transitions.size() + "]";
    }

}
