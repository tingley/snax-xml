package net.sundell.snax.handlers;

import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.namespace.QName;

import net.sundell.snax.DefaultElementHandler;
import net.sundell.snax.NodeModelBuilder;
import net.sundell.snax.SNAXUserException;

/**
 * Handler to simplify processing basic XML list structures like
 * <code>
 * &lt;foo%gt;
 *   &lt;row ... /&gt;
 *   &lt;row ... /&gt;
 *   &lt;row ... /&gt;
 * &lt;/foo&gt;
 * </code>
 * This handler can be attached to a model normally; it requires that the 
 * data object implement the ListConsumer interface.
 * 
 * @author tingley
 */
public class ListHandler<T extends ListConsumer> extends DefaultElementHandler<T> {
    private QName elementName;
    private String localName;
    
    // elementName is the item name, eg "row"
    public ListHandler(QName elementName) {
        this.elementName = elementName;
    }
    public ListHandler(String localName) {
        this.localName = localName;
    }

    @Override
    public void startElement(StartElement element, T data) {
        data.beginList();
    }
    
    @Override
    public void endElement(EndElement element, T data) {
        data.endList();
    }

    @Override
    public void build(NodeModelBuilder<T> builder) {
        if (elementName != null) {
            builder.element(elementName).attach(new InnerListHandler());
        }
        else {
            builder.element(localName).attach(new InnerListHandler());
        }
    }

    class InnerListHandler extends DefaultElementHandler<T> {
        @Override
        public void startElement(StartElement element, T data) {
            data.consumeElementStart(element);
        }
        
        @Override
        public void endElement(EndElement element, T data) throws SNAXUserException {
            data.consumeElementEnd(element);
        } 
    }
}

