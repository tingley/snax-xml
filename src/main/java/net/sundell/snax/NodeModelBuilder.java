package net.sundell.snax;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * A <code>NodeModelBuilder</code> is used to construct a <code>NodeModel</code> which is then
 * usable for parsing.  This is done by creating chains of <code>ElementSelector</code> instances,
 * to which <code>ElementHandlers</code> are attached.  A simple use of <code>NodeModelBuilder</code>
 * could look like this: 
 * <pre>  NodeModel&lt;MyData&gt; model = new NodeModelBuilder&lt;MyData&gt;() {{
 *    descendant(with("id")).attach(new IdHandler());
 *  }}.build()</pre>
 *
 * This selects all elements in the document with the <code>id</code> attribute and
 * send events about those elements to an instance of an <code>ElementHandler</code>
 * called <code>IdHandler</code>. 
 *
 * @param <T> Data object type that will be passed to parse calls
 * @see NodeModel
 * @see SNAXParser
 */
public class NodeModelBuilder<T> extends Selectable<T> {

    private NodeModel<T> model;

    public NodeModelBuilder() {
        this.model = new NodeModel<T>();
    }
    
    NodeModelBuilder(NodeModel<T> model) {
        this.model = model;
    }
    
    @Override
    NodeModelBuilder<T> getContext() {
        return this;
    }
    
    @Override
    ElementSelector<T> getCurrentSelector() {
        return null;
    }

    /**
     * Attach a {@link DeclarationHandler}.
     * @param handler <code>DeclarationHandler</code>
     */
    public final void attachDeclarationHandler(DeclarationHandler<T> handler) {
        model.addDeclarationHandler(handler);
    }
    
    /**
     * Generate a {@link NodeModel} for use in parsing, based on the selectors
     * and handlers attached to this builder.
     * <p>
     * This will also trigger a cascade <code>build()</code> calls on any attached 
     * <code>ElementHandler</code> instances.
     * 
     * @return <code>NodeModel</code> for parsing
     */
    public final NodeModel<T> build() {
     	for (Map.Entry<NodeState<T>, List<ElementHandler<T>>> e : statesWithHandlers.entrySet()) {
    	    NodeState<T> state = e.getKey();
    	    // Run a sub-builder rooted here for each handler
    	    // Inject into model
    	    NodeModel<T> subModel = new NodeModel<T>(state);
    	    NodeModelBuilder<T> subBuilder = new NodeModelBuilder<T>(subModel);
    	    for (ElementHandler<T> handler : e.getValue()) {
    	        handler.build(subBuilder);
    	    }
    	    // A recursive call is necessary to pick up layers of nesting
    	    // beyond the first
    	    subBuilder.build();
    	}
        
    	return model;
    }
    
    // Would like to use google-collect MultiMap here...
    private Map<NodeState<T>, List<ElementHandler<T>>> statesWithHandlers = 
        new HashMap<NodeState<T>, List<ElementHandler<T>>>();
    
    void addElementHandler(NodeState<T> state, ElementHandler<T> handler) {
        state.addElementHandler(handler);
        List<ElementHandler<T>> handlers = statesWithHandlers.get(state);
        if (handlers == null) {
            handlers = new ArrayList<ElementHandler<T>>();
            statesWithHandlers.put(state, handlers);
        }
        handlers.add(handler);
    }
    
    NodeModel<T> getModel() {
    	return model;
    }
}
