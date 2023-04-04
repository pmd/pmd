/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a partial order on a type {@code <K>}. This is used to
 * generate the internal data structure of {@link LatticeRelation}s.
 */
interface TopoOrder<K> {

    /**
     * Partial order on classes. The direct successors of a class are
     * its direct supertypes.
     */
    TopoOrder<Class<?>> TYPE_HIERARCHY_ORDERING = node -> {
        if (node == Object.class || node.isPrimitive()) {
            // Object
            return Collections.emptyList();
        }

        List<Class<?>> succs = new ArrayList<>();


        Class<?> superclass = node.getSuperclass();
        if (superclass != null) {
            succs.add(superclass);
        }
        Collections.addAll(succs, node.getInterfaces());

        if (node.isInterface() && node.getInterfaces().length == 0) {
            succs.add(Object.class);
        }

        return succs;
    };


    /**
     * Returns the strict direct successors of the given value.
     * Successive invocation of this method must at some point
     * terminate, also each invocation must yield the same result
     * for the same argument.
     */
    Iterable<K> directSuccessors(K key);


}
