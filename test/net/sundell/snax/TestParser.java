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
import static net.sundell.snax.TestUtils.*;

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

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("net.sundell.snax.TestParser");
    }

}

