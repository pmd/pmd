/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.ast.internal.NodeStreamImpl;


/**
 * Lazy stream of nodes. Wraps a {@link Stream} and provides additional
 * shorthands for a nice API. This API replaces the defunct {@link Node#findChildNodesWithXPath(String)}.
 *
 * <p>Unlike a Stream, NodeStreams can be iterated multiple times.
 *
 * @implNote
 * Choosing to wrap a stream instead of extending the interface is to
 * allow the functions to return NodeStreams, and to avoid code the bloat
 * induced by delegation.
 *
 * <p>Intermediate operations like {@link #filter(Predicate)} or {@link #flatMap(Function)}
 * specify new pipeline operations that are stacked on the stream produced by
 * {@link #toStream()}. Terminal operations like {@link #count()} or {@link #toList()}
 * create a new Stream with the correct pipeline and then apply the terminal
 * operation to it. That temporary stream is consumed, but subsequent terminal
 * operations on the node stream will be called on new Streams.
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
     * arbitrary elements (ie not nodes), use {@link #toStream()} then
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
     * arbitrary elements (ie not nodes), use {@link #toStream()} then
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


    /**
     * Applies the given mapping functions to this node stream in order and merges the
     * results into a new node stream. This allows exploring several paths at once on
     * the same stream.
     *
     * @param mappers Mapper functions
     * @param <R>     Common supertype for the element type of the return types of the mapper functions
     *
     * @return A merged node stream
     */
    default <R extends Node> NodeStream<R> forkJoin(Function<? super NodeStream<T>, ? extends NodeStream<? extends R>>... mappers) {
        return NodeStreamImpl.fromSupplier(() -> Arrays.stream(mappers).map(f -> f.andThen(NodeStream::toStream)).flatMap(f -> f.apply(this)));
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

    // "terminal" operations


    /**
     * Returns the number of nodes in this stream.
     *
     * @return the number of elements in this stream
     */
    default int count() {
        // ASTs are not so big as to warrant using a 'long' here
        return (int) toStream().count();
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
    default boolean any(Predicate<? super T> predicate) {
        return toStream().anyMatch(predicate);
    }


    /**
     * Returns whether no elements of this stream match the provided predicate.
     * If the stream is empty then true is returned and the predicate is not evaluated.
     */
    default boolean none(Predicate<? super T> predicate) {
        return toStream().noneMatch(predicate);
    }


    /**
     * Returns whether all elements of this stream match the provided predicate.
     * If the stream is empty then true is returned and the predicate is not evaluated.
     */
    default boolean all(Predicate<? super T> predicate) {
        return toStream().allMatch(predicate);
    }


    /**
     * Collects the elements of this node stream using the specified {@link Collector}.
     * This is equivalent to {@link #toStream()} followed by {@link Stream#collect(Collector)}.
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
    default <R, A> R collect(Collector<? super T, A, R> collector) {
        return toStream().collect(collector);
    }


    /**
     * Collects the elements of this node stream into a list. This is
     * equivalent to calling {@code collect(Collectors.toList())}.
     *
     * @return a list containing the elements of this stream
     *
     * @see Collectors#toList()
     */
    default List<T> toList() {
        return collect(Collectors.toList());
    }


    /**
     * Maps the elements of this node stream using the given mapping
     * and collects the result into a list.
     *
     * @return a list containing the elements of this stream
     *
     * @see Collectors#toList()
     */
    default <R> List<R> toList(Function<T, R> mapper) {
        return toStream().map(mapper).collect(Collectors.toList());
    }


    /**
     * Returns an Optional describing the first element of this stream,
     * or an empty Optional if the stream is empty.
     */
    default Optional<T> findFirst() {
        return toStream().findFirst();
    }


    /**
     * Returns an Optional describing the first element of this stream,
     * or an empty Optional if the stream is empty. Returns an Optional
     * describing some element of the stream, or an empty Optional if the
     * stream is empty.
     */
    default Optional<T> findAny() {
        return toStream().findAny();
    }


    /**
     * Returns a new stream of Ts having the pipeline of operations
     * defined by this node stream. This can be called multiple times.
     *
     * @return The stream backing this node stream
     */
    Stream<T> toStream();

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
        return NodeStreamImpl.fromSupplier(() -> Stream.of(node));
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
        return NodeStreamImpl.fromSupplier(() -> Stream.of(nodes));
    }


    /**
     * Returns a node stream containing all the elements of the given streams,
     * each stream flattened into a single stream.
     *
     * @param <T>     The type of stream elements
     * @param streams the streams to flatten in here
     *
     * @return the concatenation of the two input streams
     */
    static <T extends Node> NodeStream<T> union(NodeStream<? extends T>... streams) {
        return NodeStreamImpl.fromSupplier(() -> Stream.of(streams).flatMap(NodeStream::toStream));
    }
}
