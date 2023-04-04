/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class LuaTokenizerTest extends CpdTextComparisonTest {
    LuaTokenizerTest() {
        super(".lua");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/lua/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new LuaTokenizer();
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
