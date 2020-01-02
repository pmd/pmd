/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TextRegionTest {

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    public void testNegativeOffset() {

        expect.expect(IllegalArgumentException.class);

        TextRegionImpl.fromOffsetLength(-1, 0);
    }

    @Test
    public void testNegativeLength() {

        expect.expect(IllegalArgumentException.class);

        TextRegionImpl.fromOffsetLength(0, -1);
    }

    @Test
    public void testIsEmpty() {
        TextRegionImpl r = TextRegionImpl.fromOffsetLength(0, 0);

        assertTrue(r.isEmpty());
    }

    @Test
    public void testIntersectZeroLen() {
        // r1: [[-----
        // r2: [ -----[
        TextRegionImpl r1 = TextRegionImpl.fromOffsetLength(0, 0);
        TextRegionImpl r2 = TextRegionImpl.fromOffsetLength(0, 5);

        TextRegion inter = doIntersect(r1, r2);

        assertEquals(r1, inter);
    }

    @Test
    public void testIntersectZeroLen2() {
        // r1:  -----[[
        // r2: [-----[
        TextRegionImpl r1 = TextRegionImpl.fromOffsetLength(5, 0);
        TextRegionImpl r2 = TextRegionImpl.fromOffsetLength(0, 5);

        TextRegion inter = doIntersect(r1, r2);

        assertEquals(r1, inter);
    }

    @Test
    public void testIntersectZeroLen3() {
        // r1:  -- -[---[
        // r2:  --[-[---
        TextRegionImpl r1 = TextRegionImpl.fromOffsetLength(3, 3);
        TextRegionImpl r2 = TextRegionImpl.fromOffsetLength(2, 1);

        TextRegion inter = doIntersect(r1, r2);

        assertEquals(3, inter.getStartOffset());
        assertEquals(0, inter.getLength());
        assertTrue(inter.isEmpty());
    }


    @Test
    public void testIntersectZeroLen4() {
        TextRegionImpl r1 = TextRegionImpl.fromOffsetLength(0, 0);

        TextRegion inter = doIntersect(r1, r1);

        assertEquals(r1, inter);
    }

    @Test
    public void testNonEmptyIntersect() {
        // r1:  ---[-- --[
        // r2: [--- --[--
        // i:   ---[--[--
        TextRegionImpl r1 = TextRegionImpl.fromOffsetLength(3, 4);
        TextRegionImpl r2 = TextRegionImpl.fromOffsetLength(0, 5);

        TextRegion inter = doIntersect(r1, r2);

        assertEquals(3, inter.getStartOffset());
        assertEquals(2, inter.getLength());
    }

    @Test
    public void testIntersectContained() {
        // r1:  --[- - ---[
        // r2:  -- -[-[---
        // i:   -- -[-[---
        TextRegionImpl r1 = TextRegionImpl.fromOffsetLength(2, 5);
        TextRegionImpl r2 = TextRegionImpl.fromOffsetLength(3, 1);

        TextRegion inter = doIntersect(r1, r2);

        assertEquals(3, inter.getStartOffset());
        assertEquals(1, inter.getLength());
    }

    @Test
    public void testIntersectDisjoint() {
        // r1:  -- -[---[
        // r2:  --[-[---
        TextRegionImpl r1 = TextRegionImpl.fromOffsetLength(4, 3);
        TextRegionImpl r2 = TextRegionImpl.fromOffsetLength(2, 1);

        noIntersect(r1, r2);
    }

    private TextRegion doIntersect(TextRegion r1, TextRegion r2) {
        TextRegion inter = r1.intersect(r2);
        assertNotNull("Intersection of " + r1 + " and " + r2 + " must exist", inter);
        TextRegion symmetric = r2.intersect(r1);
        assertEquals("Intersection of " + r1 + " and " + r2 + " must be symmetric", inter, symmetric);

        return inter;
    }

    private void noIntersect(TextRegion r1, TextRegion r2) {
        TextRegion inter = r1.intersect(r2);
        assertNull("Intersection of " + r1 + " and " + r2 + " must not exist", inter);
        TextRegion symmetric = r2.intersect(r1);
        assertEquals("Intersection of " + r1 + " and " + r2 + " must be symmetric", inter, symmetric);
    }

}
