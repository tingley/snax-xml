package net.sundell.snax;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;

import static net.sundell.snax.TestUtils.*;

import org.junit.Test;

public class TestSelectors {
    private static XMLInputFactory factory = XMLInputFactory.newInstance();

    // Test elements("a", "b", "c")
    @Test
    public void testElements() throws Exception {
        // Test ElementHandler.elements()
        final TestHandler foo = new TestHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
                    elements("xml", "foo", "bar").attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foo><bar/></foo></xml>"), null);
        assertEquals("bar", foo.elementName);
        
        // Test ElementSelector.elements()
        final TestHandler bar = new TestHandler();
        parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
                    element("xml").elements("foo", "bar").attach(bar);
        }}.build());
        parser.parse(new StringReader("<xml><foo><bar/></foo></xml>"), null);
        assertEquals("bar", bar.elementName);
    }
    
    // Test Regex element matching with an ElementFilter
    @Test
    public void testRegexElementSelectors() throws Exception {
        final TestHandler foo = new TestHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").element(new ElementFilter() {
                @Override
                public boolean test(StartElement element) {
                    return Pattern.matches("foo.*", element.getName().getLocalPart());
                }
            }).attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foozle /></xml>"), null);
        assertEquals("foozle", foo.elementName);       
        
        // Test ElementHandler.elements()
        final TestHandler bar= new TestHandler();
        parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element(new ElementFilter() {
                @Override
                public boolean test(StartElement element) {
                    return Pattern.matches(".*l", element.getName().getLocalPart());
                }
            }).attach(bar);
        }}.build());
        parser.parse(new StringReader("<xml><foozle /></xml>"), null);
        assertEquals("xml", bar.elementName);        
    }
    
    @Test
    public void testChild() throws Exception {      
        final TestCHandler foo = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").element("foo").child().attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foo><bar>BAR</bar></foo></xml>"), null);
        assertEquals("BAR", foo.contents);

        parser.parse(new StringReader("<xml><foo><baz>BAZ</baz></foo></xml>"), null);
        assertEquals("BAZ", foo.contents);
    }
    
    @Test
    public void testChildWithConstraints() throws Exception {      
        final TestCHandler foo = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").element("foo")
                .child(with("id").equalTo("val1"))
                    .attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foo><baz id='val1'>YES</baz><bar>NO</bar></foo></xml>"), null);
        assertEquals("YES", foo.contents);
    }
    
    @Test
    public void testDescendant() throws Exception {
        final TestCHandler foo = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").descendant().attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foo>YES</foo></xml>"), null);
        assertEquals("YES", foo.contents);        
    }
    
    @Test
    public void testDeepDescendant() throws Exception {
        final TestCHandler foo = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").descendant().element("bar").attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foo><bar>YES</bar></foo></xml>"), null);
        assertEquals("YES", foo.contents);
        foo.contents = "";
        parser.parse(new StringReader("<xml><foo><baz><bar>YES!</bar></baz></foo></xml>"), null);
        assertEquals("YES!", foo.contents);               

    }
    
    @Test
    public void testDescendantWithConstraints() throws Exception {
        final TestCHandler foo = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").descendant(with("id").equalTo("test1")).attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foo><bar id='test1'>YES</bar><bar id='test2'>NO</bar></foo></xml>"), null);
        assertEquals("YES", foo.contents);
        foo.contents = "";
        parser.parse(new StringReader("<xml><foo><baz><bar id='test1'>YES</bar></baz></foo></xml>"), null);
        assertEquals("YES", foo.contents);
        foo.contents = "";
        parser.parse(new StringReader("<xml><foo><bar><bar id='test1'>YES</bar></bar></foo></xml>"), null);
        assertEquals("YES", foo.contents);
    }

    @Test
    public void testDescendantFromRoot() throws Exception {
        final TestCHandler foo = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            descendant().element("foo").attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foo>YES</foo></xml>"), null);
        assertEquals("YES", foo.contents);
    }

    @Test
    public void testDescendantWithMultiplePaths() throws Exception {
        final TestCHandler foo = new TestCHandler();
        final TestCHandler bar = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            descendant().element("foo").attach(foo);
            descendant().element("bar").attach(bar);
        }}.build());
        parser.parse(new StringReader("<xml><foo>YES</foo><bar>YES2</bar></xml>"), null);
        assertEquals("YES", foo.contents);
        assertEquals("YES2", bar.contents);
    }

    @Test
    public void testDescendantWithMultipleQualifyingNodes() throws Exception {
        final TestMultiHandler foo = new TestMultiHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            descendant(with("foo").equalTo("bar")).attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><e1 foo='bar'><e2 foo='bar'/></e1></xml>"), null);
        assertEquals(2, foo.elementNames.size());
        assertEquals("e1", foo.elementNames.get(0));
        assertEquals("e2", foo.elementNames.get(1));

        foo.elementNames.clear();
        parser.parse(new StringReader("<e1 foo='bar'><x><e2 foo='bar'/></x></e1>"), null);
        assertEquals(2, foo.elementNames.size());
        assertEquals("e1", foo.elementNames.get(0));
        assertEquals("e2", foo.elementNames.get(1));
    }
    
}
