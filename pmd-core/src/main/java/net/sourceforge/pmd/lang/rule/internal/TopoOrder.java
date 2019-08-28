/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.Arrays;
import java.util.stream.Stream;


public interface TopoOrder<T> {

    /** TopoOrder on types. */
    TopoOrder<Class<?>> TYPE_ORDER = node -> {
        if (node == Object.class) {
            // Object
            return Stream.empty();
        }

        Class<?> superclass = node.getSuperclass();
        Stream<Class<?>> stream = superclass != null ? Stream.of(superclass) : Stream.empty();

        stream = Stream.concat(stream, Arrays.stream(node.getInterfaces()));
        if (node.isInterface()) {
            stream = Stream.concat(stream, Stream.of(Object.class));
        }

        return stream;
    };


    Stream<T> strictParents(T node);


}
