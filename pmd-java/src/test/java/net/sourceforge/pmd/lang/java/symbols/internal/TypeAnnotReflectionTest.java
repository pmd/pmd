/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.ANNOTS_A_B;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.ANNOT_A;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.ANNOT_B;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.assertHasTypeAnnots;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.getFieldType;
import static net.sourceforge.pmd.util.CollectionUtil.emptyList;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.mapOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsInside;
import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsInside.A;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JWildcardType;

/**
 *
 */
class TypeAnnotReflectionTest {


    @ParameterizedTest
    @EnumSource
    void testTypeAnnotsOnFields(SymImplementation impl) {

        JClassType sym = impl.getDeclaration(ClassWithTypeAnnotationsInside.class);

        assertHasTypeAnnots(getFieldType(sym, "intField"), ANNOT_A);
        assertHasTypeAnnots(getFieldType(sym, "annotOnList"), ANNOT_A);

        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnListArg");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getTypeArgs().get(0), ANNOT_A);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnBothListAndArg");
            assertHasTypeAnnots(t, ANNOT_A);
            assertHasTypeAnnots(t.getTypeArgs().get(0), ANNOT_A);
        }
    }


    @ParameterizedTest
    @EnumSource
    void testArrayTypeAnnotsOnFields(SymImplementation impl) {

        /*

            @A int[] annotOnArrayComponent;
            int @A [] annotOnArrayDimension;
            // this annotates the int[]
            int[] @A @B [] twoAnnotsOnOuterArrayDim;
            int @A [][] annotOnInnerArrayDim;
            int @A(1) [] @A(2) [] annotsOnBothArrayDims;

         */

        JClassType sym = impl.getDeclaration(ClassWithTypeAnnotationsInside.class);

        {
            JArrayType t = (JArrayType) getFieldType(sym, "annotOnArrayComponent");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getComponentType(), ANNOT_A);
            assertHasTypeAnnots(t.getElementType(), ANNOT_A);
        }
        {
            JArrayType t = (JArrayType) getFieldType(sym, "annotOnArrayDimension");
            assertHasTypeAnnots(t, ANNOT_A);
            assertHasTypeAnnots(t.getComponentType(), emptyList());
            assertHasTypeAnnots(t.getElementType(), emptyList());
        }
        {
            // int[] @A @B []
            JArrayType t = (JArrayType) getFieldType(sym, "twoAnnotsOnOuterArrayDim");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getComponentType(), ANNOTS_A_B);
            assertHasTypeAnnots(t.getElementType(), emptyList());
        }
        {
            // int @A(1) [] @A(2) [] annotsOnBothArrayDims;
            JArrayType t = (JArrayType) getFieldType(sym, "annotsOnBothArrayDims");
            assertHasTypeAnnots(t, listOf(makeAnA(1)));
            assertHasTypeAnnots(t.getComponentType(), listOf(makeAnA(2)));
            assertHasTypeAnnots(t.getElementType(), emptyList());
        }
    }

    private static @NonNull A makeAnA(int v0) {
        return TypeAnnotTestUtil.createAnnotationInstance(A.class, mapOf("value", v0));
    }


    @ParameterizedTest
    @EnumSource
    void testInnerTypeAnnotsOnFields(SymImplementation impl) {

        JClassType sym = impl.getDeclaration(ClassWithTypeAnnotationsInside.class);

        /*
    Outer. @A Inner1                    inner1WithAnnot;
    @A Outer. @B Inner1                 inner1WithAnnotOnOuterToo;
    @A Outer. @B Inner1.Inner2          inner2WithAnnotOnBothOuter;
    @A Outer. @A @B Inner1. @B Inner2   inner2WithAnnotOnAll;
    Outer. @A @B Inner1. @A Inner2      inner2WithAnnotOnAllExceptOuter;

         */

        {
            JClassType t = (JClassType) getFieldType(sym, "inner1WithAnnot");
            assertHasTypeAnnots(t, ANNOT_A);
            assertHasTypeAnnots(t.getEnclosingType(), emptyList());
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner1WithAnnotOnOuterToo");
            assertHasTypeAnnots(t, ANNOT_B);
            assertHasTypeAnnots(t.getEnclosingType(), ANNOT_A);
            assertNull(t.getEnclosingType().getEnclosingType());
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner2WithAnnotOnBothOuter");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getEnclosingType(), ANNOT_B);
            assertHasTypeAnnots(t.getEnclosingType().getEnclosingType(), ANNOT_A);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner2WithAnnotOnAll");
            assertHasTypeAnnots(t, ANNOT_B);
            assertHasTypeAnnots(t.getEnclosingType(), ANNOTS_A_B);
            assertHasTypeAnnots(t.getEnclosingType().getEnclosingType(), ANNOT_A);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner2WithAnnotOnAllExceptOuter");
            assertHasTypeAnnots(t, ANNOT_A);
            assertHasTypeAnnots(t.getEnclosingType(), ANNOTS_A_B);
            assertHasTypeAnnots(t.getEnclosingType().getEnclosingType(), emptyList());
        }
    }


    @ParameterizedTest
    @EnumSource
    void testInnerTypeAnnotsWithGenerics(SymImplementation impl) {

        JClassType sym = impl.getDeclaration(ClassWithTypeAnnotationsInside.class);

        /*

    OuterG<A, T>.@A Inner5 annotOnInnerWithOuterGeneric;
    OuterG<@A T, T>.@A Inner5 annotOnOuterGenericArg;
    OuterG<A, @A T>.@A Inner5 annotOnOuterGenericArg2;
    @A OuterG<A, @A T>.Inner5 annotOnOuterGenericArgAndOuter;
    @A OuterG<A, @A T>.@A InnerG<@A T> annotOnOuterGenericArgAndInner;

         */

        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnInnerWithOuterGeneric");
            assertHasTypeAnnots(t, ANNOT_A);
            assertHasTypeAnnots(t.getEnclosingType(), emptyList());
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArg");
            assertHasTypeAnnots(t, ANNOT_A);
            assertHasTypeAnnots(t.getEnclosingType(), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), ANNOT_A);
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), emptyList());
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArg2");
            assertHasTypeAnnots(t, ANNOT_A);
            assertHasTypeAnnots(t.getEnclosingType(), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), ANNOT_A);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArgAndOuter");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getEnclosingType(), ANNOT_A);
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), ANNOT_A);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArgAndInner");
            assertHasTypeAnnots(t, ANNOT_A);
            assertHasTypeAnnots(t.getTypeArgs().get(0), ANNOT_A);
            assertHasTypeAnnots(t.getEnclosingType(), ANNOT_A);
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), ANNOT_A);
        }
    }


    @ParameterizedTest
    @EnumSource
    void testTypeAnnotOnMultipleGenericsAndInner(SymImplementation impl) {

        JClassType sym = impl.getDeclaration(ClassWithTypeAnnotationsInside.class);

        /*

    @A OuterG<A, @A T>.@A InnerG<@A T> annotOnOuterGenericArgAndInner;

         */

        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArgAndInner");
            assertHasTypeAnnots(t, ANNOT_A);
            assertHasTypeAnnots(t.getTypeArgs().get(0), ANNOT_A);
            assertHasTypeAnnots(t.getEnclosingType(), ANNOT_A);
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), ANNOT_A);
        }
    }


    @ParameterizedTest
    @EnumSource
    void testTypeAnnotOnWildcards(SymImplementation impl) {

        JClassType sym = impl.getDeclaration(ClassWithTypeAnnotationsInside.class);

        /*

    OuterG<@A @B ? extends @B String, ? super @A @B T>          severalWildcards;
    @A OuterG<@A @B ? extends @B String, @A List<@A @B Object>> complicatedField;

         */

        {
            JClassType t = (JClassType) getFieldType(sym, "severalWildcards");
            assertHasTypeAnnots(t, emptyList());
            JWildcardType wild0 = (JWildcardType) t.getTypeArgs().get(0);
            assertHasTypeAnnots(wild0, ANNOT_A);
            assertTrue(wild0.isUpperBound());
            assertHasTypeAnnots(wild0.getBound(), ANNOT_B);

            JWildcardType wild1 = (JWildcardType) t.getTypeArgs().get(1);
            assertHasTypeAnnots(wild1, emptyList());
            assertTrue(wild1.isLowerBound());
            assertHasTypeAnnots(wild1.getBound(), ANNOTS_A_B);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "complicatedField");
            assertHasTypeAnnots(t, ANNOT_A);
            JWildcardType wild0 = (JWildcardType) t.getTypeArgs().get(0);
            assertHasTypeAnnots(wild0, ANNOTS_A_B);
            assertTrue(wild0.isUpperBound());
            assertHasTypeAnnots(wild0.getBound(), ANNOT_B);

            JClassType arg1 = (JClassType) t.getTypeArgs().get(1);
            assertHasTypeAnnots(arg1, ANNOT_A);
            assertHasTypeAnnots(arg1.getTypeArgs().get(0), ANNOTS_A_B);
        }
    }


}
