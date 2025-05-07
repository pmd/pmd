/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.css.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class CssCpdLexerTest extends CpdTextComparisonTest {

    public CssCpdLexerTest() {
        super("css", ".css");
    }

    @Override
    protected String getResourcePrefix() {
        return "testdata";
    }

    @Test
    public void testLiterals() {
        doTest("literals");
    }

}
