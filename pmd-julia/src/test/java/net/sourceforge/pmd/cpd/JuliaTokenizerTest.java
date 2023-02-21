/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.julia.cpd.JuliaTokenizer;

public class JuliaTokenizerTest extends CpdTextComparisonTest {
    public JuliaTokenizerTest() {
        super(".jl");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/julia/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        JuliaTokenizer tok = new JuliaTokenizer();
        return tok;
    }

    @Test
    public void testMathExample() {
        doTest("mathExample");
    }
}
