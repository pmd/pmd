/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.perl.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.PerlLanguage;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

/**
 *
 */
class PerlTokenizerTest extends CpdTextComparisonTest {

    PerlTokenizerTest() {
        super(".pl");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new PerlLanguage().getTokenizer();
    }

    @Test
    void testSample() {
        doTest("sample");
    }
}
