/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

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

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new MatlabTokenizer();
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
