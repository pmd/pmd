/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * @author Clément Fournier
 */
public class LatticeRelationTest {

    @Test
    public void testLattice() {

        LatticeRelation<Class<?>, Set<String>> lattice = new LatticeRelation<>(Monoid.forSet(), TopoOrder.TYPE_HIERARCHY_ORDERING);


        lattice.put(String.class, setOf("string"));
        lattice.put(Integer.class, setOf("int"));
        lattice.put(Long.class, setOf("long"));

        lattice.freezeTopo();

        assertEquals(setOf("string", "int", "long"), lattice.get(Object.class));

        Map<Class<?>, LatticeRelation<Class<?>, Set<String>>.LNode> nodes = lattice.getNodes();

        assertTrue(nodes.get(Object.class).hasDiamond);
        assertFalse(nodes.get(Serializable.class).hasDiamond);
        assertFalse(nodes.get(Comparable.class).hasDiamond);
    }

    @Test
    public void testClearing() {

        LatticeRelation<Class<?>, Set<String>> lattice = new LatticeRelation<>(Monoid.forSet(), TopoOrder.TYPE_HIERARCHY_ORDERING);


        lattice.put(String.class, setOf("string"));
        lattice.put(Integer.class, setOf("int"));
        lattice.put(Long.class, setOf("long"));

        lattice.freezeTopo();
        lattice.clearValues();

        Map<Class<?>, LatticeRelation<Class<?>, Set<String>>.LNode> nodes = lattice.getNodes();

        nodes.values().forEach(it -> assertEquals(setOf(), it.computeValue()));
    }

}
