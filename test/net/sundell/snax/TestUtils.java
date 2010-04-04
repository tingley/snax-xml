package net.sundell.snax;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;

/**
 * Test handlers, etc.
 */
public class TestUtils {
    static class TestHandler extends DefaultElementHandler<Object> {
        public String elementName = "";
        @Override
        public void startElement(StartElement el, Object unused) {
            this.elementName = el.getName().getLocalPart();
        }
    }
    
    static class TestCHandler extends DefaultElementHandler<Object> {
        public String contents = "";
        @Override
        public void characters(StartElement parent, Characters data, Object unused) {
            contents = data.getData();
        }
    }
  
    static class TestMultiHandler extends DefaultElementHandler<Object> {
        public List<String> elementNames = new ArrayList<String>();
        @Override
        public void startElement(StartElement el, Object unused) {
            elementNames.add(el.getName().getLocalPart());
        }
    }

    static class ErrorThrowingHandler extends DefaultElementHandler<Object> {
        private String message;
        public ErrorThrowingHandler(String message) {
            this.message = message;
        }
        @Override
        public void startElement(StartElement el, Object unused) throws SNAXUserException {
            throw new SNAXUserException(message);
        }
    }
   
}
