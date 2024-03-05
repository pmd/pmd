/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.tsql.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class TSqlCpdLexerTest extends CpdTextComparisonTest {

    TSqlCpdLexerTest() {
        super("tsql", ".sql");
    }

    @Test
    void simpleTest() {
        doTest("simple");
    }

    @Test
    void mailJobTimeLineTest() {
        doTest("MailJobTimeLine");
    }
}
