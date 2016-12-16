/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.jaxen;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;

public class AttributeTest {

    @Test
    public void testConstructor() {
        DummyNode p = new DummyNode(1);
        p.testingOnlySetBeginLine(5);
        Method[] methods = p.getClass().getMethods();
        Method m = null;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals("getBeginLine")) {
                m = methods[i];
                break;
            }
        }
        Attribute a = new Attribute(p, "BeginLine", m);
        assertEquals("BeginLine", a.getName());
        assertEquals(5, a.getValue());
        assertEquals("5", a.getStringValue());
        assertEquals(p, a.getParent());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AttributeTest.class);
    }
}
