/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class GoTokenizerTest extends CpdTextComparisonTest {

    public GoTokenizerTest() {
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
    public void simpleTest() {
        doTest("hello");
    }

    @Test
    public void bigFileTest() {
        doTest("btrfs");
    }

    @Test
    public void testIssue1751() {
        doTest("issue-1751");
    }
}
