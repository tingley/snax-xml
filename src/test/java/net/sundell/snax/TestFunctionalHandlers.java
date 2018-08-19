package net.sundell.snax;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import org.junit.Test;

import net.sundell.snax.TestUtils.TestHandler;

public class TestFunctionalHandlers {

    private static XMLInputFactory factory = XMLInputFactory.newInstance();

    @Test
    public void testCallFucntionalHandlers() throws Exception {
        NodeModelBuilder<Set<String>> builder = new NodeModelBuilder<>();
        builder.elements("xml", "foo")
            .start((el, state) -> state.add("start: " + el.getName().getLocalPart()))
            .end((el, state) -> state.add("end: " + el.getName().getLocalPart()))
            .chars((chars, state) -> state.add("characters: " + chars));
        SNAXParser<Set<String>> parser = SNAXParser.createParser(factory, builder.build());
        Set<String> state = new HashSet<>();
        parser.parse(new StringReader("<xml><foo>123</foo></xml>"), state);
        Set<String> expected = new HashSet<>();
        expected.add("start: foo");
        expected.add("end: foo");
        expected.add("characters: 123");
    }
}
