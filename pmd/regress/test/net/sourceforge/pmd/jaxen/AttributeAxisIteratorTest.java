/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.jaxen;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.Attribute;
import net.sourceforge.pmd.jaxen.AttributeAxisIterator;

import java.util.HashSet;
import java.util.Set;

public class AttributeAxisIteratorTest extends TestCase {

    public void testBasicAttributes() {
        Set names = new HashSet();
        names.add("BeginLine");
        names.add("EndLine");
        names.add("BeginColumn");
        names.add("EndColumn");
        names.add("Discardable");
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
