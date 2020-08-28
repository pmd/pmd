/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

@Ignore("Ignored until the typeres branch is merged")
public class TypeTestUtilTest extends BaseNonParserTest {

    @Rule
    public final ExpectedException expect = ExpectedException.none();

    @Test
    public void testIsAFallback() {

        ASTClassOrInterfaceDeclaration klass =
            java.parse("package org; import java.io.Serializable; "
                           + "class FooBar implements Serializable {}")
                .getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);


        Assert.assertNull(klass.getType());
        Assert.assertTrue(TypeTestUtil.isA("org.FooBar", klass));
        Assert.assertTrue(TypeTestUtil.isA("java.io.Serializable", klass));
        Assert.assertTrue(TypeTestUtil.isA(Serializable.class, klass));
    }


    @Test
    public void testIsAFallbackEnum() {

        ASTEnumDeclaration klass =
            java.parse("package org; "
                           + "enum FooBar implements Iterable {}")
                .getFirstDescendantOfType(ASTEnumDeclaration.class);


        Assert.assertNull(klass.getType());
        Assert.assertTrue(TypeTestUtil.isA("org.FooBar", klass));
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
        Assert.assertTrue(TypeTestUtil.isA("org.FooBar", klass));
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
        ASTAnnotation annotation = java.parse("package org; import foo.Stuff; @Stuff public class FooBar {}")
                                       .getFirstDescendantOfType(ASTAnnotation.class);

        Assert.assertNull(annotation.getType());
        Assert.assertTrue(TypeTestUtil.isA("foo.Stuff", annotation));
        Assert.assertFalse(TypeTestUtil.isA("other.Stuff", annotation));
        // if the searched class name is not fully qualified, then the search should still be successful
        Assert.assertTrue(TypeTestUtil.isA("Stuff", annotation));
    }

    @Test
    public void testNullNode() {
        Assert.assertFalse(TypeTestUtil.isA(String.class, null));
        Assert.assertFalse(TypeTestUtil.isA("java.lang.String", null));
        Assert.assertFalse(TypeTestUtil.isExactlyA(String.class, null));
        Assert.assertFalse(TypeTestUtil.isExactlyA("java.lang.String", null));
    }

    @Test
    public void testNullClass() {
        final ASTAnnotation node = java.parse("package org; import foo.Stuff; @Stuff public class FooBar {}")
                                       .getFirstDescendantOfType(ASTAnnotation.class);
        Assert.assertNotNull(node);

        Assert.assertThrows(NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                TypeTestUtil.isA((String) null, node);
            }
        });
        Assert.assertThrows(NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                TypeTestUtil.isA((Class<?>) null, node);
            }
        });
        Assert.assertThrows(NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                TypeTestUtil.isExactlyA((Class<?>) null, node);
            }
        });
        Assert.assertThrows(NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                TypeTestUtil.isExactlyA((String) null, node);
            }
        });
    }

    private void assertIsA(TypeNode node, Class<?> type) {
        Assert.assertTrue("TypeTestUtil::isA with class arg: " + type.getCanonicalName(),
                          TypeTestUtil.isA(type, node));
        Assert.assertTrue("TypeTestUtil::isA with string arg: " + type.getCanonicalName(),
                          TypeTestUtil.isA(type.getCanonicalName(), node));
    }

}
