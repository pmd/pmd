/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.python.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class PythonCpdLexerTest extends CpdTextComparisonTest {

    PythonCpdLexerTest() {
        super("python", ".py");
    }

    @Test
    void sampleTest() {
        doTest("sample_python");
    }

    @Test
    void specialComments() {
        doTest("special_comments");
    }

    @Test
    void testBackticks() {
        doTest("backticks");
    }

    @Test
    void testUnicode() {
        doTest("sample_unicode");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }

    @Test
    void testVarWithDollar() {
        doTest("var_with_dollar");
    }

}
