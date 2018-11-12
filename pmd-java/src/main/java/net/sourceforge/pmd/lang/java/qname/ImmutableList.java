/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.qname;

import java.lang.ref.SoftReference;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;


/**
 * Classical immutable linked list representing an ordered collection.
 * This implementation has constant-time prepend, size, head and tail
 * operations. Most other operations are linear-time, but caching helps
 * reduce the overhead of some common operations (reverse). Allows
 * structural sharing of the tail, which makes many operations constant-
 * or zero-memory cost.
 *
 * <p>This implementation is designed for {@link JavaQualifiedName}, to
 * represent package and class hierarchies. It is not meant as a general-
 * purpose implementation.
 *
 * @param <E> Element type
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
interface ImmutableList<E> extends List<E> {
    /**
     * Adds an element at the beginning of this list.
     *
     * @param elem the element to prepend.
     *
     * @return a list which contains {@code elem} as first element and
     * which continues with this list.
     */
    ImmutableList<E> prepend(E elem);


    /**
     * Size of this list.
     */
    @Override
    int size();


    /**
     * Returns true if this list has no elements.
     */
    @Override
    boolean isEmpty();


    /**
     * Returns the first element of this list.
     *
     * @throws IndexOutOfBoundsException If this list is empty
     */
    E head();


    /**
     * Returns the list of the elements after the first.
     *
     * @throws IndexOutOfBoundsException If this list is empty
     */
    ImmutableList<E> tail();


    /**
     * Returns the ith element of the list.
     *
     * @param i Index of the element
     *
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    @Override
    E get(int i);


    /**
     * Returns a list with the same elements as this one, but ordered in reverse..
     */
    ImmutableList<E> reverse();


    /**
     * Returns a list of pairs, result of zipping the two lists together.
     * Eg {@code List(1,2,3).zip(List("a", "b", "c")) == List((1,"a"), (2,"b"), (3,"c"))}.
     *
     * <p>The resulting list has the length of the shortest input list.
     * Trailing elements of the longer list are discarded.
     *
     * @param right List providing the right pair elements
     * @param <T>   Type of the right list
     *
     * @return A list of pairs
     */
    <T> ImmutableList<Entry<E, T>> zip(ImmutableList<T> right);


    /**
     * Returns an iterator over the elements of this list.
     * It iterates from the head to the last element.
     */
    @Override
    Iterator<E> iterator();


    /**
     * Returns a mutable list containing the same elements as
     * this list. Changes made to the returned collection do not
     * affect this list.
     *
     * @return A list with the same elements as this list
     */
    List<E> toList();


    /**
     * Companion class providing factory methods for immutable lists.
     * Since we do not offer a general-purpose implementation, these
     * factories are package private.
     */
    final class ListFactory {
        /** Empty list instance. */
        private static final Nil<?> NIL = new Nil<>();


        private ListFactory() {

        }


        /**
         * Splits this string around matches of the given regular expression.
         * This method works as if by invoking the {@link String#split(String)}
         * method on both arguments, and converting the resulting array to a list.
         * IMPORTANT: the returned list's head points to the last match.
         *
         * @param input input string on which to match
         * @param regex the delimiting regular expression
         *
         * @return A list of strings computed by splitting the input string
         * around matches of the given regular expression
         *
         * @see String#split(String)
         */
        static ImmutableList<String> split(String input, String regex) {
            return split(input, regex, 0);
        }


        /**
         * Splits this string around matches of the given regular expression.
         * This method works as if by invoking the {@link String#split(String, int)}
         * method on the arguments, and converting the resulting array to a list.
         * IMPORTANT: The returned list's head points to the last match.
         *
         * @param input input string on which to match
         * @param regex the delimiting regular expression
         * @param limit maximum number of matches
         *
         * @return A list of strings computed by splitting the input string
         * around matches of the given regular expression
         *
         * @see String#split(String, int)
         */
        static ImmutableList<String> split(String input, String regex, int limit) {
            // this was adapted from Pattern.split
            int index = 0;
            boolean matchLimited = limit > 0;
            ImmutableList<String> matchList = emptyList();
            Matcher m = Pattern.compile(regex).matcher(input);

            // Add segments before each match found
            while (m.find()) {
                if (!matchLimited || matchList.size() < limit - 1) {
                    if (index == 0 && index == m.start() && m.start() == m.end()) {
                        // no empty leading substring included for zero-width match
                        // at the beginning of the input char sequence.
                        continue;
                    }
                    String match = input.subSequence(index, m.start()).toString();
                    matchList = matchList.prepend(match);
                    index = m.end();
                } else if (matchList.size() == limit - 1) { // last one
                    String match = input.subSequence(index,
                                                     input.length()).toString();
                    matchList = matchList.prepend(match);
                    index = m.end();
                }
            }

            // If no match was found, return the input
            if (index == 0) {
                return make(input);
            }

            // Add remaining segment
            if (!matchLimited || matchList.size() < limit) {
                matchList = matchList.prepend(input.subSequence(index, input.length()).toString());
            }

            // Eliminate the trailing empty strings
            if (limit == 0) {
                while (!matchList.isEmpty() && matchList.head().equals("")) {
                    matchList = matchList.tail();
                }
            }
            return matchList;
        }


        /**
         * Returns an empty immutable list.
         *
         * @param <E> The argument type
         *
         * @return An empty list
         */
        @SuppressWarnings("unchecked")
        static <E> ImmutableList<E> emptyList() {
            return (ImmutableList<E>) NIL;
        }


        /**
         * Returns an empty list. In all cases behaves like {@link #emptyList()},
         * but is an overload of {@link #make(Object[])} for convenience.
         *
         * @param <E> Element type
         *
         * @return An empty list
         */
        static <E> ImmutableList<E> make() {
            return emptyList();
        }


        private static <E> ImmutableList<E> fromArray(E[] arr) {
            ImmutableList<E> cur = emptyList();
            if (arr != null) {
                for (E item : arr) {
                    cur = cur.prepend(item);
                }
            }
            return cur;
        }


        /**
         * Creates and returns a list containing the given elements.
         * The returned list's head is the first parameter.
         *
         * @param elems Elements of the list in order
         * @param <E>   Element type
         *
         * @return A list containing the given elements.
         */
        @SafeVarargs
        static <E> ImmutableList<E> make(E... elems) {
            return fromArray(elems);
        }


        /**
         * Abstract implementation.
         *
         * @param <E> Element type
         */
        private abstract static class AbstractImmutableList<E> implements ImmutableList<E> {

            @Override
            public boolean add(E e) {
                throw new UnsupportedOperationException();
            }


            @Override
            public boolean addAll(Collection<? extends E> c) {
                throw new UnsupportedOperationException();
            }


            @Override
            public boolean addAll(int index, Collection<? extends E> c) {
                throw new UnsupportedOperationException();
            }


            @Override
            public void add(int index, E element) {
                throw new UnsupportedOperationException();
            }


            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }


            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }


            @Override
            public E remove(int index) {
                throw new UnsupportedOperationException();
            }


            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }


            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }


            @Override
            public ListIterator<E> listIterator() {
                throw new UnsupportedOperationException();
            }


            @Override
            public ListIterator<E> listIterator(int index) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return toList().toArray(a);
            }


            @Override
            public Object[] toArray() {
                return toList().toArray();
            }

            @Override
            public final AbstractImmutableList<E> prepend(E elem) {
                return new ListNode<>(elem, this);
            }


            @Override
            public E set(int index, E element) {
                throw new UnsupportedOperationException();
            }


            @Override
            public List<E> subList(int fromIndex, int toIndex) {
                throw new UnsupportedOperationException();
            }


            @Override
            public final <T> ImmutableList<Entry<E, T>> zip(ImmutableList<T> right) {
                Iterator<E> thisIt = this.iterator();
                Iterator<T> rightIt = right.iterator();
                ImmutableList<Entry<E, T>> result = emptyList();
                while (thisIt.hasNext() && rightIt.hasNext()) {
                    result = result.prepend(new SimpleImmutableEntry<>(thisIt.next(), rightIt.next()));
                }
                return result.reverse();
            }
        }

        /**
         * Empty list. Uses reference equality since there's only one instance around.
         *
         * @param <E> Element type
         */
        private static class Nil<E> extends AbstractImmutableList<E> {

            /**
             * You should not use that, use {@link #emptyList()}.
             */
            Nil() {
                // There should only be one instance of that
            }

            @Override
            public Iterator<E> iterator() {
                return Collections.emptyIterator();
            }


            @Override
            public E get(int i) {
                throw new IndexOutOfBoundsException("Empty list!");
            }


            @Override
            public int size() {
                return 0;
            }


            @Override
            public boolean isEmpty() {
                return true;
            }


            @Override
            public E head() {
                throw new IndexOutOfBoundsException("Empty list!");
            }


            @Override
            public ImmutableList<E> tail() {
                throw new IndexOutOfBoundsException("Empty list!");
            }


            @Override
            public ImmutableList<E> reverse() {
                return this;
            }


            @Override
            public boolean contains(Object item) {
                return false;
            }


            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }


            @Override
            public int indexOf(Object o) {
                return -1;
            }


            @Override
            public int lastIndexOf(Object o) {
                return -1;
            }


            @Override
            public String toString() {
                return "List()";
            }


            @Override
            public List<E> toList() {
                return Collections.emptyList();
            }
        }

        /**
         * Non-empty list.
         *
         * @param <E> Element type
         */
        private static class ListNode<E> extends AbstractImmutableList<E> {
            private final E head;
            private final ImmutableList<E> tail;
            private final int size;
            private SoftReference<ImmutableList<E>> reverseCache;


            private ListNode(E head, AbstractImmutableList<E> tail) {
                this.head = head;
                this.tail = tail;
                this.size = tail.isEmpty() ? 1 : tail.size() + 1;
            }


            @Override
            public int size() {
                return size;
            }


            @Override
            public boolean isEmpty() {
                return false;
            }


            @Override
            public E head() {
                return head;
            }


            @Override
            public ImmutableList<E> tail() {
                return tail;
            }


            @Override
            public E get(int i) {
                if (i < 0 || i > size()) {
                    throw new IndexOutOfBoundsException();
                }

                return i == 0 ? head() : tail().get(i - 1);
            }


            @Override
            public Iterator<E> iterator() {
                return new NodeIterator<>(this);
            }


            @Override
            public ImmutableList<E> reverse() {
                if (reverseCache == null || reverseCache.get() == null) {
                    ImmutableList<E> rev = buildReverse();
                    ((ListNode<E>) rev).reverseCache = new SoftReference<ImmutableList<E>>(this);
                    reverseCache = new SoftReference<>(rev);
                }

                return reverseCache.get();
            }


            private ImmutableList<E> buildReverse() {
                ImmutableList<E> cur = emptyList();
                for (E item : this) {
                    cur = cur.prepend(item);
                }
                return cur;
            }


            @Override
            public List<E> toList() {
                List<E> result = new ArrayList<>(size());
                for (E item : this) {
                    result.add(item);
                }
                return result;
            }


            @Override
            public boolean contains(Object o) {
                return Objects.equals(head(), o) || tail.contains(o);
            }


            @Override
            public boolean containsAll(Collection<?> c) {
                for (Object o : c) {
                    if (!contains(o)) {
                        return false;
                    }
                }
                return true;
            }


            @Override
            public int indexOf(Object o) {
                int i = 0;
                for (E e : this) {
                    if (Objects.equals(e, o)) {
                        return i;
                    }
                    i++;
                }
                return -1;
            }


            @Override
            public int lastIndexOf(Object o) {
                int i = reverse().indexOf(o);
                return i < 0 ? i : size() - i - 1;
            }


            @Override
            public String toString() {
                if (size() == 1) {
                    return "List(" + head() + ")";
                }

                StringBuilder sb = new StringBuilder("List(").append(head());
                for (E elem : tail()) {
                    sb.append(", ").append(elem);
                }
                return sb.append(")").toString();
            }


            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                ListNode<?> listNode = (ListNode<?>) o;
                return size == listNode.size
                        && Objects.equals(head, listNode.head)
                        && Objects.equals(tail, listNode.tail);
            }


            @Override
            public int hashCode() {
                return Objects.hash(head, tail, size);
            }


            private static class NodeIterator<E> implements Iterator<E> {
                ImmutableList<E> current;


                NodeIterator(ImmutableList<E> start) {
                    current = start;
                }


                @Override
                public boolean hasNext() {
                    return !current.isEmpty();
                }


                @Override
                public E next() {
                    E head = current.head();
                    current = current.tail();
                    return head;
                }


                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            }
        }
    }
}
