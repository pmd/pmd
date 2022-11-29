/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsInside;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 *
 */
public class TypeAnnotTestUtil {


    static final List<Annotation> aAnnot = listOf(new AnnotAImpl());
    static final List<Annotation> bAnnot = listOf(new AnnotBImpl());
    static final List<Annotation> aAndBAnnot = listOf(new AnnotAImpl(), new AnnotBImpl());


    /**
     * Use with {@link ParameterizedTest} and {@link EnumSource}.
     */
    enum SymImplementation {
        ASM {
            @Override
            JClassSymbol getSymbol(TypeSystem ts, Class<?> aClass) {
                return ts.getClassSymbol(aClass);
            }
        },
        AST {
            @Override
            JClassSymbol getSymbol(TypeSystem ts, Class<?> aClass) {
                ASTCompilationUnit ast = JavaParsingHelper.DEFAULT.withTypeSystem(ts).parseClass(aClass);
                return ast.getTypeDeclarations().first(it -> it.getSimpleName().equals(aClass.getSimpleName())).getSymbol();
            }

            @Override
            JClassType getDeclaration(TypeSystem ts, Class<?> aClass) {
                ASTCompilationUnit ast = JavaParsingHelper.DEFAULT.withTypeSystem(ts).parseClass(aClass);
                return ast.getTypeDeclarations().first(it -> it.getSimpleName().equals(aClass.getSimpleName())).getTypeMirror();
            }
        };

        abstract JClassSymbol getSymbol(TypeSystem ts, Class<?> aClass);

        JClassType getDeclaration(TypeSystem ts, Class<?> aClass) {
            return (JClassType) ts.declaration(getSymbol(ts, aClass));
        }
    }


    static JTypeMirror getFieldType(JClassType sym, String fieldName) {
        return sym.getDeclaredField(fieldName).getTypeMirror();
    }


    static JMethodSig getMethodType(JClassType sym, String fieldName) {
        return sym.streamMethods(it -> it.nameEquals(fieldName)).findFirst().get();
    }


    static void assertHasTypeAnnots(JTypeMirror t, List<Annotation> annots) {
        Objects.requireNonNull(t);
        Objects.requireNonNull(annots);
        assertThat(t.getTypeAnnotations(), Matchers.hasItems(annots.stream().map(TypeAnnotTestUtil::matchesAnnot).toArray(Matcher[]::new)));
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
