/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import static net.sourceforge.pmd.lang.java.typeresolution.internal.NullableClassLoader.ClassLoaderWrapper.wrapNullable;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;
import net.sourceforge.pmd.lang.java.typeresolution.internal.NullableClassLoader;

public class TypeHelperTest extends BaseNonParserTest {

    private static final NullableClassLoader LOADER = wrapNullable(TypeHelperTest.class.getClassLoader());

    @Rule
    public final ExpectedException expect = ExpectedException.none();

    @Test
    public void testIsAFallback() {

        ASTClassOrInterfaceDeclaration klass =
            java.parse("package org; import java.io.Serializable; "
                           + "class FooBar implements Serializable {}")
                .getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);


        Assert.assertNull(klass.getType());
        Assert.assertTrue(TypeHelper.isA(klass, "org.FooBar"));
        Assert.assertTrue(TypeHelper.isA(klass, "java.io.Serializable"));
        Assert.assertTrue(TypeHelper.isA(klass, Serializable.class));
    }



    @Test
    public void testIsAFallbackEnum() {

        ASTEnumDeclaration klass =
            java.parse("package org; "
                           + "enum FooBar implements Iterable {}")
                .getFirstDescendantOfType(ASTEnumDeclaration.class);


        Assert.assertNull(klass.getType());
        Assert.assertTrue(TypeHelper.isA(klass, "org.FooBar"));
        assertIsA(klass, Iterable.class);
        assertIsA(klass, Enum.class);
        assertIsA(klass, Serializable.class);
        assertIsA(klass, Object.class);
    }

    @Test
    public void testIsAFallbackAnnotation() {

        ASTAnnotationTypeDeclaration klass =
            java.parse("package org; import foo.Stuff;"
                           + "public @interface FooBar {}")
                .getFirstDescendantOfType(ASTAnnotationTypeDeclaration.class);


        Assert.assertNull(klass.getType());
        Assert.assertTrue(TypeHelper.isA(klass, "org.FooBar"));
        assertIsA(klass, Annotation.class);
        assertIsA(klass, Object.class);
    }

    /**
     * If we don't have the annotation on the classpath,
     * we should resolve the full name via the import, if possible
     * and compare then. Only after that, we should compare the
     * simple names.
     */
    @Test
    public void testIsAFallbackAnnotationSimpleNameImport() {
        ASTName annotation = java.parse("package org; import foo.Stuff; @Stuff public class FooBar {}")
                .getFirstDescendantOfType(ASTMarkerAnnotation.class).getFirstChildOfType(ASTName.class);

        Assert.assertNull(annotation.getType());
        Assert.assertTrue(TypeHelper.isA(annotation, "foo.Stuff"));
        Assert.assertFalse(TypeHelper.isA(annotation, "other.Stuff"));
        // if the searched class name is not fully qualified, then the search should still be successfull
        Assert.assertTrue(TypeHelper.isA(annotation, "Stuff"));
    }

    private void assertIsA(TypeNode node, Class<?> type) {
        Assert.assertTrue("TypeHelper::isA with class arg: " + type.getCanonicalName(),
                          TypeHelper.isA(node, type));
        Assert.assertTrue("TypeHelper::isA with string arg: " + type.getCanonicalName(),
                          TypeHelper.isA(node, type.getCanonicalName()));
    }

}
