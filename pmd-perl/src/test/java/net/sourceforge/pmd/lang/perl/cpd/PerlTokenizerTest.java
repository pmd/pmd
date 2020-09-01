/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.perl.cpd;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.PerlLanguage;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

/**
 *
 */
public class PerlTokenizerTest extends CpdTextComparisonTest {

    public PerlTokenizerTest() {
        super(".pl");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new PerlLanguage().getTokenizer();
    }

    @Test
    public void testSample() {
        doTest("sample");
    }
}
