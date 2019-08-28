/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Represents a partial order on a type {@code <T>}. This ordering
 * generates a directed *acyclic* graph on instances of {@code <T>}.
 */
interface TopoOrder<T> {

    /** TopoOrder on classes. A class's successors are its direct supertypes. */
    TopoOrder<Class<?>> TYPE_HIERARCHY_ORDERING = node -> {
        if (node == Object.class) {
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
    };


    /** Returns all nodes that strictly follow this node. */
    Stream<T> directSuccessors(T node);


}
