package net.sundell.snax;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.NotationDeclaration;

/**
 * A data structure that defines parse behavior for an XML document.  <code>NodeModel</code>
 * encapsulates a basic state machine that is described using a <code>NodeModelBuilder</code>.
 * Once the <code>NodeModel</code> has been built, it can not be modified. 
 * 
 * @param <T> Data object type that will be passed to parse calls
 * @see NodeModelBuilder
 * @see SNAXParser
 */
public class NodeModel<T> {

    private List<DeclarationHandler<T>> dtdHandlers = new ArrayList<DeclarationHandler<T>>();
    private NodeState<T> root;
       
    NodeModel() {
        this(new NodeState<T>());
    }
    
    NodeModel(NodeState<T> root) {
        this.root = root;
    }

    NodeState<T> getRoot() {
        return root;
    }

    void addDeclarationHandler(DeclarationHandler<T> handler) {
        dtdHandlers.add(handler);
    }
    
    void handleDTD(DTD dtd, T data) {
        for (DeclarationHandler<T> h : dtdHandlers) {
            h.dtd(dtd, data);
        }
    }
    
    void handleEntityDeclaration(EntityDeclaration decl, T data) {
        for (DeclarationHandler<T> h : dtdHandlers) {
            h.entityDeclaration(decl, data);
        }
    }

    void handleEntityReference(EntityReference ref, T data) {
        for (DeclarationHandler<T> h : dtdHandlers) {
            h.entityReference(ref, data);
        }
    }

    void handleNotationDeclaration(NotationDeclaration notation, T data) {
        for (DeclarationHandler<T> h : dtdHandlers) {
            h.notationDeclaration(notation, data);
        }
    }
}
