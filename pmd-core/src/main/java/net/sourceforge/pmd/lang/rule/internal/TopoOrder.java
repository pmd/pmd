/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static java.util.Collections.emptyIterator;
import static net.sourceforge.pmd.internal.util.IteratorUtil.concat;
import static net.sourceforge.pmd.internal.util.IteratorUtil.iterate;
import static net.sourceforge.pmd.internal.util.IteratorUtil.singletonIterator;

import java.util.Iterator;

/**
 * Represents a partial order on a type {@code <T>}. This is used to
 * generate the internal data structure of {@link LatticeRelation}s.
 */
interface TopoOrder<T> {

    /**
     * Partial order on classes. The direct successors of a class are
     * its direct supertypes.
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


    /**
     * Returns all nodes that directly follow this node.
     * The returned nodes may be pruned by the key selector of
     * the lattice. Successive invocation of this method must
     * at some point terminate.
     */
    Iterator<T> directSuccessors(T node);


}
