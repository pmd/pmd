/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link SimpleTypedNameDeclaration}
 */
public class SimpleTypedNameDeclarationTest {

    /**
     * Tests the equal method.
     */
    @Test
    public void testEquals() {
        Assert.assertEquals(byClass(SimpleTypedNameDeclaration.class), byClass(SimpleTypedNameDeclaration.class));
        Assert.assertEquals(byClass(List.class), byClass(ArrayList.class));
        Assert.assertEquals(byClass(ArrayList.class), byClass(List.class));
        Assert.assertEquals(byName("String"), byName("String"));
        Assert.assertEquals(byClass(String.class), byName("String"));
        Assert.assertEquals(byClass(JComponent.class), byClass(JTextField.class));

        Assert.assertFalse(byClass(Map.class).equals(byClass(List.class)));
        Assert.assertFalse(byName("A").equals(byName("B")));
        Assert.assertFalse(byClass(String.class).equals(byName("A")));
    }

    private static SimpleTypedNameDeclaration byClass(Class<?> c) {
        return new SimpleTypedNameDeclaration(c.getSimpleName(), c);
    }
    private static SimpleTypedNameDeclaration byName(String n) {
        return new SimpleTypedNameDeclaration(n, null);
    }
}
