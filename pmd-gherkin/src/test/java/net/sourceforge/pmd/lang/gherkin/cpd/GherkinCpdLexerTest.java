/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.gherkin.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class GherkinCpdLexerTest extends CpdTextComparisonTest {
    GherkinCpdLexerTest() {
        super("gherkin", ".feature");
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
