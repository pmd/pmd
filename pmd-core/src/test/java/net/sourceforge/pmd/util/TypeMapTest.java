/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * Evaluates all major functionality of the TypeMap class.
 *
 * @author Brian Remedios
 */
public class TypeMapTest {

    @Test
    public void testAddClassOfQ() {

        TypeMap map = new TypeMap(2);
        map.add(List.class);

        try {
            map.add(java.awt.List.class);
        } catch (IllegalArgumentException ex) {
            return; // caught ok
        }

        fail("Uncaught error inserting type with same root names");
    }

    @Test
    public void testContainsClassOfQ() {

        TypeMap map = new TypeMap(2);
        map.add(String.class);
        map.add(List.class);

        Assert.assertTrue(map.contains(String.class));
        Assert.assertTrue(map.contains(List.class));
        Assert.assertFalse(map.contains(Map.class));
    }

    @Test
    public void testContainsString() {

        TypeMap map = new TypeMap(2);
        map.add(String.class);
        map.add(List.class);

        Assert.assertTrue(map.contains("String"));
        Assert.assertTrue(map.contains("java.lang.String"));
    }

    @Test
    public void testTypeFor() {

        TypeMap map = new TypeMap(2);
        map.add(String.class);
        map.add(List.class);

        Assert.assertTrue(map.typeFor("String") == String.class);
        Assert.assertTrue(map.typeFor("java.lang.String") == String.class);
        Assert.assertTrue(map.typeFor("List") == List.class);
        Assert.assertTrue(map.typeFor("java.util.List") == List.class);
    }

    @Test
    public void testSize() {

        TypeMap map = new TypeMap(4);
        map.add(String.class);
        map.add(HashMap.class);
        map.add(Integer.class);

        Assert.assertTrue(map.size() == 6);
    }
}
