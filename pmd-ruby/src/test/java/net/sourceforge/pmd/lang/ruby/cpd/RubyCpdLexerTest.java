/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ruby.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class RubyCpdLexerTest extends CpdTextComparisonTest {

    RubyCpdLexerTest() {
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
