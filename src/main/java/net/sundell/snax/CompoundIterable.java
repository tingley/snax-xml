package net.sundell.snax;

import java.util.Collection;
import java.util.Iterator;

class CompoundIterable<T> implements Iterable<T> {

    private Iterable<T> first;
    private Iterable<T> rest; 

    private CompoundIterable(Iterable<T> first) {
        this.first = first;
    }
    
    private CompoundIterable(Iterable<T> first, Iterable<T> rest) {
        this.first = first;
        this.rest = rest;
    }

    /** 
     * Create a new Iterable<T> by adding the specified list contents to 
     * the front of an existing Iterable.
     * @param <T>
     * @param list
     * @param rest
     * @return
     */
    static <T> Iterable<T> prepend(Collection<T> coll, Iterable<T> rest) {
        return (coll.size() == 0) ? rest : new CompoundIterable<T>(coll, rest);
    }
  
    @Override
    public Iterator<T> iterator() {
        return new ChainIterator();
    }
    
    class ChainIterator implements Iterator<T> {
        private Iterator<T> firstIterator, restIterator;

        ChainIterator() {
            firstIterator = first.iterator();
            if (rest != null) {
                restIterator = rest.iterator();
            }
            else {
                restIterator = new EmptyIterator<T>();
            }
        }

        @Override
        public boolean hasNext() {
            return firstIterator.hasNext() || restIterator.hasNext();
        }

        @Override
        public T next() {
            if (firstIterator.hasNext()) {
                return firstIterator.next();
            }
            if (restIterator.hasNext()) {
                return restIterator.next();
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static class EmptyIterator<T> implements Iterator<T> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
