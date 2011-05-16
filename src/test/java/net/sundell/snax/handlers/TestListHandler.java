package net.sundell.snax.handlers;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;

import net.sundell.snax.NodeModelBuilder;
import net.sundell.snax.SNAXParser;
import org.junit.Test;

public class TestListHandler {

    private static XMLInputFactory factory = XMLInputFactory.newInstance();
    
    static final QName ROW_EL = new QName("row");
    static final QName VAL_ATTR = new QName("val");

    @Test
    public void test1() throws Exception {
        SNAXParser<TestListBuilder> parser = SNAXParser.createParser(factory, new NodeModelBuilder<TestListBuilder>() {{
            elements("xml", "list")
                .attach(new ListHandler<TestListBuilder>(ROW_EL));
        }}.build());
        Reader r = new StringReader(
        		"<xml><list><row val='1'/><row val='2'/><row val='3'/></list></xml>"
		);
        TestListBuilder b = new TestListBuilder(VAL_ATTR);
        parser.parse(r, b);
        assertEquals(3, b.getList().size());
        assertEquals("1", b.getList().get(0));
        assertEquals("2", b.getList().get(1));
        assertEquals("3", b.getList().get(2));
    }
    
    static class TestListBuilder extends DefaultListConsumer<String> {
        private QName attr;
        TestListBuilder(QName attr) {
            this.attr = attr;
        }
        
        @Override
        public void consumeElementStart(StartElement element) {
            add(element.getAttributeByName(attr).getValue());
        }
    }
}
