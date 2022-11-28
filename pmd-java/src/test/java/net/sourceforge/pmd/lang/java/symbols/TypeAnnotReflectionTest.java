/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import static java.util.Collections.emptyList;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
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


    @Test
    public void testTypeAnnotsOnFields() {

        JClassSymbol sym = loadClass(ClassWithTypeAnnotationsInside.class);

        List<Annotation> oneAnnot = listOf(new AnnotAImpl());
        assertHasTypeAnnots(getFieldType(sym, "intField"), oneAnnot);
        assertHasTypeAnnots(getFieldType(sym, "annotOnList"), oneAnnot);

        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnListArg");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getTypeArgs().get(0), oneAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnBothListAndArg");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getTypeArgs().get(0), oneAnnot);
        }
    }

    @Test
    public void testArrayTypeAnnotsOnFields() {

        JClassSymbol sym = loadClass(ClassWithTypeAnnotationsInside.class);

        List<Annotation> oneAnnot = listOf(new AnnotAImpl());
        {
            JArrayType t = (JArrayType) getFieldType(sym, "annotOnArrayComponent");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getComponentType(), oneAnnot);
            assertHasTypeAnnots(t.getElementType(), oneAnnot);
        }
        {
            JArrayType t = (JArrayType) getFieldType(sym, "annotOnArrayDimension");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getComponentType(), emptyList());
            assertHasTypeAnnots(t.getElementType(), emptyList());
        }
        {
            // int[] @A @B []
            JArrayType t = (JArrayType) getFieldType(sym, "twoAnnotsOnOuterArrayDim");
            assertHasTypeAnnots(t, emptyList());
            assertThat(t.getComponentType(), Matchers.isA(JArrayType.class));
            assertHasTypeAnnots(t.getComponentType(), listOf(new AnnotBImpl(), new AnnotAImpl()));
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

        JClassSymbol sym = loadClass(ClassWithTypeAnnotationsInside.class);

        List<Annotation> oneAnnot = listOf(new AnnotAImpl());
        /*
    Outer. @A Inner1                    inner1WithAnnot;
    @A Outer. @A Inner1                 inner1WithAnnotOnOuterToo;
    @A Outer. @A Inner1.Inner2          inner2WithAnnotOnBothOuter;
    @A Outer. @A @B Inner1. @A Inner2   inner2WithAnnotOnAll;
    Outer. @A @B Inner1. @A Inner2      inner2WithAnnotOnAllExceptOuter;

         */

        {
            JClassType t = (JClassType) getFieldType(sym, "inner1WithAnnot");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), emptyList());
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner1WithAnnotOnOuterToo");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), oneAnnot);
            assertNull(t.getEnclosingType().getEnclosingType());
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner2WithAnnotOnBothOuter");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getEnclosingType(), oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType().getEnclosingType(), oneAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner2WithAnnotOnAll");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), listOf(new AnnotAImpl(), new AnnotBImpl()));
            assertHasTypeAnnots(t.getEnclosingType().getEnclosingType(), oneAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner2WithAnnotOnAllExceptOuter");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), listOf(new AnnotAImpl(), new AnnotBImpl()));
            assertHasTypeAnnots(t.getEnclosingType().getEnclosingType(), emptyList());
        }
    }

    @Test
    public void testInnerTypeAnnotsWithGenerics() {

        JClassSymbol sym = loadClass(ClassWithTypeAnnotationsInside.class);

        List<Annotation> oneAnnot = listOf(new AnnotAImpl());
        /*

    OuterG<A, T>.@A Inner5 annotOnInnerWithOuterGeneric;
    OuterG<@A T, T>.@A Inner5 annotOnOuterGenericArg;
    OuterG<A, @A T>.@A Inner5 annotOnOuterGenericArg2;
    @A OuterG<A, @A T>.Inner5 annotOnOuterGenericArgAndOuter;
    @A OuterG<A, @A T>.@A InnerG<@A T> annotOnOuterGenericArgAndInner;

         */

        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnInnerWithOuterGeneric");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), emptyList());
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArg");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), emptyList());
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArg2");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), oneAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArgAndOuter");
            assertHasTypeAnnots(t, emptyList());
            assertHasTypeAnnots(t.getEnclosingType(), oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), oneAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArgAndInner");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getTypeArgs().get(0), oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), oneAnnot);
        }
    }

    @Test
    public void testTypeAnnotOnMultipleGenericsAndInner() {

        JClassSymbol sym = loadClass(ClassWithTypeAnnotationsInside.class);

        List<Annotation> oneAnnot = listOf(new AnnotAImpl());
        /*

    @A OuterG<A, @A T>.@A InnerG<@A T> annotOnOuterGenericArgAndInner;

         */

        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnOuterGenericArgAndInner");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getTypeArgs().get(0), oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(0), emptyList());
            assertHasTypeAnnots(t.getEnclosingType().getTypeArgs().get(1), oneAnnot);
        }
    }

    @Test
    public void testTypeAnnotOnWildcards() {

        JClassSymbol sym = loadClass(ClassWithTypeAnnotationsInside.class);

        List<Annotation> aAnnot = listOf(new AnnotAImpl());
        List<Annotation> bAnnot = listOf(new AnnotBImpl());
        List<Annotation> aAndBAnnot = listOf(new AnnotAImpl(), new AnnotBImpl());
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


    private @NonNull JClassSymbol loadClass(Class<?> klass) {
        JClassSymbol sym = ts.getClassSymbol(klass.getName());
        Assert.assertNotNull(sym);
        return sym;
    }


    private void assertHasTypeAnnots(JTypeMirror t, List<Annotation> annots) {
        assertThat(t.getTypeAnnotations(), Matchers.hasItems(annots.stream().map(this::matchesAnnot).toArray(Matcher[]::new)));
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

    private Matcher<SymAnnot> matchesAnnot(Annotation o) {
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
