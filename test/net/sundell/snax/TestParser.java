package net.sundell.snax;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;

import net.sundell.snax.SNAXParser;

import org.junit.*;
import static org.junit.Assert.*;

public class TestParser {

    private static XMLInputFactory factory = XMLInputFactory.newInstance();
    
    @Test
    public void test1() throws Exception {
        final TestHandler foo = new TestHandler();
        final TestHandler bar = new TestHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
                    element("xml").element("foo").attach(foo);
                    element("xml").element("bar").attach(bar);
                }}.build());
        Reader r = new StringReader("<xml><foo/><bar/></xml>");
        parser.parse(r, null);
        assertEquals("foo", foo.elementName);
        assertEquals("bar", bar.elementName);
    }
    
    @Test
    public void test2() throws Exception {
        final TestHandler foo = new TestHandler();
        final TestHandler bar = new TestHandler();
        // foo should capture, but bar should not
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
                    element("xml").element("foo").element("bar").attach(foo);
                    element("xml").element("bar").attach(bar);
        }}.build());
        parser.parse(new StringReader("<xml><foo><bar/></foo></xml>"), null);
        assertEquals("bar", foo.elementName);
        assertEquals("", bar.elementName);
    }

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
    
    @Test
    public void testContents() throws Exception {
        final TestCHandler foo = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").element("foo").element("bar").attach(foo);
        }}.build());
        Reader r = new StringReader("<xml><foo><bar>FOO</bar></foo></xml>");
        parser.parse(r, null);
        assertEquals("FOO", foo.contents);
    }

    @Test
    public void testAttr() throws Exception {
        final TestCHandler foo = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").element("foo", with("this").equalTo("yes")).attach(foo);
        }}.build());
        Reader r = new StringReader("<xml><foo this=\"yes\">FOO</foo><foo this=\"no\">BAR</foo></xml>");
        parser.parse(r, null);
        assertEquals("FOO", foo.contents);
    }

    @Test
    public void testUserException() throws Exception {
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").child().element("bar").attach(new ErrorThrowingHandler("test"));
        }}.build());
        Reader r = new StringReader("<xml><foo><bar>bleh</bar></foo></xml>");
        boolean success = false;
        try {
            parser.parse(r, null);
        }
        catch (SNAXUserException e) {
            assertEquals("test", e.getMessage());
            assertNotNull(e.getLocation());
            assertEquals(1, e.getLocation().getLineNumber());
            assertEquals(16, e.getLocation().getColumnNumber());
            success = true;
        }
        assertTrue("User exception was never thrown", success);
    }
    
    @Test 
    public void testPartialExecution() throws Exception {
        final TestCHandler foo = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            elements("xml").child().attach(foo);
        }}.build());
        parser.startParsing(new StringReader("<xml><foo>YES</foo><bar>NO</bar></xml>"), null);
        for (int i = 0; i < 5; i++) {
            parser.processEvent();
        }
        assertEquals("YES", foo.contents);
        for (int i = 0; i < 5; i++) {
            parser.processEvent();
        }
        assertEquals("NO", foo.contents);
    }
    
    @Test
    public void testNestedBuilders() throws Exception {
    	final TestCHandler foo = new TestCHandler();
    	SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
    		element("xml").attach(new DefaultElementHandler<Object>() {
    			@Override
    			public void build(NodeModelBuilder<Object> builder) {
    				builder.element("foo").attach(foo);
    			}
    		});
    	}}.build());
        parser.parse(new StringReader("<xml><foo>YES</foo></xml>"), null);
        assertEquals("YES", foo.contents);
    }
    
    @Test
    public void testMultipleNestedBuilders() throws Exception {
        final TestCHandler foo = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").attach(new DefaultElementHandler<Object>() {
                @Override
                public void build(NodeModelBuilder<Object> builder) {
                    builder.element("foo").attach(new DefaultElementHandler<Object>() {
                        @Override
                        public void build(NodeModelBuilder<Object> builder2) {
                            builder2.element("bar").attach(foo);
                        }
                    });
                }
            });
        }}.build());
        parser.parse(new StringReader("<xml><foo><bar>YES</bar></foo></xml>"), null);
        assertEquals("YES", foo.contents);
    }

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
    
    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("net.sundell.snax.TestParser");
    }

}

