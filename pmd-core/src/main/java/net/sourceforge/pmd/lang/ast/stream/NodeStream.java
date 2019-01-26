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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.stream.internal.NodeStreamImpl;


/**
 * Lazy stream of nodes. Wraps a {@link Stream} and provides additional
 * shorthands for a nice API. This API replaces the defunct {@link Node#findChildNodesWithXPath(String)}.
 *
 * <p>Like a {@link Stream}, a NodeStream works with terminal and non-terminal operations.
 * Terminal operations consume the stream, after which it may not be used any more.
 *
 * @implNote
 * Choosing to wrap a stream instead of extending the interface is to
 * allow the functions to return NodeStreams, and to avoid code the bloat
 * induced by delegation.
 * TODO we could easily make node streams iterable multiple times
 *
 * @param <T> Type of nodes this stream contains
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface NodeStream<T extends Node> {

    // mapping


    /**
     * Returns a node stream consisting of the results of replacing each
     * node of this stream with the contents of a mapped stream
     * produced by applying the given mapping function to each
     * node. Each mapped stream is closed after its contents have
     * been placed into this stream. (If a mapped stream is null an
     * empty stream is used, instead.)
     *
     * <p>If you want to map this node stream to a {@link Stream} with
     * arbitrary elements (ie not nodes), use {@link #getStream()} then
     * {@link Stream#flatMap(Function)}.
     *
     * @param mapper A function mapping the elements of this stream to another stream
     * @param <R>    Type of nodes contained in the returned stream
     *
     * @return A flat mapped stream
     *
     * @see Stream#flatMap(Function)
     */
    <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends NodeStream<? extends R>> mapper);


    /**
     * Returns a node stream consisting of the results of applying the given
     * mapping function to the node of this stream.
     *
     * <p>If you want to map this node stream to a {@link Stream} with
     * arbitrary elements (ie not nodes), use {@link #getStream()} then
     * {@link Stream#map(Function)}.
     *
     * @param mapper A function mapping the elements of this stream to another node type
     * @param <R>    The node type of the new stream
     *
     * @return A mapped stream
     *
     * @see Stream#flatMap(Function)
     */
    <R extends Node> NodeStream<R> map(Function<? super T, ? extends R> mapper);

    // navigation


    /**
     * Returns the {@linkplain #children() children stream} filtered by the given
     * node type.
     *
     * @param childClass Type of node the returning stream should contain
     * @param <R>        Type of node the returning stream should contain
     *
     * @return A new node stream
     *
     * @see #filterIs(Class)
     */
    default <R extends Node> NodeStream<R> children(Class<R> childClass) {
        return children().filterIs(childClass);
    }


    /**
     * Returns a node stream composed of all the children of the nodes
     * contained in this stream. This is equivalent to {@code flatMap(Node::childrenStream)}.
     *
     * @return A new node stream
     *
     * @see Node#childrenStream()
     * @see #children(Class)
     */
    default NodeStream<Node> children() {
        return flatMap(Node::childrenStream);
    }


    /**
     * Returns the {@linkplain #descendants() descendant stream} filtered by child type.
     *
     * @param childClass Type of node the returning stream should contain
     * @param <R>        Type of node the returning stream should contain
     *
     * @return A new node stream
     *
     * @see #filterIs(Class)
     */
    default <R extends Node> NodeStream<R> descendants(Class<R> childClass) {
        return descendants().filterIs(childClass);
    }


    /**
     * Returns a node stream composed of all the descendants of the nodes
     * contained in this stream. The nodes of the returning stream are yielded
     * in a depth-first fashion.
     *
     * <p>This is equivalent to {@code flatMap(Node::descendantStream)}.
     *
     * @return A new node stream
     *
     * @see Node#descendantStream()
     * @see #descendants(Class)
     */
    default NodeStream<Node> descendants() {
        return flatMap(Node::descendantStream);
    }


    // filtering


    /**
     * Returns a node stream consisting of the nodes of this stream that match
     * the given predicate.
     *
     * @param predicate A predicate to apply to each node to determine if
     *                  it should be included
     *
     * @return A filtered node stream
     */
    NodeStream<T> filter(Predicate<? super T> predicate);

    // these are shorthands defined relative to filter


    /**
     * Returns a node stream consisting of the nodes of this stream whose class
     * is a subtype of the given class.
     *
     * @param rClass The type of the nodes of the returned stream
     * @param <R>    The type of the nodes of the returned stream
     *
     * @return A filtered node stream
     */
    default <R extends Node> NodeStream<R> filterIs(Class<R> rClass) {
        return filter(rClass::isInstance).map(rClass::cast);
    }


    /**
     * Returns a node stream consisting of the nodes of this stream whose
     * {@linkplain Node#getImage() image} is exactly the given string.
     *
     * @param image The image the returned nodes must have
     *
     * @return A filtered node stream
     */
    default NodeStream<T> withImage(String image) {
        return filter(it -> it.hasImageEqualTo(image));
    }


    /**
     * Returns a node stream consisting of the nodes of this stream whose
     * {@linkplain Node#getImage() image} matches the given {@link Pattern}.
     *
     * @param regex A regular expression that the image of the returned nodes must match
     *
     * @return A filtered node stream
     */
    default NodeStream<T> imageMatching(Pattern regex) {
        return filter(it -> it.getImage() != null && regex.matcher(it.getImage()).matches());
    }


    /**
     * Returns a node stream consisting of the nodes of this stream whose
     * {@linkplain Node#getImage() image} matches the given regular expression.
     *
     * @param regex A regular expression that the image of the returned nodes must match
     *
     * @return A filtered node stream
     */
    default NodeStream<T> imageMatching(String regex) {
        return filter(it -> it.getImage().matches(regex));
    }

    // other Stream methods


    /**
     * Returns the number of nodes in this stream. This is a
     * terminal operation.
     *
     * @return the number of elements in this stream
     */
    default int count() {
        // ASTs are not so big as to warrant using a 'long' here
        return (int) getStream().count();
    }


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


    /**
     * Collects the elements of this node stream using the specified {@link Collector}.
     * This is a terminal operation, equivalent to {@link #getStream()} followed by
     * {@link Stream#collect(Collector)}.
     *
     * @param <R>       the type of the result
     * @param <A>       the intermediate accumulation type of the {@code Collector}
     * @param collector the {@code Collector} describing the reduction
     *
     * @return the result of the reduction
     *
     * @see Stream#collect(Collector)
     * @see java.util.stream.Collectors
     */
    <R, A> R collect(Collector<? super T, A, R> collector);


    /**
     * Collects the elements of this node stream into a list. This is a terminal operation,
     * equivalent to calling {@code collect(Collectors.toList())}.
     *
     * @return a list containing the elements of this stream
     *
     * @see java.util.stream.Collectors
     */
    default List<T> toList() {
        return collect(Collectors.toList());
    }


    /**
     * Returns an Optional describing the first element of this stream,
     * or an empty Optional if the stream is empty.
     */
    Optional<T> findFirst();


    /**
     * Returns an Optional describing the first element of this stream,
     * or an empty Optional if the stream is empty. Returns an Optional
     * describing some element of the stream, or an empty Optional if the
     * stream is empty.
     */
    Optional<T> findAny();


    /**
     * Returns the stream backing this node stream. Closing the returned
     * stream with a terminal operation will make this node stream unusable.
     *
     * @return The stream backing this node stream
     */
    Stream<? extends T> getStream();

    // construction


    /**
     * Returns a node stream containing a single node.
     *
     * @param node The node to contain
     * @param <T>  Element type of the returned stream
     *
     * @return A new node stream
     */
    static <T extends Node> NodeStream<T> of(T node) {
        return of(Stream.of(node));
    }


    /**
     * Returns a node stream whose elements are the given nodes in order.
     *
     * @param nodes The elements of the new stream
     * @param <T>   Element type of the returned stream
     *
     * @return A new node stream
     */
    @SafeVarargs
    static <T extends Node> NodeStream<T> of(T... nodes) {
        return of(Stream.of(nodes));
    }


    /**
     * Returns a node stream wrapping the given stream of nodes.
     * The returned node stream is backed by the given stream, so
     * that invoking a terminal operation on either will close both.
     *
     * @param nodeStream The elements of the new stream
     * @param <T>        Element type of the returned stream
     *
     * @return A new node stream
     */
    static <T extends Node> NodeStream<T> of(Stream<? extends T> nodeStream) {
        return new NodeStreamImpl<>(nodeStream);
    }


    /**
     * Returns a node stream containing all the elements of the first stream,
     * then all the elements of the second stream. This is equivalent to
     * calling {@link #of(Stream)} with the result of {@link Stream#concat(Stream, Stream)}.
     *
     * @param <T> The type of stream elements
     * @param a   the first input stream
     * @param b   the second input stream
     *
     * @return the concatenation of the two input streams
     */
    static <T extends Node> NodeStream<T> union(NodeStream<? extends T> a, NodeStream<? extends T> b) {
        return of(Stream.concat(a.getStream(), b.getStream()));
    }
}
