package net.sundell.snax.handlers;

import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

/**
 * Callback interface to handle events from an attached ListHandler.
 * 
 * @author tingley
 */
public interface ListConsumer {
        void beginList();
        void consumeElementStart(StartElement element);
        void consumeElementEnd(EndElement element);
        void endList();
}
