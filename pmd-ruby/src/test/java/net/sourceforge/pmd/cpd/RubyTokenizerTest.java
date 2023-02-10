/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class RubyTokenizerTest extends CpdTextComparisonTest {

    RubyTokenizerTest() {
        super(".rb");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/ruby/cpd/testdata";
    }


    @Test
    void testSimple() {
        doTest("server");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }
}
