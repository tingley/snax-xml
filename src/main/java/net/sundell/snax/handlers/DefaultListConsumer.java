package net.sundell.snax.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.events.EndElement;

public abstract class DefaultListConsumer<T> implements ListConsumer {

    private List<T> list = new ArrayList<T>();
    
    @Override
    public void beginList() {
        list.clear();
    }

    @Override
    public void consumeElementEnd(EndElement element) {
    }

    protected void add(T t) {
        list.add(t);
    }
    
    @Override
    public void endList() {
    }
    
    public List<T> getList() { 
        return list;
    }

}
