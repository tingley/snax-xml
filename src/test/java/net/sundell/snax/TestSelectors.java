package net.sundell.snax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    // Test Regex element matching with an ElementConstraint
    @Test
    public void testRegexElementSelectors() throws Exception {
        final TestHandler foo = new TestHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").child(new ElementConstraint() {
                @Override
                public boolean matches(StartElement element) {
                    return Pattern.matches("foo.*", element.getName().getLocalPart());
                }
            }).attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foozle /></xml>"), null);
        assertEquals("foozle", foo.elementName);       
        
        // Test ElementHandler.elements()
        final TestHandler bar= new TestHandler();
        parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            child(new ElementConstraint() {
                @Override
                public boolean matches(StartElement element) {
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
    public void testMultipleChildRules() throws Exception {
        final TestCHandler foo = new TestCHandler();
        final TestCHandler bar = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            child().element("foo").attach(foo);
            child().element("bar").attach(bar);
        }}.build());
        parser.parse(new StringReader("<xml><foo>YES</foo><bar>YES2</bar></xml>"), null);
        assertEquals("YES", foo.contents);
        assertEquals("YES2", bar.contents);
        
    }

    @Test
    public void testNamedDescendantAndChild() throws Exception {
        final TestCHandler foo = new TestCHandler();
        final TestCHandler bar = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            descendant("bar").attach(bar);
            child().element("foo").attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foo>YES</foo><bar>YES2</bar></xml>"), null);
        assertEquals("YES", foo.contents);
        assertEquals("YES2", bar.contents);
    }

    @Test
    public void testNamedDescendantAndChild2() throws Exception {
        final TestCHandler foo = new TestCHandler();
        final TestCHandler bar = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new 
                NodeModelBuilder<Object>() {{
                            child().element("foo").attach(foo);
                            descendant().element("bar").attach(bar);
        }}.build());
        parser.parse(new StringReader("<xml><foo>YES</foo><x><bar>YES2</bar></x></xml>"), null);
        assertEquals("YES", foo.contents);
        assertEquals("YES2", bar.contents);
    }

    @Test
    public void testDescendantMasking() throws Exception {
        final TestCHandler foo = new TestCHandler();
        final TestCHandler bar = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            // More specific d-rules should mask less specific ones
            element("xml").descendant("bar").attach(bar);
            descendant("bar").attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><bar>YES2</bar></xml>"), null);
        assertEquals("", foo.contents);
        assertEquals("YES2", bar.contents);
    }

    @Test
    public void testDescendantNodeOrdering() throws Exception {
        final TestMultiHandler handler = new TestMultiHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            descendant().attach(handler);
        }}.build());
        parser.parse(new StringReader("<a><b><c/></b><d><e><f/></e><g/></d></a>"), null);
        assertEquals(7, handler.elementNames.size());
        assertEquals("a", handler.elementNames.get(0));
        assertEquals("b", handler.elementNames.get(1));
        assertEquals("c", handler.elementNames.get(2));
        assertEquals("d", handler.elementNames.get(3));
        assertEquals("e", handler.elementNames.get(4));
        assertEquals("f", handler.elementNames.get(5));
        assertEquals("g", handler.elementNames.get(6));
    }

    @Test
    public void testNamedDescendant() throws Exception {
        final TestMultiHandler handler = new TestMultiHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            descendant("x").attach(handler);
        }}.build());
        parser.parse(new StringReader("<foo><x>1</x><y><x/><z/></y><x><x/></x></foo>"), null);
        assertEquals(4, handler.elementNames.size());
        for (String s : handler.elementNames) {
            assertEquals("x", s);
        }
    }

    @Test
    public void testExplicitSelectorPriority() throws Exception {
        final TestHandler foo = new TestHandler();
        final TestHandler bar = new TestHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            // More specific d-rules should mask less specific ones
            elements("foo", "bar").attach(foo);
            descendant("bar").attach(bar);
        }}.build());
        parser.parse(new StringReader("<foo><bar/></foo>"), null);
        assertEquals("bar", foo.elementName);
        assertEquals("", bar.elementName);
    }

    @Test
    public void testDescendantFilter() throws Exception {
        final TestMultiHandler handler = new TestMultiHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            // Find all children whose element names have digits in them
            descendant(new ElementConstraint() {
                Pattern p = Pattern.compile("\\d");
                @Override
                public boolean matches(StartElement element) {
                    return p.matcher(element.getName().getLocalPart()).find();
                }
            }).attach(handler);
        }}.build());
        parser.parse(new StringReader("<foo1><bar/><b2ar/><ba3r/><bar/></foo1>"), null);
        assertEquals(3, handler.elementNames.size());
        assertEquals("foo1", handler.elementNames.get(0));
        assertEquals("b2ar", handler.elementNames.get(1));
        assertEquals("ba3r", handler.elementNames.get(2));
    }

    @Test
    public void testOnly1Element() throws Exception {
        final TestHandler foo = new TestHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            elements("xml", "foo").only(1).attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foo>hello</foo></xml>"), null);
        assertEquals("foo", foo.elementName);
        boolean failed = false;
        try {
            parser.parse(new StringReader("<xml><foo>hello</foo><foo>world</foo></xml>"), null);
        }
        catch (SNAXUserException e) {
            failed = true;
        }
        assertTrue("'only()' failed to restrict node", failed);
    }

    @Test
    public void testOnly1RawChild() throws Exception {
        final TestHandler foo = new TestHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            elements("xml").child().only(1).attach(foo);
        }}.build());

        parser.parse(new StringReader("<xml><test>hello</test></xml>"), null);
        assertEquals("test", foo.elementName);
        boolean failed = false;
        try {
            parser.parse(new StringReader("<xml><test>hello</test><foo>world</foo></xml>"), null);
        }
        catch (SNAXUserException e) {
            failed = true;
        }
        assertTrue("'only()' failed to restrict node", failed);
    }

    @Test
    public void testOnly2Elements() throws Exception {
        final TestHandler foo = new TestHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            elements("xml", "foo").only(2).attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foo>hello</foo><foo>world</foo></xml>"), null);
        assertEquals("foo", foo.elementName);
        boolean failed = false;
        try {
            parser.parse(new StringReader("<xml><foo>hello</foo><foo>world</foo><foo>2</foo></xml>"), null);
        }
        catch (SNAXUserException e) {
            failed = true;
        }
        assertTrue("'only()' failed to restrict node", failed);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroOnlyArgument() throws Exception {
        SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            elements("xml", "foo").only(0).attach(new TestHandler());
        }}.build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeOnlyArgument() throws Exception {
        SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            elements("xml", "foo").only(-1).attach(new TestHandler());
        }}.build());
    }

    // Make sure the 'only' counters reset if the model is re-used
    @Test
    public void testMultipleParserRunsWithOnly() throws Exception {
        final TestHandler foo = new TestHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            elements("xml", "foo").only(1).attach(foo);
        }}.build());
        parser.parse(new StringReader("<xml><foo>hello</foo></xml>"), null);
        assertEquals("foo", foo.elementName);
        foo.elementName = null;
        parser.parse(new StringReader("<xml><foo>hello</foo></xml>"), null);
        assertEquals("foo", foo.elementName);
    }

    @Test
    public void testParserStateReuse() throws Exception {
        final TestHandler foo = new TestHandler();
        SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            elements("xml", "foo").only(1).attach(foo);
            descendant().element("foo").attach(foo);
        }}.build());
    }
}
