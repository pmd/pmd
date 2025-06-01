/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.lua.cpd;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import org.junit.jupiter.api.Test;

class LuaCpdLexerTest extends CpdTextComparisonTest {
    LuaCpdLexerTest() {
        super("lua", ".lua");
    }

    @Test
    void testSimple() {
        doTest("helloworld");
    }

    @Test
    void testFactorial() {
        doTest("factorial");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }

    @Test
    void testLuauTypes() {
        doTest("luauTypes");
    }

    @Test
    void testComment() {
        doTest("comment");
    }
}
