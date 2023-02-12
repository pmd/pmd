/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class KotlinTokenizerTest extends CpdTextComparisonTest {

    KotlinTokenizerTest() {
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
