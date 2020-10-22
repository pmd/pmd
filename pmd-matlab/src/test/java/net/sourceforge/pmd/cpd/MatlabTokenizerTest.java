/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class MatlabTokenizerTest extends CpdTextComparisonTest {

    public MatlabTokenizerTest() {
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
    public void testLongSample() {
        doTest("sample-matlab");
    }

    @Test
    public void testIgnoreBetweenSpecialComments() {
        doTest("specialComments");

    }

    @Test
    public void testComments() {
        doTest("comments");
    }

    @Test
    public void testBlockComments() {
        doTest("multilineComments");
    }

    @Test
    public void testQuestionMark() {
        doTest("questionMark");
    }

    @Test
    public void testDoubleQuotedStrings() {
        doTest("doubleQuotedStrings");
    }

    @Test
    public void testTabWidth() {
        doTest("tabWidth");
    }
}
