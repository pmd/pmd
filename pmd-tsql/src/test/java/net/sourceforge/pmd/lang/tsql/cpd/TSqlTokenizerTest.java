/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.tsql.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class TSqlTokenizerTest extends CpdTextComparisonTest {

    TSqlTokenizerTest() {
        super(".sql");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new TSqlTokenizer();
    }

    @Override
    protected String getResourcePrefix() {
        return "../cpd/testdata";
    }

    @Test
    void simpleTest() {
        doTest("simple");
    }

    @Test
    void mailJobTimeLineTest() {
        doTest("MailJobTimeLine");
    }
}
