/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class GherkinTokenizerTest extends CpdTextComparisonTest {
    GherkinTokenizerTest() {
        super(".feature");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/gherkin/cpd/testdata";
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
