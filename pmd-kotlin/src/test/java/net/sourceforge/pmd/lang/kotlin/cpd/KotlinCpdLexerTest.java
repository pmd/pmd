/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.cpd;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import org.junit.jupiter.api.Test;

class KotlinCpdLexerTest extends CpdTextComparisonTest {

    KotlinCpdLexerTest() {
        super("kotlin", ".kt");
    }

    @Test
    void testComments() {
        doTest("comment");
    }

    @Test
    void testIncrement() {
        doTest("increment");
    }

    @Test
    void testImportsIgnored() {
        doTest("imports");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }
}
