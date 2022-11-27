/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import static java.util.Collections.emptyList;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

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
import net.sourceforge.pmd.lang.java.symbols.testdata.SecondTypeUseAnnot;
import net.sourceforge.pmd.lang.java.symbols.testdata.TypeUseAnnot;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
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

        List<Annotation> oneAnnot = listOf(new TypeUseAnnotImpl());
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

        List<Annotation> oneAnnot = listOf(new TypeUseAnnotImpl());
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
            // int[] @TypeUseAnnot @SecondTypeUseAnnot []
            JArrayType t = (JArrayType) getFieldType(sym, "twoAnnotsOnOuterArrayDim");
            assertHasTypeAnnots(t, emptyList());
            assertThat(t.getComponentType(), Matchers.isA(JArrayType.class));
            assertHasTypeAnnots(t.getComponentType(), listOf(new TypeUseAnnotImpl(), new SecondTypeUseAnnotImpl()));
            assertHasTypeAnnots(t.getElementType(), emptyList());
        }
        {
            // int @TypeUseAnnot(1) [] @TypeUseAnnot(2) [] annotsOnBothArrayDims;
            JArrayType t = (JArrayType) getFieldType(sym, "annotsOnBothArrayDims");
            assertHasTypeAnnots(t, listOf(new TypeUseAnnotImpl(1)));
            assertHasTypeAnnots(t.getComponentType(), listOf(new TypeUseAnnotImpl(2)));
            assertHasTypeAnnots(t.getElementType(), emptyList());
        }
    }

    @Test
    public void testInnerTypeAnnotsOnFields() {

        JClassSymbol sym = loadClass(ClassWithTypeAnnotationsInside.class);

        List<Annotation> oneAnnot = listOf(new TypeUseAnnotImpl());
        /*
    ClassWithTypeAnnotationsInside. @TypeUseAnnot Inner1 inner1WithAnnot;
    @TypeUseAnnot ClassWithTypeAnnotationsInside. @TypeUseAnnot Inner1 inner1WithAnnotOnOuterToo;
    @TypeUseAnnot ClassWithTypeAnnotationsInside. @TypeUseAnnot Inner1.Inner2 inner2WithAnnotOnBothOuter;
    @TypeUseAnnot ClassWithTypeAnnotationsInside. @TypeUseAnnot @SecondTypeUseAnnot Inner1. @TypeUseAnnot Inner2 inner2WithAnnotOnAll;
    ClassWithTypeAnnotationsInside. @TypeUseAnnot @SecondTypeUseAnnot Inner1. @TypeUseAnnot Inner2 inner2WithAnnotOnAllExceptOuter;

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
            assertHasTypeAnnots(t.getEnclosingType(), listOf(new TypeUseAnnotImpl(), new SecondTypeUseAnnotImpl()));
            assertHasTypeAnnots(t.getEnclosingType().getEnclosingType(), oneAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "inner2WithAnnotOnAllExceptOuter");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getEnclosingType(), listOf(new TypeUseAnnotImpl(), new SecondTypeUseAnnotImpl()));
            assertHasTypeAnnots(t.getEnclosingType().getEnclosingType(), emptyList());
        }
    }

    @Test
    public void testInnerTypeAnnotsWithGenerics() {

        JClassSymbol sym = loadClass(ClassWithTypeAnnotationsInside.class);

        List<Annotation> oneAnnot = listOf(new TypeUseAnnotImpl());
        /*

    OuterG<A, A>.@TypeUseAnnot Inner5 annotOnInnerWithOuterGeneric;
    OuterG<@TypeUseAnnot A, A>.@TypeUseAnnot Inner5 annotOnOuterGenericArg;
    OuterG<A, @TypeUseAnnot A>.@TypeUseAnnot Inner5 annotOnOuterGenericArg2;
    @TypeUseAnnot OuterG<A, @TypeUseAnnot A>.Inner5 annotOnOuterGenericArgAndOuter;
    @TypeUseAnnot OuterG<A, @TypeUseAnnot A>.@TypeUseAnnot InnerG<@TypeUseAnnot A> annotOnOuterGenericArgAndInner;

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

    private static JTypeMirror getFieldType(JClassSymbol sym, String fieldName) {
        return sym.getDeclaredField(fieldName).getTypeMirror(Substitution.EMPTY);
    }


    private @NonNull JClassSymbol loadClass(Class<?> klass) {
        JClassSymbol sym = ts.getClassSymbol(klass.getName());
        Assert.assertNotNull(sym);
        return sym;
    }


    private void assertHasTypeAnnots(JTypeMirror t, List<Annotation> annots) {
        assertThat(t.getTypeAnnotations().toArray(), Matchers.array(annots.stream().map(this::matchesAnnot).toArray(Matcher[]::new)));
    }

    static final class TypeUseAnnotImpl implements TypeUseAnnot {

        private final int val;

        TypeUseAnnotImpl(int val) {
            this.val = val;
        }

        TypeUseAnnotImpl() {
            this.val = 1; // the default declared in interface
        }

        @Override
        public int value() {
            return val;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return TypeUseAnnot.class;
        }

        @Override
        public String toString() {
            return "@TypeUseAnnot(" + value() + ")";
        }
    }

    static final class SecondTypeUseAnnotImpl implements SecondTypeUseAnnot {

        @Override
        public Class<? extends Annotation> annotationType() {
            return SecondTypeUseAnnot.class;
        }

        @Override
        public String toString() {
            return "@SecondTypeUseAnnot";
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
