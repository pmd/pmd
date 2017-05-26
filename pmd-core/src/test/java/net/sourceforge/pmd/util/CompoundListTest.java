/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

public class CompoundListTest {

    private List<String> l1;
    private List<String> l2;
    private Iterator<String> iterator;

    @Before
    public void setUp() {
        l1 = new ArrayList<>();
        l1.add("1");
        l1.add("2");
        l2 = new ArrayList<>();
        l2.add("3");
        l2.add("4");

        iterator = new CompoundIterator<>(l1.iterator(), l2.iterator());
    }

    @Test
    public void testHappyPath() {
        assertTrue(iterator.hasNext());
        assertEquals("1", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("2", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("3", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("4", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(2, l1.size());
        assertEquals(2, l2.size());
    }

    @Test
    public void testHappyPathRemove() {
        assertTrue(iterator.hasNext());
        assertEquals("1", iterator.next());
        iterator.remove();
        assertTrue(iterator.hasNext());
        assertEquals("2", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("3", iterator.next());
        iterator.remove();
        assertTrue(iterator.hasNext());
        assertEquals("4", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(1, l1.size());
        assertEquals("2", l1.get(0));
        assertEquals(1, l2.size());
        assertEquals("4", l2.get(0));
    }

    @Test
    public void testEmpty() {
        Iterator<?> iterator = new CompoundIterator();
        assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyBadNext() {
        Iterator<?> iterator = new CompoundIterator();
        iterator.next();
    }

    @Test(expected = IllegalStateException.class)
    public void testEmptyBadRemove() {
        Iterator<?> iterator = new CompoundIterator();
        iterator.remove();
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CompoundListTest.class);
    }
}
