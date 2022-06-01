/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class GherkinTokenizerTest extends CpdTextComparisonTest {

    @org.junit.Rule
    public ExpectedException ex = ExpectedException.none();

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
