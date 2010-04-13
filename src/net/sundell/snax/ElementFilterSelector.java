package net.sundell.snax;

import javax.xml.stream.events.StartElement;

class ElementFilterSelector<T> extends ElementSelector<T> {

    private ElementFilter filter;

    ElementFilterSelector(NodeModelBuilder<T> context, ElementFilter filter) {
        super(context);
        this.filter = filter;
    }
    
    ElementFilterSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, ElementFilter filter) {
        super(context, parent);
        this.filter = filter;
    }

    protected ElementFilter getFilter() {
        return filter;
    }
    
    @Override
    protected boolean matches(StartElement element) {
        return filter.test(element);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ElementFilterSelector)) {
            return false;
        }
        return filter.equals(((ElementFilterSelector)o).filter);
    }
}
