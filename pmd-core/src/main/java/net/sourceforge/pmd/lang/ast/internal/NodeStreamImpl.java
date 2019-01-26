/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;


/**
 * Implementation of {@link NodeStream}.
 *
 * <p>Intermediate operations like {@link #filter(Predicate)} or {@link #flatMap(Function)}
 * specify new pipeline operations that are stacked on the stream produced by
 * {@link #toStream()}. Terminal operations like {@link #count()} or {@link #toList()}
 * create a new temporary Stream with the correct pipeline and then apply the terminal
 * operation to it. That temporary stream is consumed, but subsequent terminal
 * operations on the NodeStream will be called on new Streams.
 *
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class NodeStreamImpl<T extends Node> implements NodeStream<T> {

    private final Supplier<Stream<T>> myStreamBuilder;


    private NodeStreamImpl(Supplier<Stream<T>> stream) {
        this.myStreamBuilder = stream;
    }


    @Override
    public Stream<T> toStream() {
        return myStreamBuilder.get();
    }

    @Override
    public <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends NodeStream<? extends R>> mapper) {
        return mapMyStreamGetter(tStream -> tStream.flatMap(mapper.andThen(NodeStream::toStream)));
    }


    @Override
    public <R extends Node> NodeStream<R> map(Function<? super T, ? extends R> mapper) {
        return mapMyStreamGetter(tStream -> tStream.map(mapper));
    }


    @SafeVarargs
    @Override
    public final <R extends Node> NodeStream<R> forkJoin(Function<? super T, ? extends NodeStream<? extends R>> fst,
                                                         Function<? super T, ? extends NodeStream<? extends R>> snd,
                                                         Function<? super T, ? extends NodeStream<? extends R>>... rest) {
        Objects.requireNonNull(fst);
        Objects.requireNonNull(snd);

        Function<? super T, ? extends NodeStream<? extends R>>[] mappers = Arrays.copyOf(rest, rest.length + 2);

        mappers[mappers.length - 1] = snd;
        mappers[mappers.length - 2] = fst;

        return forkJoinImpl(mappers);
    }


    @SuppressWarnings("rawtypes")
    @SafeVarargs
    private final <R extends Node> NodeStream<R> forkJoinImpl(Function<? super T, ? extends NodeStream<? extends R>>... mappers) {

        Function<? super T, ? extends NodeStream<? extends R>> aggregate =
            t -> NodeStream.union(Arrays.stream(mappers)
                                        .map(f -> f.apply(t))
                                        .<NodeStream<R>>toArray(NodeStream[]::new));

        // with forkJoin we know that the stream will be iterated more than twice
        // so we cache the values
        return cached().flatMap(aggregate);
    }


    @Override
    public NodeStream<T> filter(Predicate<? super T> predicate) {
        return mapMyStreamGetter(tStream -> tStream.filter(predicate));
    }


    private <R extends Node> NodeStream<R> mapMyStreamGetter(Function<Stream<T>, Stream<R>> mapper) {
        return fromSupplier(mapSupplier(this::toStream, mapper));
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
        return new NodeStreamImpl<>(nodeStream);
    }

}
