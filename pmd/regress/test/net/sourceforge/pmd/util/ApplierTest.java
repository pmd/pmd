/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
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
