/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.gherkin.cpd;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import org.junit.jupiter.api.Test;

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
