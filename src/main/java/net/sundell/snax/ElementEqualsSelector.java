package net.sundell.snax;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

class ElementEqualsSelector<T> extends ElementSelector<T> {

    private QName qname;
    
    ElementEqualsSelector(NodeModelBuilder<T> context, QName qname) {
        super(context);
        this.qname = qname;
    }

    ElementEqualsSelector(NodeModelBuilder<T> context, QName qname, 
            List<ElementConstraint> constraints) {
        super(context, constraints);
        this.qname = qname;
    }

    ElementEqualsSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, QName qname) {
        super(context, parent);
        this.qname = qname;
    }
    
    ElementEqualsSelector(NodeModelBuilder<T> context, ElementSelector<T> parent, QName qname, 
            List<ElementConstraint> constraints) {
        super(context, parent, constraints);
        this.qname = qname;
    }

    protected QName getQName() {
        return qname;
    }
    
    @Override
    protected boolean matches(StartElement element) {
        return qname.equals(element.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof ElementEqualsSelector)) return false;
        return qname.equals(((ElementEqualsSelector)o).qname);
    }
    
    @Override
    public String toString() {
        return "equals(" + qname + ")";
    }
}
