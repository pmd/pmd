/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.matlab.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class MatlabCpdLexerTest extends CpdTextComparisonTest {

    MatlabCpdLexerTest() {
        super("matlab", ".m");
    }

    @Test
    void testLongSample() {
        doTest("sample-matlab");
    }

    @Test
    void testIgnoreBetweenSpecialComments() {
        doTest("specialComments");

    }

    @Test
    void testComments() {
        doTest("comments");
    }

    @Test
    void testBlockComments() {
        doTest("multilineComments");
    }

    @Test
    void testQuestionMark() {
        doTest("questionMark");
    }

    @Test
    void testDoubleQuotedStrings() {
        doTest("doubleQuotedStrings");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }
}
