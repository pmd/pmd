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
package test.net.sourceforge.pmd.jaxen;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.AttributeAxisIterator;
import net.sourceforge.pmd.jaxen.Attribute;

import java.util.HashSet;
import java.util.Set;

public class AttributeAxisIteratorTest extends TestCase {

    public void testBasicAttributes() {
        Set names = new HashSet();
        names.add("BeginLine");
        names.add("EndLine");
        names.add("BeginColumn");
        names.add("EndColumn");
        SimpleNode n = new SimpleNode(0);
        n.testingOnly__setBeginColumn(1);
        n.testingOnly__setBeginLine(1);
        AttributeAxisIterator iter = new AttributeAxisIterator(n);
        try {
            Attribute a = (Attribute)iter.next();
            assertTrue(names.contains(a.getName()));
            a = (Attribute)iter.next();
            assertTrue(names.contains(a.getName()));
            a = (Attribute)iter.next();
            assertTrue(names.contains(a.getName()));
            a = (Attribute)iter.next();
            assertTrue(names.contains(a.getName()));
        } catch (UnsupportedOperationException e) {
            // cool
        }
    }

    public void testRemove() {
        SimpleNode n = new SimpleNode(0);
        n.testingOnly__setBeginColumn(1);
        n.testingOnly__setBeginLine(1);
        AttributeAxisIterator iter = new AttributeAxisIterator(n);
        try {
            iter.remove();
            fail("Should have thrown an exception!");
        } catch (UnsupportedOperationException e) {
            // cool
        }
    }

}
