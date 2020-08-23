/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.lang.java.typeresolution.internal.NullableClassLoader.ClassLoaderWrapper.wrapNullable;

import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;
import net.sourceforge.pmd.lang.java.typeresolution.internal.NullableClassLoader;

public class TypesFromReflectionTest extends BaseNonParserTest {

    private static final NullableClassLoader LOADER = wrapNullable(TypesFromReflectionTest.class.getClassLoader());

    @Rule
    public final ExpectedException expect = ExpectedException.none();

    @Test
    public void testNestedClass() {
        Class<?> c = TypesFromReflection.loadClass(LOADER, "java.util.Map.Entry");
        Assert.assertEquals(Map.Entry.class, c);
    }


    @Test
    public void testPrimitiveArray() {
        Class<?> c = TypesFromReflection.loadClass(LOADER, "int[ ]");
        Assert.assertEquals(int[].class, c);
    }

    @Test
    public void testNestedClassArray() {
        Class<?> c = TypesFromReflection.loadClass(LOADER, "java.util.Map.Entry[ ]");
        Assert.assertEquals(Map.Entry[].class, c);
    }

    @Test
    public void testInvalidName() {
        expect.expect(IllegalArgumentException.class);
        TypesFromReflection.loadClass(LOADER, "java.util.Map ]");
    }

    @Test
    public void testInvalidName2() {
        expect.expect(IllegalArgumentException.class);
        TypesFromReflection.loadClass(LOADER, "[]");
    }

    @Test
    public void testNullName() {
        expect.expect(NullPointerException.class);
        TypesFromReflection.loadClass(LOADER, null);
    }

}
