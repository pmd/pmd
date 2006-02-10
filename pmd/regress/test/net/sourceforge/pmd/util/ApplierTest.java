/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.util;

import junit.framework.TestCase;
import net.sourceforge.pmd.util.Applier;
import net.sourceforge.pmd.util.UnaryFunction;

import java.util.ArrayList;
import java.util.List;

public class ApplierTest extends TestCase {

    private static class MyFunction implements UnaryFunction {
        private boolean gotCallback;

        public void applyTo(Object o) {
            this.gotCallback = true;
        }

        public boolean gotCallback() {
            return this.gotCallback;
        }
    }

    public void testSimple() {
        MyFunction f = new MyFunction();
        List l = new ArrayList();
        l.add(new Object());
        Applier.apply(f, l.iterator());
        assertTrue(f.gotCallback());
    }
}
