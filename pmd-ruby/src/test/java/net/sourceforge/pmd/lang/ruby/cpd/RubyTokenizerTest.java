/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ruby.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class RubyTokenizerTest extends CpdTextComparisonTest {

    RubyTokenizerTest() {
        super("ruby", ".rb");
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
