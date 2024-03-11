/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.go.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class GoCpdLexerTest extends CpdTextComparisonTest {

    GoCpdLexerTest() {
        super("go", ".go");
    }

    @Test
    void simpleTest() {
        doTest("hello");
    }

    @Test
    void bigFileTest() {
        doTest("btrfs");
    }

    @Test
    void testIssue1751() {
        doTest("issue-1751");
    }

    @Test
    void testUnicode() {
        // https://github.com/pmd/pmd/issues/2752
        doTest("sample_unicode");
    }

}
