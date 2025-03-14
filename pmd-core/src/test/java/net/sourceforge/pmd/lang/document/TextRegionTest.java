/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TextRegionTest {

    @Test
    void testIsEmpty() {
        TextRegion r = TextRegion.fromOffsetLength(0, 0);

        assertTrue(r.isEmpty());
    }

    @Test
    void testEmptyContains() {
        TextRegion r1 = TextRegion.fromOffsetLength(0, 0);

        assertFalse(r1.contains(0));
    }

    @Test
    void testContains() {
        TextRegion r1 = TextRegion.fromOffsetLength(1, 2);

        assertFalse(r1.contains(0));
        assertTrue(r1.contains(1));
        assertTrue(r1.contains(2));
        assertFalse(r1.contains(3));
    }

    @Test
    void testIntersectZeroLen() {
        // r1: [[-----
        // r2: [ -----[
        TextRegion r1 = TextRegion.fromOffsetLength(0, 0);
        TextRegion r2 = TextRegion.fromOffsetLength(0, 5);

        TextRegion inter = doIntersect(r1, r2);

        assertEquals(r1, inter);
    }

    @Test
    void testIntersectZeroLen2() {
        // r1:  -----[[
        // r2: [-----[
        TextRegion r1 = TextRegion.fromOffsetLength(5, 0);
        TextRegion r2 = TextRegion.fromOffsetLength(0, 5);

        TextRegion inter = doIntersect(r1, r2);

        assertEquals(r1, inter);
    }

    @Test
    void testIntersectZeroLen3() {
        // r1:  -- -[---[
        // r2:  --[-[---
        TextRegion r1 = TextRegion.fromOffsetLength(3, 3);
        TextRegion r2 = TextRegion.fromOffsetLength(2, 1);

        TextRegion inter = doIntersect(r1, r2);

        assertRegionEquals(inter, 3, 0);
        assertTrue(inter.isEmpty());
    }


    @Test
    void testIntersectZeroLen4() {
        TextRegion r1 = TextRegion.fromOffsetLength(0, 0);

        TextRegion inter = doIntersect(r1, r1);

        assertEquals(r1, inter);
    }

    @Test
    void testNonEmptyIntersect() {
        // r1:  ---[-- --[
        // r2: [--- --[--
        // i:   ---[--[--
        TextRegion r1 = TextRegion.fromOffsetLength(3, 4);
        TextRegion r2 = TextRegion.fromOffsetLength(0, 5);

        TextRegion inter = doIntersect(r1, r2);

        assertRegionEquals(inter, 3, 2);
    }

    @Test
    void testIntersectContained() {
        // r1:  --[- - ---[
        // r2:  -- -[-[---
        // i:   -- -[-[---
        TextRegion r1 = TextRegion.fromOffsetLength(2, 5);
        TextRegion r2 = TextRegion.fromOffsetLength(3, 1);

        TextRegion inter = doIntersect(r1, r2);

        assertRegionEquals(inter, 3, 1);
    }

    @Test
    void testIntersectDisjoint() {
        // r1:  -- -[---[
        // r2:  --[-[---
        TextRegion r1 = TextRegion.fromOffsetLength(4, 3);
        TextRegion r2 = TextRegion.fromOffsetLength(2, 1);

        noIntersect(r1, r2);
    }

    @Test
    void testOverlapContained() {
        // r1:  --[- - ---[
        // r2:  -- -[-[---
        // i:   -- -[-[---
        TextRegion r1 = TextRegion.fromOffsetLength(2, 5);
        TextRegion r2 = TextRegion.fromOffsetLength(3, 1);

        assertOverlap(r1, r2);
    }

    @Test
    void testOverlapDisjoint() {
        // r1:  -- -[---[
        // r2:  --[-[---
        TextRegion r1 = TextRegion.fromOffsetLength(4, 3);
        TextRegion r2 = TextRegion.fromOffsetLength(2, 1);

        assertNoOverlap(r1, r2);
    }


    @Test
    void testOverlapBoundary() {
        // r1:  -- -[---[
        // r2:  --[-[---
        TextRegion r1 = TextRegion.fromOffsetLength(3, 3);
        TextRegion r2 = TextRegion.fromOffsetLength(2, 1);

        assertNoOverlap(r1, r2);
    }

    @Test
    void testCompare() {
        // r1:  --[-[---
        // r2:  -- -[---[
        TextRegion r1 = TextRegion.fromOffsetLength(2, 1);
        TextRegion r2 = TextRegion.fromOffsetLength(3, 3);

        assertIsBefore(r1, r2);
    }

    @Test
    void testCompareSameOffset() {
        // r1:  [-[--
        // r2:  [- --[
        TextRegion r1 = TextRegion.fromOffsetLength(0, 1);
        TextRegion r2 = TextRegion.fromOffsetLength(0, 3);

        assertIsBefore(r1, r2);
    }


    @Test
    void testUnion() {
        // r1:  --[-[---
        // r2:  -- -[---[
        TextRegion r1 = TextRegion.fromOffsetLength(2, 1);
        TextRegion r2 = TextRegion.fromOffsetLength(3, 3);

        TextRegion union = doUnion(r1, r2);

        assertRegionEquals(union, 2, 4);
    }

    @Test
    void testUnionDisjoint() {
        // r1:  --[-[- ---
        // r2:  -- ---[---[
        TextRegion r1 = TextRegion.fromOffsetLength(2, 1);
        TextRegion r2 = TextRegion.fromOffsetLength(5, 3);

        TextRegion union = doUnion(r1, r2);

        assertRegionEquals(union, 2, 6);
    }

    @Test
    void testGrowLeft() {
        // r1:   --[-[-
        // r2:  [-- -[-
        TextRegion r1 = TextRegion.fromOffsetLength(2, 1);

        TextRegion r2 = r1.growLeft(+2);

        assertRegionEquals(r2, 0, 3);
    }

    @Test
    void testGrowLeftNegative() {
        // r1:  --[- [-
        // r2:  -- -[[-
        TextRegion r1 = TextRegion.fromOffsetLength(2, 1);

        TextRegion r2 = r1.growLeft(-1);

        assertRegionEquals(r2, 3, 0);
    }

    @Test
    void testGrowLeftOutOfBounds() {
        // r1:  --[-[-
        TextRegion r1 = TextRegion.fromOffsetLength(2, 1);

        assertThrows(AssertionError.class, () -> r1.growLeft(4));
    }

    @Test
    void testGrowRight() {
        // r1:  --[-[-
        // r2:  --[- -[
        TextRegion r1 = TextRegion.fromOffsetLength(2, 1);

        TextRegion r2 = r1.growRight(+1);

        assertRegionEquals(r2, 2, 2);
    }

    @Test
    void testGrowRightNegative() {
        // r1:  --[ -[-
        // r2:  --[[- -
        TextRegion r1 = TextRegion.fromOffsetLength(2, 1);

        TextRegion r2 = r1.growRight(-1);

        assertRegionEquals(r2, 2, 0);
    }

    @Test
    void testGrowRightOutOfBounds() {
        // r1:  --[-[-
        TextRegion r1 = TextRegion.fromOffsetLength(2, 1);

        assertThrows(AssertionError.class, () -> r1.growRight(-2));
    }


    private static void assertRegionEquals(TextRegion region, int start, int len) {
        assertEquals(start, region.getStartOffset(), "Start offset");
        assertEquals(len, region.getLength(), "Length");
    }

    private static void assertIsBefore(TextRegion r1, TextRegion r2) {
        assertTrue(r1.compareTo(r2) < 0, "Region " + r1 + " should be before " + r2);
        assertTrue(r2.compareTo(r1) > 0, "Region " + r2 + " should be after " + r1);
    }

    private static void assertNoOverlap(TextRegion r1, TextRegion r2) {
        assertFalse(r1.overlaps(r2), "Regions " + r1 + " and " + r2 + " should not overlap");
    }

    private static void assertOverlap(TextRegion r1, TextRegion r2) {
        assertTrue(r1.overlaps(r2), "Regions " + r1 + " and " + r2 + " should overlap");
    }


    private TextRegion doIntersect(TextRegion r1, TextRegion r2) {
        TextRegion inter = TextRegion.intersect(r1, r2);
        assertNotNull(inter, "Intersection of " + r1 + " and " + r2 + " must exist");
        TextRegion symmetric = TextRegion.intersect(r2, r1);
        assertEquals(inter, symmetric, "Intersection of " + r1 + " and " + r2 + " must be symmetric");

        return inter;
    }

    private TextRegion doUnion(TextRegion r1, TextRegion r2) {
        TextRegion union = TextRegion.union(r1, r2);

        assertTrue(union.contains(r1), "Union of " + r1 + " and " + r2 + " must contain first region");
        assertTrue(union.contains(r2), "Union of " + r1 + " and " + r2 + " must contain second region");

        TextRegion symmetric = TextRegion.union(r2, r1);
        assertEquals(union, symmetric, "Union of " + r1 + " and " + r2 + " must be symmetric");

        return union;
    }

    private void noIntersect(TextRegion r1, TextRegion r2) {
        TextRegion inter = TextRegion.intersect(r1, r2);
        assertNull(inter, "Intersection of " + r1 + " and " + r2 + " must not exist");
        TextRegion symmetric = TextRegion.intersect(r2, r1);
        assertEquals(inter, symmetric, "Intersection of " + r1 + " and " + r2 + " must be symmetric");
    }

}
