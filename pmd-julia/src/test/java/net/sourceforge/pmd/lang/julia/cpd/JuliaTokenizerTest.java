/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.julia.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.julia.JuliaLanguageModule;

class JuliaTokenizerTest extends CpdTextComparisonTest {
    JuliaTokenizerTest() {
        super(JuliaLanguageModule.getInstance(), ".jl");
    }

    @Test
    void testMathExample() {
        doTest("mathExample");
    }
}
