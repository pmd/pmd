/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

public class TypesFromReflectionTest extends BaseNonParserTest {

    private static final TypeSystem LOADER = JavaParsingHelper.TEST_TYPE_SYSTEM;

    @Rule
    public final ExpectedException expect = ExpectedException.none();

    @Test
    public void testNestedClass() {
        JClassSymbol c = TypesFromReflection.loadSymbol(LOADER, "java.util.Map.Entry");
        assertReflects(Map.Entry.class, c);
    }


    @Test
    public void testPrimitiveArray() {
        JClassSymbol c = TypesFromReflection.loadSymbol(LOADER, "int[ ]");
        assertReflects(int[].class, c);
    }

    @Test
    public void testNestedClassArray() {
        JClassSymbol c = TypesFromReflection.loadSymbol(LOADER, "java.util.Map.Entry[ ]");
        assertReflects(Map.Entry[].class, c);
    }

    @Test
    public void testInvalidName() {
        expect.expect(IllegalArgumentException.class);
        TypesFromReflection.loadSymbol(LOADER, "java.util.Map ]");
    }

    @Test
    public void testInvalidName2() {
        expect.expect(IllegalArgumentException.class);
        TypesFromReflection.loadSymbol(LOADER, "[]");
    }

    @Test
    public void testNullName() {
        expect.expect(NullPointerException.class);
        TypesFromReflection.loadSymbol(LOADER, null);
    }

    private void assertReflects(Class<?> expected, JClassSymbol actual) {
        if (expected == null) {
            Assert.assertNull(actual);
            return;
        }
        Assert.assertNotNull("Expected " + expected, actual);
        Assert.assertEquals("Annot", expected.isAnnotation(), actual.isAnnotation());
        Assert.assertEquals("Array", expected.isArray(), actual.isArray());
        Assert.assertEquals("Modifiers", expected.getModifiers(), actual.getModifiers());
        if (actual.isArray()) {
            assertReflects(expected.getComponentType(), (JClassSymbol) actual.getArrayComponent());
            // don't test names, the spec of Class::getName and JClassSymbol::getBinaryName
            // differ for arrays
            return;
        }
        Assert.assertEquals("Binary name", expected.getName(), actual.getBinaryName());
        Assert.assertEquals("Canonical name", expected.getCanonicalName(), actual.getCanonicalName());
        assertReflects(expected.getEnclosingClass(), actual.getEnclosingClass());
    }

}
