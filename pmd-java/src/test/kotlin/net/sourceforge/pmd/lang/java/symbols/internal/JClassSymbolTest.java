/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
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
            assertEquals(type != ElementType.TYPE_PARAMETER, sym.annotationAppliesTo(type),
                         "annot supports " + type);
        }
    }
}
