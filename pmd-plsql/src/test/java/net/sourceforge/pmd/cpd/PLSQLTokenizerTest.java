/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class PLSQLTokenizerTest extends CpdTextComparisonTest {

    PLSQLTokenizerTest() {
        super(".sql");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/plsql/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new PLSQLTokenizer();
    }

    
    @Test
    void testSimple() {
        doTest("sample-plsql");
    }

    @Test
    void testSpecialComments() {
        doTest("specialComments");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }
}
