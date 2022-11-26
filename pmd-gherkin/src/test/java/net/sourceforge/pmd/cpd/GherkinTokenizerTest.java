/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.gherkin.cpd.GherkinTokenizer;

class GherkinTokenizerTest extends CpdTextComparisonTest {
    GherkinTokenizerTest() {
        super(".feature");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/gherkin/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        GherkinTokenizer tok = new GherkinTokenizer();
        return tok;
    }

    @Test
    void testAnnotatedSource() {
        doTest("annotatedSource");
    }

    @Test
    void testDocstring() {
        doTest("docstring");
    }
}
