/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.jaxen;

import junit.framework.TestCase;
import net.sourceforge.pmd.jaxen.Attribute;

public class AttributeTest extends TestCase {

    public void testConstructor() {
        Attribute a = new Attribute(null, "name", "value");
        assertEquals("name", a.getName());
        assertEquals("value", a.getValue());
        assertNull(a.getParent());
    }

    public void testAccessors() {
        Attribute a = new Attribute(null, null, null);
        a.setName("name");
        a.setValue("value");
        a.setParent(null);
        assertEquals("name", a.getName());
        assertEquals("value", a.getValue());
        assertNull(a.getParent());

    }
}
