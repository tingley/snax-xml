package net.sundell.snax;

import java.util.function.BiConsumer;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

class FunctionalHandler<T> implements ElementHandler<T> {

	private BiConsumer<StartElement, T> startConsumer;
	private BiConsumer<EndElement, T> endConsumer;
	private BiConsumer<Characters, T> charsConsumer;

	FunctionalHandler() { }

	public final FunctionalHandler<T> start(BiConsumer<StartElement, T> consumer) {
		this.startConsumer = consumer;
		return this;
	}

	public final FunctionalHandler<T> end(BiConsumer<EndElement, T> consumer) {
		this.endConsumer = consumer;
		return this;
	}

	public final FunctionalHandler<T> chars(BiConsumer<Characters, T> consumer) {
		this.charsConsumer = consumer;
		return this;
	}

	@Override
	public final void startElement(StartElement element, T data) throws SNAXUserException {
		if (startConsumer != null) {
			startConsumer.accept(element, data);
		}
	}

	@Override
	public final void endElement(EndElement element, T data) throws SNAXUserException {
		if (endConsumer != null) {
			endConsumer.accept(element, data);
		}
	}

	@Override
	public final void characters(StartElement parent, Characters characters, T data) throws SNAXUserException {
		if (charsConsumer != null) {
			charsConsumer.accept(characters, data);
		}
	}

	@Override
	public final void build(NodeModelBuilder<T> builder) {}
}
