/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.symboltable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

class ApplierTest {

    private static class MyFunction implements Predicate<Object> {
        private int numCallbacks = 0;
        private final int maxCallbacks;

        MyFunction(int maxCallbacks) {
            this.maxCallbacks = maxCallbacks;
        }

        @Override
        public boolean test(Object o) {
            this.numCallbacks++;
            return numCallbacks < maxCallbacks;
        }

        public int getNumCallbacks() {
            return this.numCallbacks;
        }
    }

    @Test
    void testSimple() {
        MyFunction f = new MyFunction(Integer.MAX_VALUE);
        List<Object> l = new ArrayList<>();
        l.add(new Object());
        l.add(new Object());
        l.add(new Object());
        Applier.apply(f, l.iterator());
        assertEquals(l.size(), f.getNumCallbacks());
    }

    @Test
    void testLimit() {
        MyFunction f = new MyFunction(2);
        List<Object> l = new ArrayList<>();
        l.add(new Object());
        l.add(new Object());
        l.add(new Object());
        Applier.apply(f, l.iterator());
        assertEquals(2, f.getNumCallbacks());
    }
}
