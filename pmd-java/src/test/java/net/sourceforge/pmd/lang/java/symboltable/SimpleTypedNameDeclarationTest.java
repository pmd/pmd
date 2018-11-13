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

        Assert.assertEquals(by(Double.TYPE, "double"), by(null, "double"));
        Assert.assertEquals(by(Double.class, "Double"), by(null, "double"));
        Assert.assertEquals(by(Character.class, "Character"), by(null, "char"));
        Assert.assertEquals(by(Double.TYPE, "double"), by(null, "float"));
        Assert.assertEquals(by(Double.TYPE, "double"), by(null, "int"));
        Assert.assertEquals(by(Double.TYPE, "double"), by(Integer.class, "Integer"));
        Assert.assertEquals(by(Double.TYPE, "double"), by(null, "long"));
        Assert.assertEquals(by(Double.TYPE, "double"), by(Long.TYPE, "long"));
        Assert.assertEquals(by(Double.TYPE, "double"), by(Long.class, "Long"));
        Assert.assertEquals(by(Float.TYPE, "float"), by(null, "int"));
        Assert.assertEquals(by(Float.TYPE, "float"), by(Integer.TYPE, "int"));
        Assert.assertEquals(by(Float.TYPE, "float"), by(Integer.class, "Integer"));
        Assert.assertEquals(by(Float.TYPE, "float"), by(null, "long"));
        Assert.assertEquals(by(Float.TYPE, "float"), by(Long.TYPE, "long"));
        Assert.assertEquals(by(Float.TYPE, "float"), by(Long.class, "Long"));
        Assert.assertEquals(by(Integer.TYPE, "int"), by(null, "char"));
        Assert.assertEquals(by(Integer.TYPE, "int"), by(Character.TYPE, "char"));
        Assert.assertEquals(by(Integer.TYPE, "int"), by(Character.class, "Character"));
        Assert.assertEquals(by(Long.TYPE, "long"), by(null, "int"));
        Assert.assertEquals(by(Long.TYPE, "long"), by(Integer.TYPE, "int"));
        Assert.assertEquals(by(Long.TYPE, "long"), by(Integer.class, "Integer"));
        Assert.assertEquals(by(Long.TYPE, "long"), by(null, "char"));
        Assert.assertEquals(by(Long.TYPE, "long"), by(Character.TYPE, "char"));
        Assert.assertEquals(by(Long.TYPE, "long"), by(Character.class, "Character"));

        // should always equal to Object
        Assert.assertEquals(by(Object.class, "Object"), by(null, "Something"));

        Assert.assertEquals(withNext(byName("Foo.I"), "Foo.B"), byName("Foo.I"));
        Assert.assertEquals(byName("Foo.I"), withNext(byName("Foo.I"), "Foo.B"));
    }

    private static SimpleTypedNameDeclaration byClass(Class<?> c) {
        return new SimpleTypedNameDeclaration(c.getSimpleName(), c);
    }

    private static SimpleTypedNameDeclaration byName(String n) {
        return new SimpleTypedNameDeclaration(n, null);
    }

    private static SimpleTypedNameDeclaration by(Class<?> c, String n) {
        return new SimpleTypedNameDeclaration(n, c);
    }

    private static SimpleTypedNameDeclaration withNext(SimpleTypedNameDeclaration next, String n) {
        SimpleTypedNameDeclaration t = new SimpleTypedNameDeclaration(n, null);
        t.addNext(next);
        return t;
    }
}
