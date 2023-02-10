/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class GroovyTokenizerTest extends CpdTextComparisonTest {

    GroovyTokenizerTest() {
        super(".groovy");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/groovy/cpd/testdata";
    }

    @Test
    void testSample() {
        doTest("sample");
    }
}
