/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.perl.cpd;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import org.junit.jupiter.api.Test;

/**
 *
 */
class PerlCpdLexerTest extends CpdTextComparisonTest {

    PerlCpdLexerTest() {
        super("perl", ".pl");
    }

    @Test
    void testSample() {
        doTest("sample");
    }
}
