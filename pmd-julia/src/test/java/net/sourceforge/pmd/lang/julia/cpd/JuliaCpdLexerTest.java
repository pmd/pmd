/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.julia.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.julia.JuliaLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class JuliaCpdLexerTest extends CpdTextComparisonTest {
    JuliaCpdLexerTest() {
        super(JuliaLanguageModule.getInstance(), ".jl");
    }

    @Test
    void testMathExample() {
        doTest("mathExample");
    }
}
