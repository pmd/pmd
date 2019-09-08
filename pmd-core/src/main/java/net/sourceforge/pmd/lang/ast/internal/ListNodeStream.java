/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.ast.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * Random access node stream. Suitable for implementing eg {@link Node#children()},
 * since stuff like toList/count/get are free.
 */
public final class ListNodeStream<T extends Node> implements NodeStream<T> {

    public static final NodeStream<Node> EMPTY = new ListNodeStream<>(Collections.emptyList());

    private final List<T> myList;

    private ListNodeStream(List<T> list) {
        this.myList = list.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(list);
    }


    @Override
    public Stream<T> toStream() {
        return myList.stream();
    }

    @Override
    public boolean isEmpty() {
        return myList.isEmpty();
    }

    @Override
    public boolean nonEmpty() {
        return !isEmpty();
    }

    @Override
    public int count() {
        return myList.size();
    }

    @Override
    public NodeStream<T> cached() {
        return this;
    }

    @Override
    public Optional<T> first() {
        return get(0);
    }

    @Override
    public Optional<T> get(int n) {
        if (n < 0 || n >= myList.size()) {
            return Optional.empty();
        }
        return Optional.of(myList.get(n));
    }

    @Override
    public NodeStream<T> drop(int n) {
        if (n <= 0) {
            return this;
        } else if (n >= myList.size()) {
            return NodeStream.empty();
        } else {
            return ofNullable(myList.subList(n, myList.size()));
        }
    }

    @Override
    public NodeStream<T> take(int maxSize) {
        if (maxSize <= 0) {
            return NodeStream.empty();
        } else if (maxSize >= myList.size()) {
            return this;
        } else {
            return ofNullable(myList.subList(0, maxSize));
        }
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        myList.forEach(action);
    }

    @Override
    public Iterator<T> iterator() {
        return myList.iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return myList.spliterator();
    }

    @Override
    public List<T> toList() {
        return myList;
    }

    /** Does a copy. */
    public static <T extends Node> ListNodeStream<T> ofNullable(List<T> list) {
        list = new ArrayList<>(list);
        list.removeAll(Collections.singleton(null));
        return new ListNodeStream<>(list);
    }

    /** Doesn't copy. */
    public static <T extends Node> ListNodeStream<T> ofNonNull(List<T> list) {
        return new ListNodeStream<>(list);
    }
}
