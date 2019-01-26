/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.stream.internal;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.stream.NodeStream;


/**
 * @author Clément Fournier
 * @since 6.12.0
 */
public final class NodeStreamImpl<T extends Node> implements NodeStream<T> {

    private final Supplier<Stream<? extends T>> myStream;


    public NodeStreamImpl(Supplier<Stream<? extends T>> stream) {
        this.myStream = stream;
    }


    @Override
    public Stream<? extends T> toStream() {
        return myStream.get();
    }


    @Override
    public <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends NodeStream<? extends R>> mapper) {
        return mapMyStreamGetter(tStream -> tStream.<R>flatMap(mapper.andThen(NodeStream::toStream)));
    }


    @Override
    public <R extends Node> NodeStream<R> map(Function<? super T, ? extends R> mapper) {
        return mapMyStreamGetter(tStream -> tStream.map(mapper));
    }


    @Override
    public NodeStream<T> filter(Predicate<? super T> predicate) {
        return mapMyStreamGetter(tStream -> tStream.filter(predicate));
    }


    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> findFirst() {
        return (Optional<T>) myStream.get().findFirst();
    }


    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> findAny() {
        return (Optional<T>) myStream.get().findAny();
    }

    private <R extends Node> NodeStream<R> mapMyStreamGetter(Function<Stream<? extends T>, Stream<? extends R>> mapper) {
        return fromSupplier(mapSupplier(myStream, mapper));
    }


    private static <U, R> Supplier<R> mapSupplier(Supplier<U> base, Function<U, R> fun) {
        return () -> fun.apply(base.get());
    }


    /**
     * Returns a node stream that contains the elements of the given stream supplier.
     * The returned node stream is iterable multiple times. Each time a terminal operation
     * is called on it, the supplier should provide a fresh stream to start over.
     *
     * @param nodeStream The elements of the new stream
     * @param <T>        Element type of the returned stream
     *
     * @return A new node stream
     *
     * @apiNote This is a low-level utility that shouldn't ever be called from user code.
     */
    public static <T extends Node> NodeStream<T> fromSupplier(Supplier<Stream<? extends T>> nodeStream) {
        return new NodeStreamImpl<>(nodeStream);
    }
}
