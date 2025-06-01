/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.objectivec.cpd;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import org.junit.jupiter.api.Test;

class ObjectiveCCpdLexerTest extends CpdTextComparisonTest {

    ObjectiveCCpdLexerTest() {
        super("objectivec", ".m");
    }

    @Test
    void testLongSample() {
        doTest("big_sample");
    }

    @Test
    void testUnicodeEscape() {
        doTest("unicodeEscapeInString");
    }

    @Test
    void testUnicodeCharInIdent() {
        doTest("unicodeCharInIdent");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }
}
