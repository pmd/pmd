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
        Assert.assertTrue(TypeHelper.isA(klass, "java.lang.Iterable"));
        Assert.assertTrue(TypeHelper.isA(klass, Iterable.class));
        Assert.assertTrue(TypeHelper.isA(klass, Enum.class));
        Assert.assertTrue(TypeHelper.isA(klass, Serializable.class));
        Assert.assertTrue(TypeHelper.isA(klass, Comparable.class));
    }

    @Test
    public void testIsAFallbackAnnotation() {

        ASTAnnotationTypeDeclaration klass =
            java.parse("package org; import foo.Stuff;"
                           + "public @interface FooBar {}")
                .getFirstDescendantOfType(ASTAnnotationTypeDeclaration.class);


        Assert.assertNull(klass.getType());
        Assert.assertTrue(TypeHelper.isA(klass, "org.FooBar"));
        Assert.assertTrue(TypeHelper.isA(klass, Annotation.class));
    }


}
