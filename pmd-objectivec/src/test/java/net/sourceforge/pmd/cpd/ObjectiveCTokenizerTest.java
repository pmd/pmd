/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class ObjectiveCTokenizerTest extends CpdTextComparisonTest {


    ObjectiveCTokenizerTest() {
        super(".m");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/objc/cpd/testdata";
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
