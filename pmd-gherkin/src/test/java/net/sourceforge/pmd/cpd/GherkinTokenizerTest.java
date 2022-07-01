/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.gherkin.cpd.GherkinTokenizer;

public class GherkinTokenizerTest extends CpdTextComparisonTest {
    public GherkinTokenizerTest() {
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
    public void testAnnotatedSource() {
        doTest("annotatedSource");
    }

    @Test
    public void testDocstring() {
        doTest("docstring");
    }
}
