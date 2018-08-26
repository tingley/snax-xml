package net.sundell.snax;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestFunctionalHandlers {

    private static XMLInputFactory factory = XMLInputFactory.newInstance();

    //@Test
    public void testCallFunctionalHandlers() throws Exception {
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
        assertEquals(expected, state);
    }

    static class OuterResult {
        private String outerValue;
    }
    static class InnerResult {
        private String innerValue;
    }

    @Test
    public void testChildModel() throws Exception {
        NodeModelBuilder<InnerResult> childBuilder = new NodeModelBuilder<InnerResult>();
        childBuilder.elements("a", "b").chars((c, data) -> data.innerValue = c.getData());
        NodeModel<InnerResult> childModel = childBuilder.build();

        // Verify that the inner model works
        SNAXParser<InnerResult> innerParser = SNAXParser.createParser(factory, childModel);
        InnerResult ir = new InnerResult();
        innerParser.parse(new StringReader("<a><b>123</b></a>"), ir);
        assertEquals("123", ir.innerValue);

        NodeModelBuilder<OuterResult> parentBuilder = new NodeModelBuilder<OuterResult>();
        // XXX This doesn't work, for two reasons.
        // 1) elements("x", "y").attach(childModel, ...) means that the first element the child model
        //    is sent is the <y>, not the <a>.
        // 2) I need to write code to lock execution into the child model somehow.  I may need to
        //    write some kind of nested state holder.
        parentBuilder.elements("x", "y")
                    .attach(childModel, InnerResult.class,
                            (startEl) -> new InnerResult(),
                            (inner, data) -> data.outerValue = inner.innerValue);
        NodeModel<OuterResult> parentModel = parentBuilder.build();

        SNAXParser<OuterResult> parser = SNAXParser.createParser(factory, parentModel);
        OuterResult result = new OuterResult();
        parser.parse(new StringReader("<x><y><a><b>123</b></a></y></x>"),result);
        assertEquals("123", result.outerValue);
    }
}
