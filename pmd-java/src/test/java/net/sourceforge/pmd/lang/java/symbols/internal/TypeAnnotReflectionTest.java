/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.aAndBAnnot;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.aAnnot;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.assertHasTypeAnnots;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.bAnnot;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.getFieldType;
import static net.sourceforge.pmd.util.CollectionUtil.emptyList;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.SymImplementation;
import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsInside;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JWildcardType;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 *
 */
public class TypeAnnotReflectionTest {

    private final TypeSystem ts = JavaParsingHelper.TEST_TYPE_SYSTEM;


    @ParameterizedTest
    @EnumSource
    public void testTypeAnnotsOnFields(SymImplementation impl) {

        JClassType sym = impl.getDeclaration(ts, ClassWithTypeAnnotationsInside.class);

        assertHasTypeAnnots(getFieldType(sym, "intField"), aAnnot);
        assertHasTypeAnnots(getFieldType(sym, "annotOnList"), aAnnot);

        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnListArg");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getTypeArgs().get(0), aAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnBothListAndArg");
            assertHasTypeAnnots(t, aAnnot);
            assertHasTypeAnnots(t.getTypeArgs().get(0), aAnnot);
        }
    }


    @ParameterizedTest
    @EnumSource
    public void testArrayTypeAnnotsOnFields(SymImplementation impl) {

        /*

            @A int[] annotOnArrayComponent;
            int @A [] annotOnArrayDimension;
            // this annotates the int[]
            int[] @A @B [] twoAnnotsOnOuterArrayDim;
            int @A [][] annotOnInnerArrayDim;
            int @A(1) [] @A(2) [] annotsOnBothArrayDims;

         */

        JClassType sym = impl.getDeclaration(ts, ClassWithTypeAnnotationsInside.class);

        {
            JArrayType t = (JArrayType) getFieldType(sym, "annotOnArrayComponent");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getComponentType(), aAnnot);
            assertHasTypeAnnots(t.getElementType(), aAnnot);
        }
        {
            JArrayType t = (JArrayType) getFieldType(sym, "annotOnArrayDimension");
            assertHasTypeAnnots(t, aAnnot);
            assertHasTypeAnnots(t.getComponentType(), emptyList());
            assertHasTypeAnnots(t.getElementType(), emptyList());
        }
        {
            // int[] @A @B []
            JArrayType t = (JArrayType) getFieldType(sym, "twoAnnotsOnOuterArrayDim");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getComponentType(), aAndBAnnot);
            assertHasTypeAnnots(t.getElementType(), emptyList());
        }
        {
            // int @A(1) [] @A(2) [] annotsOnBothArrayDims;
            JArrayType t = (JArrayType) getFieldType(sym, "annotsOnBothArrayDims");
            assertHasTypeAnnots(t, listOf(new TypeAnnotTestUtil.AnnotAImpl(1)));
            assertHasTypeAnnots(t.getComponentType(), listOf(new TypeAnnotTestUtil.AnnotAImpl(2)));
            assertHasTypeAnnots(t.getElementType(), emptyList());
        }
    }


    @ParameterizedTest
    @EnumSource
    public void testInnerTypeAnnotsOnFields(SymImplementation impl) {

        JClassType sym = impl.getDeclaration(ts, ClassWithTypeAnnotationsInside.class);

        /*
    Outer. @A Inner1                    inner1WithAnnot;
    @A Outer. @A Inner1                 inner1WithAnnotOnOuterToo;
    @A Outer. @A Inner1.Inner2          inner2WithAnnotOnBothOuter;
    @A Outer. @A @B Inner1. @A Inner2   inner2WithAnnotOnAll;
    Outer. @A @B Inner1. @A Inner2      inner2WithAnnotOnAllExceptOuter;

         */

        {
            JClassType t = (JClassType) getFieldType(sym, "inner1WithAnnot");
            assertHasTypeAnnots(t, aAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), emptyList());
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner1WithAnnotOnOuterToo");
            assertHasTypeAnnots(t, aAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), aAnnot);
            assertNull(t.getEnclosingType().getEnclosingType());
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner2WithAnnotOnBothOuter");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getEnclosingType(), aAnnot);
            assertHasTypeAnnots(t.getEnclosingType().getEnclosingType(), aAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner2WithAnnotOnAll");
            assertHasTypeAnnots(t, aAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), aAndBAnnot);
            assertHasTypeAnnots(t.getEnclosingType().getEnclosingType(), aAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner2WithAnnotOnAllExceptOuter");
            assertHasTypeAnnots(t, aAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), aAndBAnnot);
            assertHasTypeAnnots(t.getEnclosingType().getEnclosingType(), emptyList());
        }
    }


    @ParameterizedTest
    @EnumSource
    public void testInnerTypeAnnotsWithGenerics(SymImplementation impl) {

        JClassType sym = impl.getDeclaration(ts, ClassWithTypeAnnotationsInside.class);

        /*

    OuterG<A, T>.@A Inner5 annotOnInnerWithOuterGeneric;
    OuterG<@A T, T>.@A Inner5 annotOnOuterGenericArg;
    OuterG<A, @A T>.@A Inner5 annotOnOuterGenericArg2;
    @A OuterG<A, @A T>.Inner5 annotOnOuterGenericArgAndOuter;
    @A OuterG<A, @A T>.@A InnerG<@A T> annotOnOuterGenericArgAndInner;

         */

        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnInnerWithOuterGeneric");
            assertHasTypeAnnots(t, aAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), emptyList());
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArg");
            assertHasTypeAnnots(t, aAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), aAnnot);
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), emptyList());
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArg2");
            assertHasTypeAnnots(t, aAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), aAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArgAndOuter");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getEnclosingType(), aAnnot);
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), aAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArgAndInner");
            assertHasTypeAnnots(t, aAnnot);
            assertHasTypeAnnots(t.getTypeArgs().get(0), aAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), aAnnot);
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), aAnnot);
        }
    }


    @ParameterizedTest
    @EnumSource
    public void testTypeAnnotOnMultipleGenericsAndInner(SymImplementation impl) {

        JClassType sym = impl.getDeclaration(ts, ClassWithTypeAnnotationsInside.class);

        /*

    @A OuterG<A, @A T>.@A InnerG<@A T> annotOnOuterGenericArgAndInner;

         */

        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArgAndInner");
            assertHasTypeAnnots(t, aAnnot);
            assertHasTypeAnnots(t.getTypeArgs().get(0), aAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), aAnnot);
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), aAnnot);
        }
    }


    @ParameterizedTest
    @EnumSource
    public void testTypeAnnotOnWildcards(SymImplementation impl) {

        JClassType sym = impl.getDeclaration(ts, ClassWithTypeAnnotationsInside.class);

        /*

    OuterG<@A @B ? extends @B String, ? super @A @B T>          severalWildcards;
    @A OuterG<@A @B ? extends @B String, @A List<@A @B Object>> complicatedField;

         */

        {
            JClassType t = (JClassType) getFieldType(sym, "severalWildcards");
            assertHasTypeAnnots(t, emptyList());
            JWildcardType wild0 = (JWildcardType) t.getTypeArgs().get(0);
            assertHasTypeAnnots(wild0, aAnnot);
            assertTrue(wild0.isUpperBound());
            assertHasTypeAnnots(wild0.getBound(), bAnnot);

            JWildcardType wild1 = (JWildcardType) t.getTypeArgs().get(1);
            assertHasTypeAnnots(wild1, emptyList());
            assertTrue(wild1.isLowerBound());
            assertHasTypeAnnots(wild1.getBound(), aAndBAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "complicatedField");
            assertHasTypeAnnots(t, aAnnot);
            JWildcardType wild0 = (JWildcardType) t.getTypeArgs().get(0);
            assertHasTypeAnnots(wild0, aAndBAnnot);
            assertTrue(wild0.isUpperBound());
            assertHasTypeAnnots(wild0.getBound(), bAnnot);

            JClassType arg1 = (JClassType) t.getTypeArgs().get(1);
            assertHasTypeAnnots(arg1, aAnnot);
            assertHasTypeAnnots(arg1.getTypeArgs().get(0), aAndBAnnot);
        }
    }


}
