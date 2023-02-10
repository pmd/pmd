/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;


class JSPTokenizerTest extends CpdTextComparisonTest {

    JSPTokenizerTest() {
        super(".jsp");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/jsp/cpd/testdata";
    }

    @Test
    void scriptletWithString() {
        doTest("scriptletWithString");
    }
}
