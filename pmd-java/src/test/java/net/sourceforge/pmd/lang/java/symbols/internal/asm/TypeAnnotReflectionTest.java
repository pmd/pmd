/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import static java.util.Collections.emptyList;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsInside;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JWildcardType;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 *
 */
public class TypeAnnotReflectionTest {

    private final TypeSystem ts = JavaParsingHelper.TEST_TYPE_SYSTEM;


    private final JClassSymbol sym = Objects.requireNonNull(ts.getClassSymbol(ClassWithTypeAnnotationsInside.class.getName()));
    private final List<Annotation> aAnnot = listOf(new AnnotAImpl());
    private final List<Annotation> bAnnot = listOf(new AnnotBImpl());
    private final List<Annotation> aAndBAnnot = listOf(new AnnotAImpl(), new AnnotBImpl());

    @Test
    public void testTypeAnnotsOnFields() {


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

    @Test
    public void testArrayTypeAnnotsOnFields() {


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
            assertThat(t.getComponentType(), Matchers.isA(JArrayType.class));
            assertHasTypeAnnots(t.getComponentType(), aAndBAnnot);
            assertHasTypeAnnots(t.getElementType(), emptyList());
        }
        {
            // int @A(1) [] @A(2) [] annotsOnBothArrayDims;
            JArrayType t = (JArrayType) getFieldType(sym, "annotsOnBothArrayDims");
            assertHasTypeAnnots(t, listOf(new AnnotAImpl(1)));
            assertHasTypeAnnots(t.getComponentType(), listOf(new AnnotAImpl(2)));
            assertHasTypeAnnots(t.getElementType(), emptyList());
        }
    }

    @Test
    public void testInnerTypeAnnotsOnFields() {


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

    @Test
    public void testInnerTypeAnnotsWithGenerics() {


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

    @Test
    public void testTypeAnnotOnMultipleGenericsAndInner() {


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

    @Test
    public void testTypeAnnotOnWildcards() {


        List<Annotation> aAnnot = listOf(new AnnotAImpl());
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
            assertHasTypeAnnots(wild1,emptyList());
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
            assertHasTypeAnnots(arg1,aAnnot);
            assertHasTypeAnnots(arg1.getTypeArgs().get(0), aAndBAnnot);
        }
    }

    private static JTypeMirror getFieldType(JClassSymbol sym, String fieldName) {
        return sym.getDeclaredField(fieldName).getTypeMirror(Substitution.EMPTY);
    }


    static void assertHasTypeAnnots(JTypeMirror t, List<Annotation> annots) {
        assertThat(t.getTypeAnnotations(), Matchers.hasItems(annots.stream().map(TypeAnnotReflectionTest::matchesAnnot).toArray(Matcher[]::new)));
    }

    static final class AnnotAImpl implements ClassWithTypeAnnotationsInside.A {

        private final int val;

        AnnotAImpl(int val) {
            this.val = val;
        }

        AnnotAImpl() {
            this.val = 1; // the default declared in interface
        }

        @Override
        public int value() {
            return val;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return ClassWithTypeAnnotationsInside.A.class;
        }

        @Override
        public String toString() {
            return "@A(" + value() + ")";
        }
    }

    static final class AnnotBImpl implements ClassWithTypeAnnotationsInside.B {

        @Override
        public Class<? extends Annotation> annotationType() {
            return ClassWithTypeAnnotationsInside.B.class;
        }

        @Override
        public String toString() {
            return "@B";
        }
    }

    private static Matcher<SymAnnot> matchesAnnot(Annotation o) {
        return new BaseMatcher<SymAnnot>() {
            @Override
            public boolean matches(Object actual) {
                return actual instanceof SymAnnot && ((SymAnnot) actual).valueEquals(o);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("an annotation like " + o);
            }
        };
    }


}
