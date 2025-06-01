/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce.cpd;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import org.junit.jupiter.api.Test;

class VfCpdLexerTest extends CpdTextComparisonTest {

    VfCpdLexerTest() {
        super("visualforce", ".page");
    }

    @Test
    void testTokenize() {
        doTest("SampleUnescapeElWithTab");
    }
}
