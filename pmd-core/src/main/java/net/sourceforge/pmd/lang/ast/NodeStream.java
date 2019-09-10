/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.internal.StreamImpl;


/**
 * Lazy stream of AST nodes. Conceptually identical to a {@link java.util.stream.Stream}, but exposes
 * a specialized API to navigate abstract syntax trees. This API replaces the defunct {@link Node#findChildNodesWithXPath(String)}.
 *
 * <h1>API usage</h1>
 *
 * <p>The {@link Node} interface exposes methods like {@link Node#children()} or {@link Node#asStream()}
 * to obtain new NodeStreams. Null-safe construction methods are available here, see {@link #of(Node)},
 * {@link #of(Node[])}, {@link #fromIterable(Iterable)}.
 *
 * <p>Most functions have an equivalent in the {@link Stream} interface and their behaviour is equivalent.
 * Some additional functions are provided to iterate the axes of the tree: {@link #children()}, {@link #descendants()},
 * {@link #descendantsOrSelf()}, {@link #parents()}, {@link #ancestors()}, {@link #ancestorsOrSelf()},
 * {@link #precedingSiblings()}, {@link #followingSiblings()}. Filtering and mapping
 * nodes by type is possible through {@link #filterIs(Class)}, and the specialized {@link #children(Class)},
 * {@link #descendants(Class)}, and {@link #ancestors(Class)}.
 *
 * <p>Many complex predicates about nodes can be expressed by testing the emptiness of a node stream. E.g. the
 * following tests if the node is a variable declarator id initialized to the value {@code 0}:
 * <pre>
 *     {@linkplain #of(Node) NodeStream.of}(someNode)                           <i>// the stream here is empty if the node is null</i>
 *               {@linkplain #filterIs(Class) .filterIs}(ASTVariableDeclaratorId.class)<i>// the stream here is empty if the node was not a variable declarator id</i>
 *               {@linkplain #followingSiblings() .followingSiblings}()                    <i>// the stream here contains only the siblings, not the original node</i>
 *               {@linkplain #filterIs(Class) .filterIs}(ASTVariableInitializer.class)
 *               {@linkplain #children(Class) .children}(ASTExpression.class)
 *               .children(ASTPrimaryExpression.class)
 *               .children(ASTPrimaryPrefix.class)
 *               .children(ASTLiteral.class)
 *               {@linkplain #filterMatching(Function, Object) .filterMatching}(Node::getImage, "0")
 *               {@linkplain #filterNot(Predicate) .filterNot}(ASTLiteral::isStringLiteral)
 *               {@linkplain #nonEmpty() .nonEmpty}(); <i>// If the stream is non empty here, then all the pipeline matched</i>
 * </pre>
 *
 * <p>Many existing operations from the node interface can be written with streams too:
 * <ul>
 * <li><tt>node.{@link Node#getFirstChildOfType(Class) getFirstChildOfType(t)} === node.{@link Node#children(Class) children(t)}.{@link #first()}</tt></li>
 * <li><tt>node.{@link Node#getFirstDescendantOfType(Class) getFirstDescendantOfType(t)} === node.{@link Node#descendants(Class) descendants(t)}.{@link #first()}</tt></li>
 * <li><tt>node.{@link Node#getFirstParentOfType(Class) getFirstParentOfType(t)} === node.{@link Node#ancestors(Class) ancestors(t)}.{@link #first()}</tt></li>
 * <li><tt>node.{@link Node#findChildrenOfType(Class) findChildrenOfType(t)} === node.{@link Node#descendants(Class) children(t)}.{@link #toList()}</tt></li>
 * <li><tt>node.{@link Node#findDescendantsOfType(Class) findDescendantsOfType(t)} === node.{@link Node#descendants(Class) descendants(t)}.{@link #toList()}</tt></li>
 * <li><tt>node.{@link Node#getParentsOfType(Class) getParentsOfType(t)} === node.{@link Node#descendants(Class) ancestors(t)}.{@link #toList()}</tt></li>
 * <li><tt>node.{@link Node#getNthParent(int) getNthParent(n)} === node.{@link Node#ancestors() ancestors()}.{@link #get(int) get(n - 1)}</tt></li>
 * <li><tt>node.{@link Node#hasDescendantOfType(Class) hasDescendantOfType(t)} === node.{@link Node#descendants(Class) descendants(t)}.{@link #nonEmpty()}</tt>.</li>
 * </ul>
 *
 * <p>Unlike {@link Stream}s, NodeStreams can be iterated multiple times. That means, that the operations
 * that are <i>terminal</i> in the Stream interface (i.e. consume the stream) don't consume NodeStreams.
 * Be aware though, that node streams don't cache their results by default, so e.g. calling {@link #count()}
 * followed by {@link #toList()} will execute the whole pipeline twice. The elements of a stream can
 * however be {@linkplain #cached() cached} at an arbitrary point in the pipeline to evaluate the
 * upstream only once. Some construction methods allow building a node stream from an external data
 * source, e.g. {@link #fromIterable(Iterable) fromIterable} and {@link #fromSupplier(Supplier) fromSupplier}.
 * Depending on how the data source is implemented, the built node streams may be iterable only once.
 *
 * <p>Node streams may contain duplicates, which can be pruned with {@link #distinct()}.
 *
 * <h1>Details</h1>
 *
 * <p>NodeStream is a functional interface, equivalent to {@code Supplier<Stream<T>>}.
 * Its only abstract member is {@link #toStream()}.
 *
 * <p>Node streams are meant to be sequential streams, so there is no equivalent to {@link Stream#findAny()}.
 * The method {@link #first()} is an equivalent to {@link Stream#findFirst()}.
 *
 * <p>Node streams are most of the time ordered in document order (w.r.t. the XPath specification),
 * a.k.a. prefix order. Some operations which explicitly manipulate the order of nodes, like
 * {@link #union(NodeStream[]) union} or {@link #append(NodeStream) append}, may not preserve that ordering.
 * {@link #map(Function) map} and {@link #flatMap(Function) flatMap} operations may not preserve the ordering
 * if the stream has more than one element, since the mapping is applied in order to each element
 * of the receiver stream. This extends to methods defined in terms of map or flatMap, e.g.
 * {@link #descendants()} or {@link #children()}.
 *
 * @param <T> Type of nodes this stream contains
 *
 * @author Clément Fournier
 * @implNote Choosing to wrap a stream instead of extending the interface is to
 * allow the functions to return NodeStreams, and to avoid the code bloat
 * induced by delegation. Being a functional interface wasn't expected at
 * all, but in the end it's a nice-to-have that shortens the default implementation.
 *
 * <p>The default implementation relies exclusively on the {@link #toStream()}
 * method. This is very inefficient for short pipelines like {@code node.children().first()}
 * and so, optimal implementations are available for the singleton use case.
 * When the pipeline is long though, it's more efficient to use streams, and
 * so the default methods are ok.
 *
 * <p>About making stream iterable multiple times:
 * intermediate operations like {@link #filter(Predicate)} or {@link #flatMap(Function)}
 * specify new pipeline operations that are stacked on the stream produced by
 * {@link #toStream()}. Terminal operations like {@link #count()} or {@link #toList()}
 * create a new temporary Stream with the correct pipeline and then apply the terminal
 * operation to it. That temporary stream is consumed, but subsequent terminal
 * operations on the NodeStream will be called on new Streams.
 *
 * @since 7.0.0
 */
@FunctionalInterface
public interface NodeStream<T extends Node> extends Iterable<T> {

    // TODO this should probably not be a functional interface,
    //  it's too easy to instantiate.

    /**
     * Returns a new stream of Ts having the pipeline of operations
     * defined by this node stream. This can be called multiple times.
     *
     * @return A stream containing the same elements as this node stream
     */
    Stream<T> toStream();

    // lazy pipeline transformations


    /**
     * Returns a node stream consisting of the results of replacing each
     * node of this stream with the contents of a mapped stream
     * produced by applying the given mapping function to each
     * node. If a mapped stream is null an empty stream is used, instead.
     *
     * <p>If you want to flatMap this node stream to a {@link Stream} with
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
    default <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends NodeStream<? extends R>> mapper) {
        return () -> toStream().flatMap(mapper.<NodeStream<? extends R>>andThen(ns -> ns == null ? empty() : ns)
                                            .andThen(NodeStream::toStream));
    }


    /**
     * Returns a node stream consisting of the results of applying the given
     * mapping function to the node of this stream. If the mapping function
     * returns null, the elements are not included.
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
     * @see Stream#map(Function)
     */
    default <R extends Node> NodeStream<R> map(Function<? super T, ? extends R> mapper) {
        return () -> toStream().<R>map(mapper).filter(Objects::nonNull);
    }


    /**
     * Returns a node stream consisting of the nodes of this stream that match
     * the given predicate.
     *
     * @param predicate A predicate to apply to each node to determine if
     *                  it should be included
     *
     * @return A filtered node stream
     *
     * @see Stream#filter(Predicate)
     * @see #filterNot(Predicate)
     * @see #filterIs(Class)
     * @see #filterMatching(Function, Object)
     */
    default NodeStream<T> filter(Predicate<? super T> predicate) {
        return () -> toStream().filter(predicate);
    }


    /**
     * Returns a stream consisting of the elements of this stream, additionally
     * performing the provided action on each element as elements are consumed
     * from the resulting stream. Note that terminal operations such as {@link #count()}
     * don't necessarily execute the action.
     *
     * @param action an action to perform on the elements as they are consumed
     *               from the stream
     *
     * @return A new stream
     */
    default NodeStream<T> peek(Consumer<? super T> action) {
        return () -> toStream().peek(action);
    }


    /**
     * Returns a new node stream that contains all the elements of this stream, then
     * all the elements of the given stream.
     *
     * @param right Other stream
     *
     * @return A concatenated stream
     */
    default NodeStream<T> append(NodeStream<? extends T> right) {
        return () -> Stream.concat(this.toStream(), right.toStream());
    }


    /**
     * Returns a new node stream that contains all the elements of the given stream,
     * then all the elements of this stream.
     *
     * @param right Other stream
     *
     * @return A concatenated stream
     */
    default NodeStream<T> prepend(NodeStream<? extends T> right) {
        return () -> Stream.concat(right.toStream(), this.toStream());
    }


    /**
     * Returns a node stream containing all the elements of this node stream,
     * but which will evaluate the upstream pipeline only once. The returned
     * stream is also lazy, which means the elements of this stream are not
     * eagerly evaluated when calling this method, but only on the first
     * terminal operation called on the downstream of the returned stream.
     *
     * <p>This is useful e.g. if you want to call several terminal operations
     * without executing the pipeline several times. For example,
     *
     * <pre>
     *
     *     NodeStream&lt;T&gt; stream = NodeStream.of(...)
     *                                      <i>// long pipeline</i>
     *                                      <i>// ...</i>
     *                                      .cached()
     *                                      <i>// downstream</i>
     *                                      <i>// ...</i>
     *                                      ;
     *
     *     stream.forEach(this::addViolation); <i>// both up- and downstream will be evaluated</i>
     *     curViolations += stream.count();    <i>// only downstream is evaluated</i>
     * </pre>
     *
     * @return A cached node stream
     */
    default NodeStream<T> cached() {
        return new NodeStream<T>() {
            List<T> cachedValue = null;

            @Override
            public Stream<T> toStream() {
                return toList().stream();
            }

            @Override
            public int count() {
                return toList().size();
            }

            @Override
            public List<T> toList() {
                if (cachedValue == null) {
                    cachedValue = NodeStream.this.toList();
                }
                return cachedValue;
            }
        };
    }


    /**
     * Returns a stream consisting of the elements of this stream,
     * truncated to be no longer than maxSize in length.
     *
     * @param maxSize Maximum size of the returned stream
     *
     * @return A new node stream
     *
     * @throws IllegalArgumentException if n is negative
     * @see Stream#limit(long)
     * @see #drop(int)
     */
    default NodeStream<T> take(int maxSize) {
        AssertionUtil.assertArgNonNegative(maxSize);
        return () -> toStream().limit(maxSize);
    }


    /**
     * Returns a stream consisting of the remaining elements of this
     * stream after discarding the first n elements of the stream. If
     * this stream contains fewer than n elements then an empty stream
     * will be returned.
     *
     * @param n the number of leading elements to skip
     *
     * @return A new node stream
     *
     * @throws IllegalArgumentException if n is negative
     * @see Stream#skip(long)
     * @see #take(int)
     */
    default NodeStream<T> drop(int n) {
        AssertionUtil.assertArgNonNegative(n);
        return () -> toStream().skip(n);
    }


    /**
     * Returns the longest prefix of elements that satisfy the given predicate.
     *
     * @param predicate The predicate used to test elements.
     *
     * @return the longest prefix of this stream whose elements all satisfy
     *     the predicate `p`.
     */
    default NodeStream<T> takeWhile(Predicate<? super T> predicate) {
        return () -> IteratorUtil.takeWhile(toStream(), predicate);
    }


    /**
     * Returns a stream consisting of the distinct elements (w.r.t {@link Object#equals(Object)}) of this stream.
     *
     * @return a stream consisting of the distinct elements of this stream
     */
    default NodeStream<T> distinct() {
        return () -> toStream().distinct();
    }

    // tree navigation


    /**
     * Returns a node stream containing all the ancestors of the nodes
     * contained in this stream. The returned stream doesn't preserve document
     * order, since ancestors are yielded in innermost to outermost order.
     *
     * <p>This is equivalent to {@code flatMap(Node::ancestors)}.
     *
     * @return A stream of ancestors
     *
     * @see Node#ancestors()
     * @see #ancestorsOrSelf()
     * @see #ancestors(Class)
     */
    default NodeStream<Node> ancestors() {
        return flatMap(Node::ancestors);
    }


    /**
     * Returns a node stream containing the nodes contained in this stream and their ancestors.
     * The nodes of the returned stream are yielded in a depth-first fashion.
     *
     * <p>This is equivalent to {@code flatMap(Node::ancestorsOrSelf)}.
     *
     * @return A stream of ancestors
     *
     * @see #ancestors()
     */
    default NodeStream<Node> ancestorsOrSelf() {
        return flatMap(Node::ancestorsOrSelf);
    }


    /**
     * Returns a node stream containing all the (first-degree) parents of the nodes
     * contained in this stream.
     *
     * <p>This is equivalent to {@code map(Node::jjtGetParent)}.
     *
     * @return A stream of parents
     *
     * @see #ancestors()
     * @see #ancestorsOrSelf()
     */
    default NodeStream<Node> parents() {
        return map(Node::jjtGetParent);
    }


    /**
     * Returns a node stream containing all the children of the nodes
     * contained in this stream.
     *
     * <p>This is equivalent to {@code flatMap(Node::children)}.
     *
     * @return A stream of children
     *
     * @see Node#children()
     * @see #children(Class)
     */
    default NodeStream<Node> children() {
        return flatMap(Node::children);
    }


    /**
     * Returns a node stream containing all the strict descendants of the nodes
     * contained in this stream. The nodes of the returned stream are yielded
     * in a depth-first fashion.
     *
     * <p>This is equivalent to {@code flatMap(Node::descendants)}.
     *
     * @return A stream of descendants
     *
     * @see Node#descendants()
     * @see #descendants(Class)
     * @see #descendantsOrSelf()
     */
    default NodeStream<Node> descendants() {
        return flatMap(Node::descendants);
    }


    /**
     * Returns a node stream containing the nodes contained in this stream and their descendants.
     * The nodes of the returned stream are yielded in a depth-first fashion.
     *
     * <p>This is equivalent to {@code flatMap(Node::descendantsOrSelf)}.
     *
     * @return A stream of descendants
     *
     * @see Node#descendantsOrSelf()
     * @see #descendants()
     */
    default NodeStream<Node> descendantsOrSelf() {
        return flatMap(Node::descendantsOrSelf);
    }


    /**
     * Returns a node stream containing all the following siblings of the nodes contained
     * in this stream.
     *
     * @return A stream of siblings
     */
    default NodeStream<Node> followingSiblings() {
        return flatMap(StreamImpl::followingSiblings);
    }


    /**
     * Returns a node stream containing all the preceding siblings of the nodes contained
     * in this stream. The nodes are yielded from left to right, i.e. in document order.
     *
     * @return A stream of siblings
     */
    default NodeStream<Node> precedingSiblings() {
        return flatMap(StreamImpl::precedingSiblings);
    }


    /**
     * Returns the {@linkplain #children() children stream} of each node
     * in this stream, filtered by the given node type.
     *
     * <p>This is equivalent to {@code children().filterIs(rClass)}.
     *
     * @param rClass Type of node the returned stream should contain
     * @param <R>    Type of node the returned stream should contain
     *
     * @return A new node stream
     *
     * @see #filterIs(Class)
     * @see Node#children(Class)
     */
    default <R extends Node> NodeStream<R> children(Class<R> rClass) {
        return flatMap(it -> it.children(rClass));
    }


    /**
     * Returns the {@linkplain #descendants() descendant stream} of each node
     * in this stream, filtered by the given node type.
     *
     * <p>This is equivalent to {@code descendants().filterIs(rClass)}.
     *
     * @param rClass Type of node the returned stream should contain
     * @param <R>    Type of node the returned stream should contain
     *
     * @return A new node stream
     *
     * @see #filterIs(Class)
     * @see Node#descendants(Class)
     */
    default <R extends Node> NodeStream<R> descendants(Class<R> rClass) {
        return flatMap(it -> it.descendants(rClass));
    }


    /**
     * Returns the {@linkplain #ancestors() ancestor stream} of each node
     * in this stream, filtered by the given node type.
     *
     * <p>This is equivalent to {@code ancestors().filterIs(rClass)}.
     *
     * @param rClass Type of node the returned stream should contain
     * @param <R>    Type of node the returned stream should contain
     *
     * @return A new node stream
     *
     * @see #filterIs(Class)
     * @see Node#ancestors(Class)
     */
    default <R extends Node> NodeStream<R> ancestors(Class<R> rClass) {
        return flatMap(it -> it.ancestors(rClass));
    }


    /**
     * Applies the given mapping functions to this node stream in order and merges the
     * results into a new node stream. This allows exploring several paths at once on the
     * same stream. The method is lazy and won't evaluate the upstream pipeline several times.
     *
     * @param fst  First mapper
     * @param snd  Second mapper
     * @param rest Rest of the mappers
     * @param <R>  Common supertype for the element type of the streams returned by the mapping functions
     *
     * @return A merged node stream
     */
    @SuppressWarnings("rawtypes") // necessary because of generic array creation is forbidden
    default <R extends Node> NodeStream<R> forkJoin(Function<? super T, ? extends NodeStream<? extends R>> fst,
                                                    Function<? super T, ? extends NodeStream<? extends R>> snd,
                                                    Function<? super T, ? extends NodeStream<? extends R>>... rest) {
        Objects.requireNonNull(fst);
        Objects.requireNonNull(snd);

        List<Function<? super T, ? extends NodeStream<? extends R>>> mappers = new ArrayList<>(rest.length + 2);
        mappers.add(fst);
        mappers.add(snd);
        mappers.addAll(Arrays.asList(rest));

        Function<? super T, ? extends NodeStream<? extends R>> aggregate =
            t -> NodeStream.union(mappers.stream().map(f -> f.apply(t)).<NodeStream<R>>toArray(NodeStream[]::new));

        // with forkJoin we know that the stream will be iterated more than twice so we cache the values
        return cached().flatMap(aggregate);
    }

    // these are shorthands defined relative to filter


    /**
     * Filters the node of this stream using the negation of the given predicate.
     *
     * <p>This is equivalent to {@code filter(predicate.negate())}
     *
     * @param predicate A predicate to apply to each node to determine if
     *                  it should be included
     *
     * @return A filtered node stream
     *
     * @see #filter(Predicate)
     */
    default NodeStream<T> filterNot(Predicate<? super T> predicate) {
        return filter(predicate.negate());
    }


    /**
     * Filters the nodes of this stream that are a subtype of the given class.
     *
     * <p>This is equivalent to {@code filter(rClass::isInstance).map(rClass::cast)}.
     *
     * @param rClass The type of the nodes of the returned stream
     * @param <R>    The type of the nodes of the returned stream
     *
     * @return A filtered node stream
     *
     * @see #filter(Predicate)
     */
    @SuppressWarnings("unchecked")
    default <R extends Node> NodeStream<R> filterIs(Class<R> rClass) {
        return rClass == Node.class ? (NodeStream<R>) this
                                    : (NodeStream<R>) filter(rClass::isInstance);
    }


    /**
     * Filters the nodes of this stream by comparing a value extracted from the nodes
     * with the given constant. This takes care of null value by calling
     * {@link Objects#equals(Object, Object)}. E.g. to filter nodes that have
     * the {@linkplain Node#getImage() image} {@code "a"}, use {@code filterMatching(Node::getImage, "a")}.
     *
     * <p>This is equivalent to {@code filter(t -> Objects.equals(extractor.apply(t), comparand))}.
     *
     * @param extractor Function extracting a value from the nodes of this stream
     * @param comparand Value to which the extracted value will be compared
     * @param <U>       Type of value to compare
     *
     * @return A filtered node stream
     *
     * @see #filter(Predicate)
     * @see #filterNotMatching(Function, Object)
     */
    default <U> NodeStream<T> filterMatching(Function<? super T, ? extends U> extractor, U comparand) {
        return filter(t -> Objects.equals(extractor.apply(t), comparand));
    }


    /**
     * Inverse of {@link #filterMatching(Function, Object)}.
     *
     * @param extractor Function extracting a value from the nodes of this stream
     * @param comparand Value to which the extracted value will be compared
     * @param <U>       Type of value to compare
     *
     * @return A filtered node stream
     *
     * @see #filter(Predicate)
     * @see #filterMatching(Function, Object)
     */
    default <U> NodeStream<T> filterNotMatching(Function<? super T, ? extends U> extractor, U comparand) {
        return filter(t -> !Objects.equals(extractor.apply(t), comparand));
    }

    // "terminal" operations


    /**
     * Returns the number of nodes in this stream.
     *
     * @return the number of nodes in this stream
     */
    default int count() {
        // ASTs are not so big as to warrant using a 'long' here
        return (int) toStream().count();
    }


    /**
     * Returns 'true' if the stream has at least one element.
     *
     * @return 'true' if the stream has at least one element.
     *
     * @see #isEmpty()
     */
    default boolean nonEmpty() {
        return toStream().anyMatch(t -> true);
    }


    /**
     * Returns 'true' if the stream has no elements.
     *
     * @return 'true' if the stream has no elements.
     *
     * @see #nonEmpty()
     */
    default boolean isEmpty() {
        return !nonEmpty();
    }


    /**
     * Returns whether any elements of this stream match the provided predicate.
     * If the stream is empty then false is returned and the predicate is not evaluated.
     *
     * @param predicate The predicate that one element should match for this method to return true
     *
     * @return true if any elements of the stream match the provided predicate, otherwise false
     *
     * @see #all(Predicate)
     * @see #none(Predicate)
     */
    default boolean any(Predicate<? super T> predicate) {
        return toStream().anyMatch(predicate);
    }


    /**
     * Returns whether no elements of this stream match the provided predicate.
     * If the stream is empty then true is returned and the predicate is not evaluated.
     *
     * @param predicate The predicate that no element should match for this method to return true
     *
     * @return true if either no elements of the stream match the provided predicate or the stream is empty, otherwise false
     *
     * @see #any(Predicate)
     * @see #all(Predicate)
     */
    default boolean none(Predicate<? super T> predicate) {
        return toStream().noneMatch(predicate);
    }


    /**
     * Returns whether all elements of this stream match the provided predicate.
     * If the stream is empty then true is returned and the predicate is not evaluated.
     *
     * @param predicate The predicate that all elements should match for this method to return true
     *
     * @return true if either all elements of the stream match the provided predicate or the stream is empty, otherwise false
     *
     * @see #any(Predicate)
     * @see #none(Predicate)
     */
    default boolean all(Predicate<? super T> predicate) {
        return toStream().allMatch(predicate);
    }


    /**
     * Returns the element at index n in this stream.
     * If no such element exists, {@code null} is returned.
     *
     * <p>This is equivalent to <tt>{@link #drop(int) drop(n)}.{@link #first()}</tt>
     *
     * <p>If you'd rather continue processing the nth element as a node stream,
     * you can use <tt>{@link #drop(int) drop(n)}.{@link #take(int) take(1)}.</tt>
     *
     * @param n Index of the element to find
     *
     * @return The nth element of this stream, or {@code null} if it doesn't exist
     *
     * @throws IllegalArgumentException if n is negative
     */
    default @Nullable T get(int n) {
        return drop(n).first();
    }


    /**
     * Returns the first element of this stream, or {@code null} if the
     * stream is empty.
     *
     * <p>If you'd rather continue processingthe first element as a node
     * stream, you can use {@link #take(int) take(1)}.
     *
     * <p>This is equivalent to {@link #get(int) get(0)}.
     *
     * @return the first element of this stream, or {@code null} if it doesn't exist
     *
     * @see #first(Predicate)
     * @see #first(Class)
     * @see #firstOpt()
     */
    default @Nullable T first() {
        return toStream().findFirst().orElse(null);
    }


    /**
     * Returns an optional containing the first element of this stream,
     * or an empty optional if the stream is empty.
     *
     * <p>This is equivalent to {@code Optional.ofNullable(first())}.
     *
     * @return the first element of this stream, or an empty optional if it doesn't exist
     *
     * @see #first(Predicate)
     * @see #first(Class)
     * @see #first()
     */
    default Optional<T> firstOpt() {
        return Optional.ofNullable(first());
    }


    /**
     * Returns the first element of this stream that matches the given
     * predicate, or {@code null} if there is none.
     *
     * @param predicate The predicate that one element should match for
     *                  this method to return it
     *
     * @return the first element of this stream that matches the given
     * predicate, or {@code null} if it doesn't exist
     *
     * @see #first()
     * @see #first(Class)
     */
    default @Nullable T first(Predicate<? super T> predicate) {
        return filter(predicate).first();
    }


    /**
     * Returns the first element of this stream of the given type, or
     * {@code null} if there is none.
     *
     * @param rClass The type of node to find
     * @param <R>    The type of node to find
     *
     * @return the first element of this stream of the given type, or {@code null} if it doesn't exist
     *
     * @see #first()
     * @see #first(Predicate)
     */
    default <R extends Node> @Nullable R first(Class<R> rClass) {
        return filterIs(rClass).first();
    }


    /**
     * Returns the last element of this stream, or {@code null} if the
     * stream is empty.
     *
     * @return the last element of this stream, or {@code null} if it doesn't exist
     */
    default @Nullable T last() {
        return IteratorUtil.last(iterator());
    }


    /**
     * Returns the last element of this stream of the given type, or
     * {@code null} if there is none.
     *
     * @param rClass The type of node to find
     * @param <R>    The type of node to find
     *
     * @return the last element of this stream of the given type, or {@code null} if it doesn't exist
     *
     * @see #last()
     */
    default <R extends Node> @Nullable R last(Class<R> rClass) {
        return filterIs(rClass).last();
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
     * @see #toList()
     * @see #toList(Function)
     */
    default <R, A> R collect(Collector<? super T, A, R> collector) {
        return toStream().collect(collector);
    }


    /**
     * Collects the elements of this node stream into a list.
     *
     * <p>This is equivalent to {@code collect(Collectors.toList())}.
     *
     * @return a list containing the elements of this stream
     *
     * @see Collectors#toList()
     * @see #collect(Collector)
     */
    default List<T> toList() {
        return collect(Collectors.toList());
    }


    /**
     * Maps the elements of this node stream using the given mapping
     * and collects the results into a list.
     *
     * <p>This is equivalent to {@code collect(Collectors.mapping(mapper, Collectors.toList()))}.
     *
     * @param mapper Mapping function
     * @param <R>    Return type of the mapper, and element type of the returned list
     *
     * @return a list containing the elements of this stream
     *
     * @see Collectors#mapping(Function, Collector)
     * @see #collect(Collector)
     */
    default <R> List<R> toList(Function<? super T, ? extends R> mapper) {
        return collect(Collectors.mapping(mapper, Collectors.toList()));
    }

    // Iterable methods


    @Override
    default Iterator<T> iterator() {
        return toStream().iterator();
    }


    @Override
    default void forEach(Consumer<? super T> action) {
        toStream().forEach(action);
    }


    @Override
    default Spliterator<T> spliterator() {
        return toStream().spliterator();
    }

    // construction
    // we ensure here that no node stream may contain null values


    /**
     * Returns a node stream containing zero or one node,
     * depending on whether the argument is null or not.
     *
     * <p>If you know the node is not null, you can also
     * call <tt>node.{@link Node#asStream() asStream()}</tt>.
     *
     * @param node The node to contain
     * @param <T>  Element type of the returned stream
     *
     * @return A new node stream
     *
     * @see Node#asStream()
     */
    static <T extends Node> NodeStream<T> of(T node) {
        // overload the varargs to avoid useless array creation
        return node == null ? empty() : StreamImpl.singleton(node);
    }


    /**
     * Returns a node stream containing zero or one node,
     * depending on whether the optional is empty or not.
     *
     * @param optNode The node to contain
     * @param <T>     Element type of the returned stream
     *
     * @return A new node stream
     *
     * @see #of(Node)
     */
    static <T extends Node> NodeStream<T> ofOptional(Optional<T> optNode) {
        return optNode.map(StreamImpl::singleton).orElseGet(StreamImpl::empty);
    }


    /**
     * Returns a node stream whose elements are the given nodes
     * in order. Null elements are not part of the resulting node
     * stream.
     *
     * @param nodes The elements of the new stream
     * @param <T>   Element type of the returned stream
     *
     * @return A new node stream
     */
    @SafeVarargs
    static <T extends Node> NodeStream<T> of(T... nodes) {
        return () -> Stream.of(nodes).filter(Objects::nonNull);
    }


    /**
     * Returns a new node stream that contains the same elements as the given
     * iterable. Null items are filtered out of the resulting stream.
     *
     * <p>It's possible to map an iterator to a node stream by calling
     * {@code fromIterable(() -> iterator)}, but then the returned node stream
     * would only be iterable once.
     *
     * @param iterable Source of nodes
     * @param <T>      Type of nodes in the returned node stream
     *
     * @return A new node stream
     */
    static <T extends Node> NodeStream<T> fromIterable(Iterable<T> iterable) {
        return () -> StreamSupport.stream(iterable.spliterator(), false).filter(Objects::nonNull);
    }


    /**
     * Returns a new node stream backed by the given stream supplier.
     * Null items are filtered out of the resulting stream.
     *
     * <p>The returned node stream will be iterable several times if the
     * supplier returns a non-closed stream each time.
     *
     * @param streamSupplier A supplier for a stream of nodes
     * @param <T>            Type of nodes in the returned stream
     *
     * @return A new node stream
     */
    static <T extends Node> NodeStream<T> fromSupplier(Supplier<Stream<T>> streamSupplier) {
        return () -> streamSupplier.get().filter(Objects::nonNull);
    }


    /**
     * Returns a node stream containing all the elements of the given streams,
     * one after the other.
     *
     * @param <T>     The type of stream elements
     * @param streams the streams to flatten
     *
     * @return the concatenation of the input streams
     */
    @SafeVarargs
    static <T extends Node> NodeStream<T> union(NodeStream<? extends T>... streams) {
        return () -> Arrays.stream(streams).flatMap(NodeStream::toStream);
    }


    /**
     * Returns an empty node stream.
     *
     * @param <T> Expected type of nodes.
     *
     * @return An empty node stream
     */
    static <T extends Node> NodeStream<T> empty() {
        return StreamImpl.empty();
    }
}
