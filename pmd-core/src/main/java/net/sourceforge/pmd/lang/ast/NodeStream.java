/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.internal.StreamImpl;


/**
 * A sequence of AST nodes. Conceptually similar to a {@link Stream},
 * and exposes a specialized API to navigate abstract syntax trees.
 * This API replaces the defunct {@link Node#findChildNodesWithXPath(String)}.
 *
 * <h1>API usage</h1>
 *
 * <p>The {@link Node} interface exposes methods like {@link Node#children()}
 * or {@link Node#asStream()} to obtain new NodeStreams. Null-safe construction
 * methods are available here, see {@link #of(Node)}, {@link #of(Node[])},
 * {@link #fromIterable(Iterable)}.
 *
 * <p>Most functions have an equivalent in the {@link Stream} interface
 * and their behaviour is similar. One important departure from the
 * {@link Stream} contract is the absence of requirement on the laziness
 * of pipeline operations. More on that in the details section below.
 *
 * <p>Some additional functions are provided to iterate the axes of the
 * tree: {@link #children()}, {@link #descendants()}, {@link #descendantsOrSelf()},
 * {@link #parents()}, {@link #ancestors()}, {@link #ancestorsOrSelf()},
 * {@link #precedingSiblings()}, {@link #followingSiblings()}.
 * Filtering and mapping nodes by type is possible through {@link #filterIs(Class)},
 * and the specialized {@link #children(Class)}, {@link #descendants(Class)},
 * and {@link #ancestors(Class)}.
 *
 * <p>Many complex predicates about nodes can be expressed by testing
 * the emptiness of a node stream. E.g. the following tests if the node
 * is a variable declarator id initialized to the value {@code 0}:
 * <pre>
 *     {@linkplain #of(Node) NodeStream.of}(someNode)                           <i>// the stream here is empty if the node is null</i>
 *               {@linkplain #filterIs(Class) .filterIs}(ASTVariableDeclaratorId.class)<i>// the stream here is empty if the node was not a variable declarator id</i>
 *               {@linkplain #followingSiblings() .followingSiblings}()                    <i>// the stream here contains only the siblings, not the original node</i>
 *               {@linkplain #take(int) .take}(1)                                <i>// the stream here contains only the first sibling, if it exists</i>
 *               {@linkplain #filterIs(Class) .filterIs}(ASTNumericLiteral.class)
 *               {@linkplain #filter(Predicate) .filter}(it -&gt; !it.isFloatingPoint() &amp;&amp; it.getValueAsInt() == 0)
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
 * <li><tt>node.{@link Node#hasDescendantOfType(Class) hasDescendantOfType(t)} === node.{@link Node#descendants(Class) descendants(t)}.{@link #nonEmpty()}</tt></li>
 * <li><tt>node.getFirstParentOfAnyType(c1, c2) ===  node.{@link Node#ancestors() ancestors()}.{@link #firstNonNull(Function) firstNonNull}({@link #asInstanceOf(Class, Class[]) asInstanceOf(c1, c2)})</tt></li>
 * <li><tt>node.hasDescendantOfAnyType(c1, c2) ===  node.{@link Node#descendants() descendants()}.{@link #map(Function) map}({@link #asInstanceOf(Class, Class[]) asInstanceOf(c1, c2)}).{@link #nonEmpty()}</tt></li>
 * </ul>
 * The new way to write those is as efficient as the old way.
 *
 * <p>Unlike {@link Stream}s, NodeStreams can be iterated multiple times. That means, that the operations
 * that are <i>terminal</i> in the Stream interface (i.e. consume the stream) don't consume NodeStreams.
 * Be aware though, that node streams don't cache their results by default, so e.g. calling {@link #count()}
 * followed by {@link #toList()} will execute the whole pipeline twice. The elements of a stream can
 * however be {@linkplain #cached() cached} at an arbitrary point in the pipeline to evaluate the
 * upstream only once. Some construction methods allow building a node stream from an external data
 * source, e.g. {@link #fromIterable(Iterable) fromIterable}.
 * Depending on how the data source is implemented, the built node streams may be iterable only once.
 *
 * <p>Node streams may contain duplicates, which can be pruned with {@link #distinct()}.
 *
 * <h1>Details</h1>
 *
 * <p>NodeStreams are not necessarily implemented with {@link Stream}, but
 * when a method has an equivalent in the {@link Stream} API, their
 * contract is similar. The only difference, is that node streams are not
 * necessarily lazy, ie, a pipeline operation may be evaluated eagerly
 * to improve performance. For this reason, relying on side-effects
 * produced in the middle of the pipeline is a bad idea. {@link Stream}
 * gives the same guideline about statefulness, but not for the same reason.
 * Their justification is parallelism and operation reordering, once
 * the pipeline is fully known.
 *
 * <p>Node streams are meant to be sequential streams, so there is no
 * equivalent to {@link Stream#findAny()}. The method {@link #first()}
 * is an equivalent to {@link Stream#findFirst()}. There is however a
 * {@link #last()} method, which may be implemented efficiently on some
 * streams (eg {@link #children()}). TODO maybe implement reverse
 *
 * <p>Node streams are most of the time ordered in document order (w.r.t. the XPath specification),
 * a.k.a. prefix order. Some operations which explicitly manipulate the order of nodes, like
 * {@link #union(NodeStream[]) union} or {@link #append(NodeStream) append}, may not preserve that ordering.
 * {@link #map(Function) map} and {@link #flatMap(Function) flatMap} operations may not preserve the ordering
 * if the stream has more than one element, since the mapping is applied in order to each element
 * of the receiver stream. This extends to methods defined in terms of map or flatMap, e.g.
 * {@link #descendants()} or {@link #children()}.
 *
 * @param <T> Type of nodes this stream contains. This parameter is
 *           covariant, which means for maximum flexibility, methods
 *           taking a node stream argument should declare it with an
 *           "extends" wildcard.
 *
 * @author Clément Fournier
 * @implNote Choosing to wrap a stream instead of extending the interface is to
 * allow the functions to return NodeStreams, and to avoid the code bloat
 * induced by delegation.
 *
 * <p>The default implementation relies on the iterator method. From benchmarking,
 * that appears more efficient than streams.
 *
 * @since 7.0.0
 */
public interface NodeStream<@NonNull T extends Node> extends Iterable<@NonNull T> {

    /**
     * Returns a node stream consisting of the results of replacing each
     * node of this stream with the contents of a stream produced by the
     * given mapping function. If a mapped stream is null, it is discarded.
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
    <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends @Nullable NodeStream<? extends R>> mapper);

    // lazy pipeline transformations


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
    <R extends Node> NodeStream<R> map(Function<? super T, ? extends @Nullable R> mapper);


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
    NodeStream<T> filter(Predicate<? super @NonNull T> predicate);


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
    NodeStream<T> peek(Consumer<? super @NonNull T> action);



    /**
     * Returns a new node stream that contains all the elements of this stream, then
     * all the elements of the given stream.
     *
     * @param right Other stream
     *
     * @return A concatenated stream
     *
     * @see #union(NodeStream[])
     */
    NodeStream<T> append(NodeStream<? extends T> right);


    /**
     * Returns a new node stream that contains all the elements of the given stream,
     * then all the elements of this stream.
     *
     * @param right Other stream
     *
     * @return A concatenated stream
     *
     * @see #union(NodeStream[])
     */
    NodeStream<T> prepend(NodeStream<? extends T> right);


    /**
     * Returns a node stream containing all the elements of this node stream,
     * but which will evaluate the upstream pipeline only once. The returned
     * stream is not necessarily lazy, which means it may evaluate the upstream
     * pipeline as soon as the call to this method is made.
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
    NodeStream<T> cached();


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
    NodeStream<T> take(int maxSize);


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
     * @see #dropLast(int)
     */
    NodeStream<T> drop(int n);

    /**
     * Returns a stream consisting of the elements of this stream except
     * the n tail elements. If n is greater than the number of elements
     * of this stream, returns an empty stream. This requires a lookahead
     * buffer in general.
     *
     * @param n the number of trailing elements to skip
     *
     * @return A new node stream
     *
     * @throws IllegalArgumentException if n is negative
     * @see #drop(int)
     */
    NodeStream<T> dropLast(int n);


    /**
     * Returns the longest prefix of elements that satisfy the given predicate.
     *
     * @param predicate The predicate used to test elements.
     *
     * @return the longest prefix of this stream whose elements all satisfy
     *     the predicate.
     */
    NodeStream<T> takeWhile(Predicate<? super T> predicate);


    /**
     * Returns a stream consisting of the distinct elements (w.r.t
     * {@link Object#equals(Object)}) of this stream.
     *
     * @return a stream consisting of the distinct elements of this stream
     */
    NodeStream<T> distinct();

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
     * <p>This is equivalent to {@code map(Node::getParent)}.
     *
     * @return A stream of parents
     *
     * @see #ancestors()
     * @see #ancestorsOrSelf()
     */
    default NodeStream<Node> parents() {
        return map(Node::getParent);
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
     * contained in this stream. See {@link DescendantNodeStream} for details.
     *
     * <p>This is equivalent to {@code flatMap(Node::descendants)}, except
     * the returned stream is a {@link DescendantNodeStream}.
     *
     * @return A stream of descendants
     *
     * @see Node#descendants()
     * @see #descendants(Class)
     * @see #descendantsOrSelf()
     */
    DescendantNodeStream<Node> descendants();


    /**
     * Returns a node stream containing the nodes contained in this stream and their descendants.
     * See {@link DescendantNodeStream} for details.
     *
     * <p>This is equivalent to {@code flatMap(Node::descendantsOrSelf)}, except
     * the returned stream is a {@link DescendantNodeStream}.
     *
     * @return A stream of descendants
     *
     * @see Node#descendantsOrSelf()
     * @see #descendants()
     */
    DescendantNodeStream<Node> descendantsOrSelf();


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
    default <R extends Node> NodeStream<R> children(Class<? extends R> rClass) {
        return flatMap(it -> it.children(rClass));
    }

    /**
     * Returns a stream containing the first child of each of the nodes
     * in this stream that has the given type.
     *
     * <p>This is equivalent to {@code flatMap(it -> it.children(rClass).take(1))}.
     *
     * @param rClass Type of node the returned stream should contain
     * @param <R>    Type of node the returned stream should contain
     *
     * @return A new node stream
     *
     * @see Node#children(Class)
     */
    default <R extends Node> NodeStream<R> firstChild(Class<? extends R> rClass) {
        return flatMap(it -> it.children(rClass).take(1));
    }

    /**
     * Returns the {@linkplain #descendants() descendant stream} of each node
     * in this stream, filtered by the given node type. See {@link DescendantNodeStream}
     * for details.
     *
     * <p>This is equivalent to {@code descendants().filterIs(rClass)}, except
     * the returned stream is a {@link DescendantNodeStream}.
     *
     * @param rClass Type of node the returned stream should contain
     * @param <R>    Type of node the returned stream should contain
     *
     * @return A new node stream
     *
     * @see #filterIs(Class)
     * @see Node#descendants(Class)
     */
    <R extends Node> DescendantNodeStream<R> descendants(Class<? extends R> rClass);


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
    default <R extends Node> NodeStream<R> ancestors(Class<? extends R> rClass) {
        return flatMap(it -> it.ancestors(rClass));
    }


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
    default NodeStream<T> filterNot(Predicate<? super @NonNull T> predicate) {
        return filter(predicate.negate());
    }

    // these are shorthands defined relative to filter


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
    default <U> NodeStream<T> filterMatching(Function<? super @NonNull T, ? extends @Nullable U> extractor, U comparand) {
        return filter(t -> Objects.equals(extractor.apply(t), comparand));
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
     * @see #asInstanceOf(Class, Class[])
     */
    @SuppressWarnings("unchecked")
    default <R extends Node> NodeStream<R> filterIs(Class<? extends R> rClass) {
        return (NodeStream<R>) filter(rClass::isInstance);
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
    default <U> NodeStream<T> filterNotMatching(Function<? super @NonNull T, ? extends @Nullable U> extractor, U comparand) {
        return filter(t -> !Objects.equals(extractor.apply(t), comparand));
    }


    // "terminal" operations


    @Override
    void forEach(Consumer<? super @NonNull T> action);


    /**
     * Reduce the elements of this stream sequentially.
     *
     * @param identity   Identity element
     * @param accumulate Combine an intermediate result with a new node from this stream,
     *                   returns the next intermediate result
     * @param <R>        Result type
     *
     * @return The last intermediate result (identity if this stream is empty)
     */
    default <R> R reduce(R identity, BiFunction<? super R, ? super T, ? extends R> accumulate) {
        R result = identity;
        for (T node : this) {
            result = accumulate.apply(result, node);
        }
        return result;
    }

    /**
     * Sum the elements of this stream by associating them to an integer.
     *
     * @param toInt Map an element to an integer, which will be added
     *              to the running sum
     *              returns the next intermediate result
     *
     * @return The sum, zero if the stream is empty.
     */
    default int sumBy(ToIntFunction<? super T> toInt) {
        int result = 0;
        for (T node : this) {
            result += toInt.applyAsInt(node);
        }
        return result;
    }


    /**
     * Returns the number of nodes in this stream.
     *
     * @return the number of nodes in this stream
     */
    // ASTs are not so big as to warrant using a 'long' here
    int count();

    /**
     * Returns the sum of the value of the function applied to all
     * elements of this stream.
     *
     * @param intMapper Mapping function
     *
     * @return The sum
     */
    default int sumByInt(ToIntFunction<? super T> intMapper) {
        int sum = 0;
        for (T item : this) {
            sum += intMapper.applyAsInt(item);
        }
        return sum;
    }


    /**
     * Returns 'true' if the stream has at least one element.
     *
     * @return 'true' if the stream has at least one element.
     *
     * @see #isEmpty()
     */
    boolean nonEmpty();


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
    boolean any(Predicate<? super T> predicate);


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
    boolean none(Predicate<? super T> predicate);


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
    boolean all(Predicate<? super T> predicate);


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
     * <p>If you'd rather continue processing the first element as a node
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
    @Nullable T first();


    /**
     * Returns the first element of this stream, or throws a {@link NoSuchElementException}
     * if the stream is empty.
     *
     * @return the first element of this stream
     *
     * @see #first(Predicate)
     * @see #first(Class)
     * @see #firstOpt()
     */
    @NonNull
    default T firstOrThrow() {
        T first = first();
        if (first == null) {
            throw new NoSuchElementException("Empty node stream");
        }
        return first;
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
    default <R extends Node> @Nullable R first(Class<? extends R> rClass) {
        return filterIs(rClass).first();
    }


    /**
     * Returns the first element of this stream for which the mapping function
     * returns a non-null result. Returns null if there is no such element.
     * This is a convenience method to use with {@link #asInstanceOf(Class, Class[])},
     * because using just {@link #map(Function) map} followed by {@link #first()}
     * will lose the type information and mentioning explicit type arguments
     * would be needed.
     *
     * @param nullableFun Mapper function
     * @param <R>         Result type
     *
     * @return A node, or null
     *
     * @see #asInstanceOf(Class, Class[])
     */
    default <R extends Node> @Nullable R firstNonNull(Function<? super @NonNull T, ? extends @Nullable R> nullableFun) {
        return map(nullableFun).first();
    }


    /**
     * Returns the last element of this stream, or {@code null} if the
     * stream is empty. This may or may not require traversing all the
     * elements of the stream.
     *
     * @return the last element of this stream, or {@code null} if it doesn't exist
     */
    @Nullable T last();


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
    default <R extends Node> @Nullable R last(Class<? extends R> rClass) {
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
    <R, A> R collect(Collector<? super T, A, R> collector);


    /**
     * Returns a new stream of Ts having the pipeline of operations
     * defined by this node stream. This can be called multiple times.
     *
     * @return A stream containing the same elements as this node stream
     */
    Stream<@NonNull T> toStream();


    /**
     * Collects the elements of this node stream into a list. Just like
     * for {@link Collectors#toList()}, there are no guarantees on the
     * type, mutability, serializability, or thread-safety of the returned
     * list.
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
    static <T extends Node> NodeStream<T> of(@Nullable T node) {
        // overload the varargs to avoid useless array creation
        return node == null ? empty() : StreamImpl.singleton(node);
    }


    // construction
    // we ensure here that no node stream may contain null values


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
    static <T extends Node> NodeStream<T> fromIterable(Iterable<? extends @Nullable T> iterable) {
        return StreamImpl.fromIterable(iterable);
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
    static <T extends Node> NodeStream<T> ofOptional(Optional<? extends T> optNode) {
        return optNode.map(StreamImpl::<T>singleton).orElseGet(StreamImpl::empty);
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
        return fromIterable(Arrays.asList(nodes));
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
        return union(Arrays.asList(streams));
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
    static <T extends Node> NodeStream<T> union(Iterable<? extends NodeStream<? extends T>> streams) {
        return StreamImpl.union(streams);
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


    /**
     * Applies the given mapping functions to the given upstream in order and merges the
     * results into a new node stream. This allows exploring several paths at once on the
     * same stream. The method is lazy and won't evaluate the upstream pipeline several times.
     *
     * @param upstream Source of the stream
     * @param fst      First mapper
     * @param snd      Second mapper
     * @param rest     Rest of the mappers
     * @param <R>      Common supertype for the element type of the streams returned by the mapping functions
     *
     * @return A merged node stream
     */
    @SafeVarargs // this method is static because of the generic varargs
    static <T extends Node, R extends Node> NodeStream<R> forkJoin(NodeStream<? extends T> upstream,
                                                                   Function<? super @NonNull T, ? extends NodeStream<? extends R>> fst,
                                                                   Function<? super @NonNull T, ? extends NodeStream<? extends R>> snd,
                                                                   Function<? super @NonNull T, ? extends NodeStream<? extends R>>... rest) {
        Objects.requireNonNull(fst);
        Objects.requireNonNull(snd);

        List<Function<? super T, ? extends NodeStream<? extends R>>> mappers = new ArrayList<>(rest.length + 2);
        mappers.add(fst);
        mappers.add(snd);
        mappers.addAll(Arrays.asList(rest));

        Function<? super T, NodeStream<R>> aggregate =
            t -> NodeStream.<R>union(mappers.stream().map(f -> f.apply(t)).collect(Collectors.toList()));

        // with forkJoin we know that the stream will be iterated more than twice so we cache the values
        return upstream.cached().flatMap(aggregate);
    }


    /**
     * Returns a map function, that checks whether the parameter is an
     * instance of any of the given classes. If so, it returns the parameter,
     * otherwise it returns null.
     *
     * <p>This may be used to filter a node stream to those specific
     * classes, for example:
     *
     * <pre>{@code
     *     NodeStream<ASTExpression> exprs = someStream.map(asInstanceOf(ASTInfixExpression.class, ASTCastExpression.class));
     * }</pre>
     *
     * Using this in the middle of a call chain might require passing
     * explicit type arguments:
     *
     * <pre>{@code
     *    ASTAnyTypeDeclaration ts =
     *       node.ancestors()
     *           .<ASTAnyTypeDeclaration>map(asInstanceOf(ASTClassOrInterfaceDeclaration.class, ASTEnumDeclaration.class))
     *           .first(); // would not compile without the explicit type arguments
     * }</pre>
     *
     * <p>For this use case the {@link #firstNonNull(Function)} method
     * may be used, which reduces the above to
     *
     * <pre>{@code
     *    ASTAnyTypeDeclaration ts =
     *       node.ancestors().firstNonNull(asInstanceOf(ASTClassOrInterfaceDeclaration.class, ASTEnumDeclaration.class));
     * }</pre>
     *
     * @param c1   First type to test
     * @param rest Other types to test
     * @param <O>  Output type
     *
     * @see #firstNonNull(Function)
     */
    @SafeVarargs // this method is static because of the generic varargs
    @SuppressWarnings("unchecked")
    static <O> Function<@Nullable Object, @Nullable O> asInstanceOf(Class<? extends O> c1, Class<? extends O>... rest) {
        if (rest.length == 0) {
            return obj -> c1.isInstance(obj) ? (O) obj : null;
        }
        return obj -> {
            if (c1.isInstance(obj)) {
                return (O) obj;
            }

            for (Class<? extends O> aClass : rest) {
                if (aClass.isInstance(obj)) {
                    return (O) obj;
                }
            }
            return null;
        };
    }


    /**
     * A specialization of {@link NodeStream} that allows configuring
     * tree traversal behaviour when traversing the descendants of a node.
     * Such a stream is returned by methods such as {@link Node#descendants()}.
     * When those methods are called on a stream containing more than one
     * element (eg {@link NodeStream#descendants()}), the configuration
     * applies to each individual traversal.
     *
     * <p>By default, traversal is performed depth-first (prefix order). Eg
     * <pre>{@code
     * A
     * + B
     *   + C
     *   + D
     * + E
     *   + F
     * }</pre>
     * is traversed in the order {@code A, B, C, D, E, F}.
     *
     * <p>By default, traversal also does not cross {@linkplain #crossFindBoundaries(boolean) find boundaries}.
     *
     * @param <T> Type of node this stream contains
     */
    interface DescendantNodeStream<T extends Node> extends NodeStream<T> {

        // TODO stop recursion on an arbitrary boundary
        // TODO breadth-first traversal


        /**
         * Returns a node stream that will not stop the tree traversal
         * when encountering a find boundary. Find boundaries are node
         * that by default stop tree traversals, like class declarations.
         * They are identified via {@link Node#isFindBoundary()}.
         *
         * <p>For example, supposing you have the AST node for the following
         * method:
         * <pre>{@code
         *  void method() {
         *    String outer = "before";
         *
         *    class Local {
         *      void localMethod() {
         *        String local = "local";
         *      }
         *    }
         *
         *    String after = "after";
         *  }
         * }</pre>
         * Then the stream {@code method.descendants(ASTStringLiteral.class)}
         * will only yield the literals {@code "before"} and {@code "after"},
         * because the traversal doesn't go below the local class.
         *
         * <p>Note that traversal is stopped only for the subtree of the
         * find boundary, but continues on the siblings. This is why
         * {@code "after"} is yielded. This is also why {@link #takeWhile(Predicate)}
         * is not a substitute for this method: {@code method.descendants(ASTStringLiteral.class).takeWhile(it -> !it.isFindBoundary)}
         * would yield only {@code "before"}.
         *
         * <p>This behaviour can be opted out of with this method. In the
         * example, the stream {@code method.descendants(ASTStringLiteral.class).crossFindBoundaries()}
         * will yield {@code "before"}, {@code "local"} and {@code "after"}
         * literals.
         *
         * @param cross If true, boundaries will be crossed.
         *
         * @return A new node stream
         */
        DescendantNodeStream<T> crossFindBoundaries(boolean cross);


        /**
         * An alias for {@link #crossFindBoundaries(boolean) crossFindBoundaries(true)}.
         *
         * @return A new node stream
         */
        default DescendantNodeStream<T> crossFindBoundaries() {
            return crossFindBoundaries(true);
        }

    }


}
