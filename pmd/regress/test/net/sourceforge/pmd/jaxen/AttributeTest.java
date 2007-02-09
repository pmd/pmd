/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.jaxen;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.jaxen.Attribute;

import org.junit.Test;

import java.lang.reflect.Method;
public class AttributeTest{

    @Test
    public void testConstructor() {
        ASTPrimaryPrefix p = new ASTPrimaryPrefix(1);
        p.testingOnly__setBeginLine(5);
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
        assertEquals("5", a.getValue());
        assertEquals(p, a.getParent());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AttributeTest.class);
    }
}
