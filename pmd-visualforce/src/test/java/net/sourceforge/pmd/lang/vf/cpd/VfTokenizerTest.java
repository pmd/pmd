/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.vf.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class VfTokenizerTest extends CpdTextComparisonTest {

    VfTokenizerTest() {
        super("vf", ".page");
    }

    @Test
    void testTokenize() {
        doTest("SampleUnescapeElWithTab");
    }
}
