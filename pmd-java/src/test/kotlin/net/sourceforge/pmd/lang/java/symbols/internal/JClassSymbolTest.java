/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.testdata.TypeAnnotation;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * Tests that test both AST and ASM symbols.
 *
 * @author Clément Fournier
 */
public class JClassSymbolTest {

    private final TypeSystem ts = JavaParsingHelper.TEST_TYPE_SYSTEM;

    @EnumSource
    @ParameterizedTest
    void testAnnotationAttributes(SymImplementation impl) {
        JClassSymbol sym = impl.getSymbol(ts, TypeAnnotation.class);

        assertEquals(RetentionPolicy.RUNTIME, sym.getAnnotationRetention());
    }
}
