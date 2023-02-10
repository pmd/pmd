/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class TSqlTokenizerTest extends CpdTextComparisonTest {

    public TSqlTokenizerTest() {
        super(".sql");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new TSqlTokenizer();
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/tsql/cpd/testdata";
    }

    @Test
    public void simpleTest() {
        doTest("simple");
    }

    @Test
    public void mailJobTimeLineTest() {
        doTest("MailJobTimeLine");
    }
}
