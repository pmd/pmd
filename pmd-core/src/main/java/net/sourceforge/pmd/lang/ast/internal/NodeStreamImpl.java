/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;


/**
 * @author Clément Fournier
 * @since 6.12.0
 */
public final class NodeStreamImpl<T extends Node> implements NodeStream<T> {

    private final Supplier<Stream<T>> myStreamBuilder;

    /**
     * Populates the cached stream supplier.
     * * When doing a union, the cache produces the Stream.concat of the component streams
     * * Otherwise, when a terminal operation is called, it necessarily goes through {@link #toStream()},
     * and the elements are dumped to a list.
     */
    private final Supplier<Supplier<Stream<T>>> myCacheMaker;

    /** Produces a valid open stream on each get. Null when no terminal operation has been called. */
    private Supplier<Stream<T>> myCachedValue;


    private NodeStreamImpl(Supplier<Stream<T>> stream, Supplier<Supplier<Stream<T>>> cacheMaker) {
        this.myStreamBuilder = stream;
        this.myCacheMaker = cacheMaker;
    }


    @Override
    public Stream<T> toStream() {
        // was called from a terminal operation or directly by the user
        // so we cache the value
        return toStream(true);
    }


    private Stream<T> toStream(boolean populateCache) {
        if (myCachedValue != null) {
            return myCachedValue.get();
        } else if (!populateCache) {
            return myStreamBuilder.get();
        } else {
            // populate cache
            myCachedValue = myCacheMaker.get();
            return myCachedValue.get();
        }
    }


    // Test only
    boolean isCached() {
        return myCachedValue != null;
    }


    @Override
    public <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends NodeStream<? extends R>> mapper) {
        return mapMyStreamGetter(tStream -> tStream.flatMap(mapper.andThen(NodeStream::toStream)));
    }


    @Override
    public <R extends Node> NodeStream<R> map(Function<? super T, ? extends R> mapper) {
        return mapMyStreamGetter(tStream -> tStream.map(mapper));
    }


    @Override
    public NodeStream<T> filter(Predicate<? super T> predicate) {
        return mapMyStreamGetter(tStream -> tStream.filter(predicate));
    }


    private <R extends Node> NodeStream<R> mapMyStreamGetter(Function<Stream<T>, Stream<R>> mapper) {
        return fromSupplier(mapSupplier(() -> this.toStream(false), mapper)); // intermediate operations don't populate the cache
    }


    private static <U, R> Supplier<R> mapSupplier(Supplier<? extends U> base, Function<? super U, ? extends R> fun) {
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
    public static <T extends Node> NodeStream<T> fromSupplier(Supplier<Stream<T>> nodeStream) {
        return new NodeStreamImpl<>(nodeStream, () -> {
            List<T> lst = nodeStream.get().collect(Collectors.toList());
            return lst::stream;
        }); // use the default cache maker
    }


    @SafeVarargs
    public static <T extends Node> NodeStream<T> unionImpl(NodeStream<? extends T>... streams) {
        // evaluating toStream() for the union stream will evaluate the toStream of each substream eagerly and cache them inside the substreams.
        // so creating another cache list in the union with the values of all substreams is not useful.
        Supplier<Supplier<Stream<T>>> joinCacheBuilder = () -> () -> Stream.of(streams).flatMap(NodeStream::toStream);
        return new NodeStreamImpl<>(joinCacheBuilder.get(), joinCacheBuilder);
    }
}
