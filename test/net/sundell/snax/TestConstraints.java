package net.sundell.snax;

import java.io.Reader;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;

import net.sundell.snax.TestUtils.TestCHandler;

import org.junit.Test;

import static net.sundell.snax.TestUtils.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests of element constraints.
 */
public class TestConstraints {
    private static XMLInputFactory factory = XMLInputFactory.newInstance();

    @Test
    public void testAttr() throws Exception {
        final TestCHandler foo = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").element("foo", with("this")).attach(foo);
        }}.build());
        Reader r = new StringReader("<xml><foo>FOO</foo><foo this=\"no\">BAR</foo></xml>");
        parser.parse(r, null);
        assertEquals("BAR", foo.contents);
    }

    @Test
    public void testAttrEqualTo() throws Exception {
        final TestCHandler foo = new TestCHandler();
        SNAXParser<?> parser = SNAXParser.createParser(factory, new NodeModelBuilder<Object>() {{
            element("xml").element("foo", with("this").equalTo("yes")).attach(foo);
        }}.build());
        Reader r = new StringReader("<xml><foo this=\"yes\">FOO</foo><foo this=\"no\">BAR</foo></xml>");
        parser.parse(r, null);
        assertEquals("FOO", foo.contents);
    }
    

}
