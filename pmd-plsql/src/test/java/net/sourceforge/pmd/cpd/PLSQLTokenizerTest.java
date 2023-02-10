/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.plsql.PLSQLLanguageModule;

class PLSQLTokenizerTest extends CpdTextComparisonTest {

    PLSQLTokenizerTest() {
        super(PLSQLLanguageModule.getInstance(), ".sql");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/plsql/cpd/testdata";
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
