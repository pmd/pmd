/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import static java.util.Collections.emptySet;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.createAnnotationInstance;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.getMethodSym;
import static net.sourceforge.pmd.util.CollectionUtil.mapOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Objects;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.SymImplementation;
import net.sourceforge.pmd.lang.java.symbols.testdata.AnnotWithDefaults;
import net.sourceforge.pmd.lang.java.symbols.testdata.ConstructorAnnotation;
import net.sourceforge.pmd.lang.java.symbols.testdata.FieldAnnotation;
import net.sourceforge.pmd.lang.java.symbols.testdata.LocalVarAnnotation;
import net.sourceforge.pmd.lang.java.symbols.testdata.MethodAnnotation;
import net.sourceforge.pmd.lang.java.symbols.testdata.ParameterAnnotation;
import net.sourceforge.pmd.lang.java.symbols.testdata.SomeClass;

class AnnotationReflectionTest {

    @ParameterizedTest
    @EnumSource
    void testReflectionOfClassMethods(SymImplementation impl) {
        Class<SomeClass> actualClass = SomeClass.class;
        JClassSymbol sym = impl.getSymbol(actualClass);
    
        impl.assertAllMethodsMatch(actualClass, sym);
        impl.assertAllFieldsMatch(actualClass, sym);
    }

    @ParameterizedTest
    @EnumSource
    void testReflectionOfAnnotDefaults(SymImplementation impl) {
        Class<AnnotWithDefaults> actualClass = AnnotWithDefaults.class;
        JClassSymbol sym = impl.getSymbol(actualClass);
        
        impl.assertAllMethodsMatch(actualClass, sym);
    }


    @ParameterizedTest
    @EnumSource
    void testAnnotUseThatUsesDefaults(SymImplementation impl) {
        // note that as the annotation has retention class, we can't use reflection to check

        /*
            @AnnotWithDefaults(valueNoDefault = "ohio",
                       stringArrayDefault = {})
         */
        JClassSymbol sym = impl.getSymbol(SomeClass.class);

        assertTrue(sym.isAnnotationPresent(AnnotWithDefaults.class));

        SymAnnot target = sym.getDeclaredAnnotation(AnnotWithDefaults.class);

        // explicit values are known
        assertEquals(YES, target.attributeMatches("valueNoDefault", "ohio"));
        assertEquals(YES, target.attributeMatches("stringArrayDefault", new String[] { }));
        assertEquals(NO, target.attributeMatches("stringArrayDefault", "0"));

        // default values are also known
        assertEquals(YES, target.attributeMatches("stringArrayEmptyDefault", new String[] { }));
        assertEquals(NO, target.attributeMatches("stringArrayEmptyDefault", new String[] { "a" }));

        // Non existing values are always considered unknown
        assertEquals(UNKNOWN, target.attributeMatches("attributeDoesNotExist", new String[] { "a" }));
    }

    @ParameterizedTest
    @EnumSource
    void testAnnotOnAnnot(SymImplementation impl) {
        // This only checks for Target.ANNOTATION_TYPE annotations
        Class<AnnotWithDefaults> actualClass = AnnotWithDefaults.class;
        JClassSymbol sym = impl.getSymbol(actualClass);

        Target targetAnnot = createAnnotationInstance(Target.class, mapOf("value", new ElementType[] { ElementType.TYPE, ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD }));
        assertHasAnnotations(setOf(targetAnnot), sym);
    }

    @ParameterizedTest
    @EnumSource
    void testAnnotOnParameter(SymImplementation impl) {
        // This only checks Target.PARAMETER annotations, do not confuse with TYPE_PARAMETER / TYPE_USE
        Class<SomeClass> actualClass = SomeClass.class;
        JClassSymbol sym = impl.getSymbol(actualClass);

        ParameterAnnotation annot = createAnnotationInstance(ParameterAnnotation.class);

        JMethodSymbol method = getMethodSym(sym, "withAnnotatedParam");
        assertHasAnnotations(emptySet(), method.getFormalParameters().get(0));
        assertHasAnnotations(setOf(annot), method.getFormalParameters().get(1));
    }

    @ParameterizedTest
    @EnumSource
    void testAnnotOnField(SymImplementation impl) {
        // This only checks Target.FIELD annotations, do not confuse with TYPE_USE annotations
        Class<SomeClass> actualClass = SomeClass.class;
        JClassSymbol sym = impl.getSymbol(actualClass);

        JFieldSymbol f1 = sym.getDeclaredField("f1");
        assertHasAnnotations(setOf(createAnnotationInstance(FieldAnnotation.class)), f1);
    }
    
    @ParameterizedTest
    @EnumSource
    void testAnnotOnMethod(SymImplementation impl) {
        // This only checks Target.METHOD annotations, do not confuse with TYPE_USE on return types
        Class<SomeClass> actualClass = SomeClass.class;
        JClassSymbol sym = impl.getSymbol(actualClass);

        JMethodSymbol method = getMethodSym(sym, "anotatedMethod");
        assertHasAnnotations(emptySet(), method.getFormalParameters().get(0));
        assertHasAnnotations(setOf(createAnnotationInstance(MethodAnnotation.class)), method);
    }
    
    @ParameterizedTest
    @EnumSource
    void testAnnotOnConstructor(SymImplementation impl) {
        // This only checks Target.CONSTRUCTOR annotations, do not confuse with TYPE_USE on return types
        Class<SomeClass> actualClass = SomeClass.class;
        JClassSymbol sym = impl.getSymbol(actualClass);

        JConstructorSymbol ctor = sym.getConstructors().get(0);
        assertHasAnnotations(setOf(createAnnotationInstance(ConstructorAnnotation.class)), ctor);
    }

    @Test
    void testAnnotOnLocalVar() {
        // This only checks Target.LOCAL_VAR annotations, do not confuse with TYPE_USE on return types
        JClassSymbol sym = SymImplementation.AST.getSymbol(SomeClass.class);

        @NonNull JVariableSymbol localSym = Objects.requireNonNull(sym.tryGetNode())
                                                   .descendants(ASTVariableDeclaratorId.class)
                                                   .filter(it -> "local".equals(it.getName()))
                                                   .firstOrThrow()
                                                   .getSymbol();

        assertHasAnnotations(setOf(createAnnotationInstance(LocalVarAnnotation.class)), localSym);
    }

    @Test
    void testAnnotWithInvalidType() {
        @NonNull ASTVariableDeclaratorId field =
            JavaParsingHelper.DEFAULT.parse(
                "@interface A {}\n"
                    + "class C<A> { @A int a; }\n"
            ).descendants(ASTFieldDeclaration.class).firstOrThrow().getVarIds().firstOrThrow();

        // The annotation actually refers to the type parameter A. Since
        // this is invalid code, it is filtered out.
        PSet<SymAnnot> annots = field.getSymbol().getDeclaredAnnotations();
        assertThat(annots, empty());

    }

    private void assertHasAnnotations(Set<? extends Annotation> expected, AnnotableSymbol annotable) {
        assertNotNull(annotable);
        assertThat(annotable.getDeclaredAnnotations(), hasSize(expected.size()));

        for (Annotation annot : expected) {
            Class<? extends Annotation> annotType = annot.annotationType();
            SymAnnot symAnnot = annotable.getDeclaredAnnotation(annotType);

            assertEquals(SymbolicValue.of(annotable.getTypeSystem(), annot), symAnnot);

            assertTrue(annotable.isAnnotationPresent(annotType));
            assertTrue(symAnnot.isOfType(annotType));
            assertTrue(symAnnot.valueEquals(annot));
            assertTrue(symAnnot.isOfType(annotType.getName()));
        }
    }


}
