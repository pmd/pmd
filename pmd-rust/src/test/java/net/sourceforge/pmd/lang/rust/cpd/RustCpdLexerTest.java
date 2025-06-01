/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rust.cpd;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import org.junit.jupiter.api.Test;

class RustCpdLexerTest extends CpdTextComparisonTest {

    RustCpdLexerTest() {
        super("rust", ".rs");
    }

    @Test
    void testHelloWorld() {
        doTest("helloworld");
    }
}
