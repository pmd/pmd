/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

public class TypeHelperTest extends BaseNonParserTest {


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

    private void assertIsA(TypeNode node, Class<?> type) {
        Assert.assertTrue(TypeHelper.isA(node, type));
        Assert.assertTrue(TypeHelper.isA(node, type.getCanonicalName()));
    }

    private void assertIsExactlyA(TypeNode node, Class<?> type) {
        Assert.assertTrue(TypeHelper.isExactlyA(node, type.getCanonicalName()));
        assertIsA(node, type);
    }

}
