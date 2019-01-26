/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;


/**
 * Implementation of {@link NodeStream}.
 *
 * <p>Choosing to wrap a stream instead of extending the interface is to
 * allow the functions to return NodeStreams, and to avoid code the bloat
 * induced by delegation.
 *
 * <p>Intermediate operations like {@link #filter(Predicate)} or {@link #flatMap(Function)}
 * specify new pipeline operations that are stacked on the stream produced by
 * {@link #toStream()}. Terminal operations like {@link #count()} or {@link #toList()}
 * create a new temporary Stream with the correct pipeline and then apply the terminal
 * operation to it. That temporary stream is consumed, but subsequent terminal
 * operations on the NodeStream will be called on new Streams.
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
