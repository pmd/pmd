/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Represents a partial order on a type {@code <T>}. This ordering
 * generates a directed *acyclic* graph on instances of {@code <T>}.
 */
interface TopoOrder<T> extends Comparator<T> {

    /** TopoOrder on classes. A class's successors are its direct supertypes. */
    TopoOrder<Class<?>> TYPE_HIERARCHY_ORDERING = new TopoOrder<Class<?>>() {
        @Override
        public boolean isDefined(Class<?> l, Class<?> r) {
            return l == r || l.isAssignableFrom(r) || r.isAssignableFrom(l);
        }

        @Override
        public Stream<Class<?>> directSuccessors(Class<?> node) {
            if (node == Object.class || node.isPrimitive()) {
                // Object
                return Stream.empty();
            }

            Class<?> superclass = node.getSuperclass();
            Stream<Class<?>> stream = superclass != null ? Stream.of(superclass)
                                                         : Stream.empty();

            stream = Stream.concat(stream, Arrays.stream(node.getInterfaces()));
            if (node.isInterface() && node.getInterfaces().length == 0) {
                stream = Stream.concat(stream, Stream.of(Object.class));
            }

            return stream;
        }

        @Override
        public int compare(Class<?> l, Class<?> r) {
            if (l.equals(r)) {
                return 0;
            }

            if (l.isAssignableFrom(r)) {
                // r <: l
                return 1;
            } else if (r.isAssignableFrom(l)) {
                // l <: r
                return -1;
            }

            return 0; // if the ordering is undefined, prefer stability
        }
    };


    boolean isDefined(T l, T r);


    /** Returns all nodes that strictly follow this node. */
    Stream<T> directSuccessors(T node);


}
