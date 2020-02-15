/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static java.util.Collections.emptyIterator;
import static net.sourceforge.pmd.internal.util.IteratorUtil.concat;
import static net.sourceforge.pmd.internal.util.IteratorUtil.iterate;
import static net.sourceforge.pmd.internal.util.IteratorUtil.singletonIterator;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a partial order on a type {@code <T>}. This ordering
 * generates a directed *acyclic* graph on instances of {@code <T>}.
 *
 * <p>This violates the contract of {@link Comparator} (total ordering)
 * so doesn't extend that interface. It couldn't be used in {@link Collections#sort(List, Comparator)} anyway.
 */
interface TopoOrder<T> {

    /**
     * TopoOrder on classes. A class's successors are its direct supertypes.
     */
    TopoOrder<Class<?>> TYPE_HIERARCHY_ORDERING = node -> {
        if (node == Object.class || node.isPrimitive()) {
            // Object
            return emptyIterator();
        }

        Class<?> superclass = node.getSuperclass();
        Iterator<Class<?>> iter = superclass != null ? singletonIterator(superclass)
                                                     : emptyIterator();

        iter = concat(iter, iterate(node.getInterfaces()));
        if (node.isInterface() && node.getInterfaces().length == 0) {
            iter = concat(iter, singletonIterator(Object.class));
        }

        return iter;
    };


    /** Returns all nodes that directly follow this node. */
    Iterator<T> directSuccessors(T node);


}
