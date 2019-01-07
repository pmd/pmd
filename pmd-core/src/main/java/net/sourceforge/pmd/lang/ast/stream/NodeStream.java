/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.stream;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.stream.internal.NodeStreamImpl;


/**
 * Lazy stream of nodes. Wraps a {@link Stream} and provides additional
 * shorthands for a nice API. This API will replace {@link Node#findChildNodesWithXPath(String)}
 * from 7.0.0 on.
 *
 * Like a Stream, a NodeStream works with terminal and non-terminal operations.
 * Terminal operations consume the stream, after which it may not be used any more.
 * See <a href="https://stackoverflow.com/a/24474871/6245827">this SO answer</a> to
 * "duplicate" a node stream.
 *
 * @author Clément Fournier
 * @since 6.12.0
 */
public interface NodeStream<T extends Node> {

    // navigation


    default <R extends Node> NodeStream<R> children(Class<R> childClass) {
        return children().filterIs(childClass);
    }


    default NodeStream<Node> children() {
        return flatMap(Node::childrenStream);
    }


    default <R extends Node> NodeStream<R> descendants(Class<R> childClass) {
        return descendants().filterIs(childClass);
    }


    default NodeStream<Node> descendants() {
        return flatMap(Node::descendantStream);
    }

    // mapping


    <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends NodeStream<? extends R>> nodeStreamGetter);


    <R extends Node> NodeStream<R> map(Function<? super T, ? extends R> nodeGetter);

    // filtering


    NodeStream<T> filter(Predicate<? super T> nodeGetter);

    // these are shorthands defined relative to filter


    default <R extends Node> NodeStream<R> filterIs(Class<R> rClass) {
        return filter(rClass::isInstance).map(rClass::cast);
    }


    default NodeStream<T> withImage(String image) {
        return filter(it -> it.hasImageEqualTo(image));
    }


    default NodeStream<T> withImage(Pattern imagePattern) {
        return filter(it -> it.getImage() != null && imagePattern.matcher(it.getImage()).matches());
    }


    default NodeStream<T> imageMatching(String imagePattern) {
        return filter(it -> it.getImage().matches(imagePattern));
    }

    // other Stream methods


    /** Returns 'true' if the stream has at least one element. **/
    default boolean any() {
        return any(t -> true);
    }


    /** Returns 'true' if the stream is empty. **/
    default boolean none() {
        return none(t -> true);
    }


    /**
     * Returns whether any elements of this stream match the provided predicate.
     * If the stream is empty then false is returned and the predicate is not evaluated.
     */
    boolean any(Predicate<? super T> predicate);


    /**
     * Returns whether no elements of this stream match the provided predicate.
     * If the stream is empty then true is returned and the predicate is not evaluated.
     */
    boolean none(Predicate<? super T> predicate);


    /**
     * Returns whether all elements of this stream match the provided predicate.
     * If the stream is empty then true is returned and the predicate is not evaluated.
     */
    boolean all(Predicate<? super T> predicate);


    <R, A> R collect(Collector<? super T, A, R> collector);


    Optional<T> findFirst();


    Optional<T> findAny();


    Stream<? extends T> getStream();


    List<T> toList();

    // construction


    static <T extends Node> NodeStream<T> of(T node) {
        return of(Stream.of(node));
    }


    @SafeVarargs
    static <T extends Node> NodeStream<T> of(T... nodes) {
        return of(Stream.of(nodes));
    }


    static <T extends Node> NodeStream<T> of(Stream<? extends T> nodeStream) {
        return new NodeStreamImpl<>(nodeStream);
    }


    static <T extends Node> NodeStream<T> union(NodeStream<? extends T> a, NodeStream<? extends T> b) {
        return new NodeStreamImpl<>(Stream.concat(a.getStream(), b.getStream()));
    }
}
