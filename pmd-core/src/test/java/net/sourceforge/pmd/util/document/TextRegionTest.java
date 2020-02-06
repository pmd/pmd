/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        TextRegion r = TextRegionImpl.fromOffsetLength(0, 0);

        assertTrue(r.isEmpty());
    }

    @Test
    public void testEmptyContains() {
        TextRegion r1 = TextRegionImpl.fromOffsetLength(0, 0);

        assertFalse(r1.containsChar(0));
    }

    @Test
    public void testContains() {
        TextRegion r1 = TextRegionImpl.fromOffsetLength(1, 2);

        assertFalse(r1.containsChar(0));
        assertTrue(r1.containsChar(1));
        assertTrue(r1.containsChar(2));
        assertFalse(r1.containsChar(3));
    }

    @Test
    public void testIntersectZeroLen() {
        // r1: [[-----
        // r2: [ -----[
        TextRegion r1 = TextRegionImpl.fromOffsetLength(0, 0);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(0, 5);

        TextRegion inter = doIntersect(r1, r2);

        assertEquals(r1, inter);
    }

    @Test
    public void testIntersectZeroLen2() {
        // r1:  -----[[
        // r2: [-----[
        TextRegion r1 = TextRegionImpl.fromOffsetLength(5, 0);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(0, 5);

        TextRegion inter = doIntersect(r1, r2);

        assertEquals(r1, inter);
    }

    @Test
    public void testIntersectZeroLen3() {
        // r1:  -- -[---[
        // r2:  --[-[---
        TextRegion r1 = TextRegionImpl.fromOffsetLength(3, 3);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(2, 1);

        TextRegion inter = doIntersect(r1, r2);

        assertEquals(3, inter.getStartOffset());
        assertEquals(0, inter.getLength());
        assertTrue(inter.isEmpty());
    }


    @Test
    public void testIntersectZeroLen4() {
        TextRegion r1 = TextRegionImpl.fromOffsetLength(0, 0);

        TextRegion inter = doIntersect(r1, r1);

        assertEquals(r1, inter);
    }

    @Test
    public void testNonEmptyIntersect() {
        // r1:  ---[-- --[
        // r2: [--- --[--
        // i:   ---[--[--
        TextRegion r1 = TextRegionImpl.fromOffsetLength(3, 4);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(0, 5);

        TextRegion inter = doIntersect(r1, r2);

        assertEquals(3, inter.getStartOffset());
        assertEquals(2, inter.getLength());
    }

    @Test
    public void testIntersectContained() {
        // r1:  --[- - ---[
        // r2:  -- -[-[---
        // i:   -- -[-[---
        TextRegion r1 = TextRegionImpl.fromOffsetLength(2, 5);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(3, 1);

        TextRegion inter = doIntersect(r1, r2);

        assertEquals(3, inter.getStartOffset());
        assertEquals(1, inter.getLength());
    }

    @Test
    public void testIntersectDisjoint() {
        // r1:  -- -[---[
        // r2:  --[-[---
        TextRegion r1 = TextRegionImpl.fromOffsetLength(4, 3);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(2, 1);

        noIntersect(r1, r2);
    }

    @Test
    public void testOverlapContained() {
        // r1:  --[- - ---[
        // r2:  -- -[-[---
        // i:   -- -[-[---
        TextRegion r1 = TextRegionImpl.fromOffsetLength(2, 5);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(3, 1);

        assertOverlap(r1, r2);
    }

    @Test
    public void testOverlapDisjoint() {
        // r1:  -- -[---[
        // r2:  --[-[---
        TextRegion r1 = TextRegionImpl.fromOffsetLength(4, 3);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(2, 1);

        assertNoOverlap(r1, r2);
    }


    @Test
    public void testOverlapBoundary() {
        // r1:  -- -[---[
        // r2:  --[-[---
        TextRegion r1 = TextRegionImpl.fromOffsetLength(3, 3);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(2, 1);

        assertNoOverlap(r1, r2);
    }

    @Test
    public void testCompare() {
        // r1:  --[-[---
        // r2:  -- -[---[
        TextRegion r1 = TextRegionImpl.fromOffsetLength(2, 1);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(3, 3);

        assertIsBefore(r1, r2);
    }

    @Test
    public void testCompareSameOffset() {
        // r1:  [-[--
        // r2:  [- --[
        TextRegion r1 = TextRegionImpl.fromOffsetLength(0, 1);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(0, 3);

        assertIsBefore(r1, r2);
    }


    @Test
    public void testUnion() {
        // r1:  --[-[---
        // r2:  -- -[---[
        TextRegion r1 = TextRegionImpl.fromOffsetLength(2, 1);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(3, 3);

        TextRegion union = doUnion(r1, r2);

        assertEquals(2, union.getStartOffset());
        assertEquals(6, union.getEndOffset());
        assertEquals(4, union.getLength());
    }

    @Test
    public void testUnionDisjoint() {
        // r1:  --[-[- ---
        // r2:  -- ---[---[
        TextRegion r1 = TextRegionImpl.fromOffsetLength(2, 1);
        TextRegion r2 = TextRegionImpl.fromOffsetLength(5, 3);

        TextRegion union = doUnion(r1, r2);

        assertEquals(2, union.getStartOffset());
        assertEquals(8, union.getEndOffset());
        assertEquals(6, union.getLength());
    }


    public void assertIsBefore(TextRegion r1, TextRegion r2) {
        assertTrue("Region " + r1 + " should be before " + r2, r1.compareTo(r2) < 0);
        assertTrue("Region " + r2 + " should be after " + r1, r2.compareTo(r1) > 0);
    }

    private void assertNoOverlap(TextRegion r1, TextRegion r2) {
        assertFalse("Regions " + r1 + " and " + r2 + " should not overlap", r1.overlaps(r2));
    }

    private void assertOverlap(TextRegion r1, TextRegion r2) {
        assertTrue("Regions " + r1 + " and " + r2 + " should overlap", r1.overlaps(r2));
    }


    private TextRegion doIntersect(TextRegion r1, TextRegion r2) {
        TextRegion inter = r1.intersect(r2);
        assertNotNull("Intersection of " + r1 + " and " + r2 + " must exist", inter);
        TextRegion symmetric = r2.intersect(r1);
        assertEquals("Intersection of " + r1 + " and " + r2 + " must be symmetric", inter, symmetric);

        return inter;
    }

    private TextRegion doUnion(TextRegion r1, TextRegion r2) {
        TextRegion union = r1.union(r2);

        assertTrue("Union of " + r1 + " and " + r2 + " must contain first region", union.contains(r1));
        assertTrue("Union of " + r1 + " and " + r2 + " must contain second region", union.contains(r2));

        TextRegion symmetric = r2.union(r1);
        assertEquals("Union of " + r1 + " and " + r2 + " must be symmetric", union, symmetric);

        return union;
    }

    private void noIntersect(TextRegion r1, TextRegion r2) {
        TextRegion inter = r1.intersect(r2);
        assertNull("Intersection of " + r1 + " and " + r2 + " must not exist", inter);
        TextRegion symmetric = r2.intersect(r1);
        assertEquals("Intersection of " + r1 + " and " + r2 + " must be symmetric", inter, symmetric);
    }

}
