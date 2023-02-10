/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

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

    @Test
    void testSample() {
        doTest("sample");
    }
}
