/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.stream.internal;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.stream.NodeStream;


/**
 * @author Clément Fournier
 * @since 6.12.0
 */
public final class NodeStreamImpl<T extends Node> implements NodeStream<T> {

    private final Stream<? extends T> myStream;


    public NodeStreamImpl(Stream<? extends T> stream) {
        this.myStream = stream;
    }


    @Override
    public Stream<? extends T> getStream() {
        return myStream;
    }


    @Override
    public <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends NodeStream<? extends R>> mapper) {
        return NodeStream.of(myStream.flatMap(mapper.andThen(NodeStream::getStream)));
    }


    @Override
    public <R extends Node> NodeStream<R> map(Function<? super T, ? extends R> mapper) {
        return NodeStream.of(myStream.map(mapper));
    }


    @Override
    public NodeStream<T> filter(Predicate<? super T> predicate) {
        return NodeStream.of(myStream.filter(predicate));
    }


    @Override
    public boolean none(Predicate<? super T> predicate) {
        return myStream.noneMatch(predicate);
    }


    @Override
    public boolean any(Predicate<? super T> predicate) {
        return myStream.anyMatch(predicate);
    }


    @Override
    public boolean all(Predicate<? super T> predicate) {
        return myStream.allMatch(predicate);
    }


    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> findFirst() {
        return (Optional<T>) myStream.findFirst();
    }


    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> findAny() {
        return (Optional<T>) myStream.findAny();
    }


    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        return myStream.collect(collector);
    }


    @Override
    public List<T> toList() {
        return myStream.collect(Collectors.toList());
    }
}
