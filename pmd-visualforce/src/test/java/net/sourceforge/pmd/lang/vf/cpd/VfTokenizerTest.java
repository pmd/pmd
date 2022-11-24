/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.vf.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.VfTokenizer;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class VfTokenizerTest extends CpdTextComparisonTest {

    VfTokenizerTest() {
        super(".page");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        VfTokenizer tokenizer = new VfTokenizer();
        return tokenizer;
    }

    @Test
    void testTokenize() {
        doTest("SampleUnescapeElWithTab");
    }
}
