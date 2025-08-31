/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.SymImplementation.Fixture;
import net.sourceforge.pmd.lang.java.symbols.testdata.AnnotationWithNoRetention;
import net.sourceforge.pmd.lang.java.symbols.testdata.TypeAnnotation;

/**
 * Tests that test both AST and ASM symbols.
 *
 * @author Clément Fournier
 */
class JClassSymbolTest {

    @EnumSource
    @ParameterizedTest
    void testAnnotationAttributes(SymImplementation impl) {
        JClassSymbol sym = impl.getSymbol(TypeAnnotation.class);

        assertEquals(RetentionPolicy.RUNTIME, sym.getAnnotationRetention());
    }


    @EnumSource
    @ParameterizedTest
    void testAnnotWithNoRetention(SymImplementation impl) {
        JClassSymbol sym = impl.getSymbol(AnnotationWithNoRetention.class);

        assertEquals(RetentionPolicy.CLASS, sym.getAnnotationRetention());
    }


    @EnumSource
    @ParameterizedTest
    void testAnnotWithNoTarget(SymImplementation impl) {
        JClassSymbol sym = impl.getSymbol(AnnotationWithNoRetention.class);

        for (ElementType type : ElementType.values()) {
            assertEquals(type != ElementType.TYPE_PARAMETER
                && type != ElementType.TYPE_USE, sym.annotationAppliesTo(type),
                         "annot supports " + type);
        }
    }

    private static final String SEALED_TESTDATA = "net.sourceforge.pmd.lang.java.symbols.testdata.sealed.";

    @EnumSource
    @ParameterizedTest
    void testSealedInterfaces(SymImplementation impl) {
        Fixture fixture = impl.findClass(SEALED_TESTDATA + "SealedTypesTestData");

        JClassSymbol stTestData = fixture.getSymbol("SealedTypesTestData");
        JClassSymbol stA = fixture.getSymbol("A");
        JClassSymbol stB = fixture.getSymbol("B");
        JClassSymbol stC = fixture.getSymbol("C");
        JClassSymbol stX = fixture.getSymbol("X");

        assertIsSealed(stTestData, stA, stB, stC);
        assertIsSealed(stA, stX);

        assertNotSealed(stB);

        assertNotSealed(stC);
        assertTrue(stC.isFinal(), "final");

        assertNotSealed(stX);
        assertTrue(stX.isFinal(), "final");
    }


    @EnumSource
    @ParameterizedTest
    void testImplicitPermitsClause(SymImplementation impl) {
        Fixture fixture = impl.findClass(SEALED_TESTDATA + "ImplicitPermitsClause");

        JClassSymbol sealedClass = fixture.getSymbol("ImplicitPermitsClause");
        JClassSymbol sealedItf = fixture.getSymbol("ImplicitPermitsClauseItf");
        JClassSymbol bar = fixture.getSymbol("Foo$Bar");
        JClassSymbol qux = fixture.getSymbol("Qux");
        JClassSymbol subitf = fixture.getSymbol("SubItf");
        JClassSymbol subitf2 = fixture.getSymbol("SubItf2");
        JClassSymbol fooEnum = fixture.getSymbol("FooEnum");
        JClassSymbol fooRecord = fixture.getSymbol("FooRecord");

        assertIsSealed(sealedClass, bar, qux);
        assertIsSealed(sealedItf, bar, subitf);

        assertNotSealed(bar);
        assertNotSealed(qux);
        assertNotSealed(subitf);

        assertIsSealed(subitf2, fooEnum, fooRecord);
        assertNotSealed(fooEnum);
    }

    private static void assertIsSealed(JClassSymbol sealedClass, JClassSymbol... permittedSubtypes) {
        assertTrue(sealedClass.isSealed(), "sealed");
        assertThat(sealedClass.getPermittedSubtypes(), containsInAnyOrder(permittedSubtypes));
    }

    private static void assertNotSealed(JClassSymbol sealedClass) {
        assertFalse(sealedClass.isSealed(), "sealed");
        assertThat(sealedClass.getPermittedSubtypes(), empty());
    }

}
