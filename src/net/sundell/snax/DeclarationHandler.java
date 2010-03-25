package net.sundell.snax;

import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.NotationDeclaration;

/**
 * A handler that receives events about DTDs and related data, including 
 * unparsed entities and notations.  
 *
 * A DTDHandler is more limited in their application than an {@link ElementHandler},
 * as it can only be applied globally to a model.
 */
public interface DeclarationHandler<T> {

    public void dtd(DTD dtd, T data);
    
    public void entityDeclaration(EntityDeclaration entityDecl, T data);
    
    public void entityReference(EntityReference entityRef, T data);
    
    public void notationDeclaration(NotationDeclaration notationDecl, T data);
}
