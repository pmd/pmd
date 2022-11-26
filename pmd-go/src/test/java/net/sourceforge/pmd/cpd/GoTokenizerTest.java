/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class GoTokenizerTest extends CpdTextComparisonTest {

    GoTokenizerTest() {
        super(".go");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new GoTokenizer();
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/go/cpd/testdata";
    }

    @Test
    void simpleTest() {
        doTest("hello");
    }

    @Test
    void bigFileTest() {
        doTest("btrfs");
    }

    @Test
    void testIssue1751() {
        doTest("issue-1751");
    }

    @Test
    void testUnicode() {
        // https://github.com/pmd/pmd/issues/2752
        doTest("sample_unicode");
    }

}
