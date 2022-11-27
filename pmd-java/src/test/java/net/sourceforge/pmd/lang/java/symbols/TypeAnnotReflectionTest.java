/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsInside;
import net.sourceforge.pmd.lang.java.symbols.testdata.TypeUseAnnot;
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
            assertHasTypeAnnots(t, Collections.emptyList());
            assertHasTypeAnnots(t.getTypeArgs().get(0), oneAnnot);
        }
        {
            JClassType t = (JClassType) getFieldType(sym, "annotOnBothListAndArg");
            assertHasTypeAnnots(t, oneAnnot);
            assertHasTypeAnnots(t.getTypeArgs().get(0), oneAnnot);
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
        assertThat(t.getTypeAnnotations(), CoreMatchers.hasItems(annots.stream().map(this::matchesAnnot).toArray(Matcher[]::new)));
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
