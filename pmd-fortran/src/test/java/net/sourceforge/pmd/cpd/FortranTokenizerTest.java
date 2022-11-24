/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

/**
 * @author rpelisse
 *
 */
class FortranTokenizerTest extends CpdTextComparisonTest {

    FortranTokenizerTest() {
        super(".for");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/fortran/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new FortranLanguage().getTokenizer();
    }

    @Test
    void testSample() {
        doTest("sample");
    }
}
