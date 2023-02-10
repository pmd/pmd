/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class MatlabTokenizerTest extends CpdTextComparisonTest {

    MatlabTokenizerTest() {
        super(".m");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/matlab/cpd/testdata";
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
