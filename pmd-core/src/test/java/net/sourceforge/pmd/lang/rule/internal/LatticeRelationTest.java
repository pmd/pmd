/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author Clément Fournier
 */
public class LatticeRelationTest {

    @Test
    public void testLattice() {

        LatticeRelation<Class<?>, List<String>> lattice = new LatticeRelation<>(Monoid.forList(), TopoOrder.TYPE_HIERARCHY_ORDERING);


        lattice.put(String.class, Collections.singletonList("string"));
        lattice.put(Integer.class, Collections.singletonList("int"));
        lattice.put(Long.class, Collections.singletonList("long"));

        lattice.freeze();

        assertEquals(listOf("string", "int", "long"), lattice.get(Object.class));

        Map<Class<?>, LatticeRelation<Class<?>, List<String>>.LNode> nodes = lattice.getNodes();

        assertTrue(nodes.get(Object.class).hasDiamond);
        assertFalse(nodes.get(Serializable.class).hasDiamond);
        assertFalse(nodes.get(Comparable.class).hasDiamond);
    }

}
