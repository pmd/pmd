/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.groovy.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class GroovyCpdLexerTest extends CpdTextComparisonTest {

    GroovyCpdLexerTest() {
        super("groovy", ".groovy");
    }

    @Test
    void testSample() {
        doTest("sample");
    }
    
    @Test
    void testCpdOffAndOn() {
        doTest("cpdoff");
    }

    @Test
    void testGStringsEndingWithInterpolatedVariable() {
        // https://github.com/pmd/pmd/issues/7100
        doTest("gstrings");
    }

    @Test
    void testLexerSyntaxErrorIsReportedAsLexException() {
        // https://github.com/pmd/pmd/issues/7100
        // Groovy's lexer throws GroovySyntaxError (an AssertionError) for some errors
        LexException e = expectLexException("def a = `x`\n");
        assertEquals(1, e.getLine());
        assertEquals(9, e.getColumn());
    }
}
