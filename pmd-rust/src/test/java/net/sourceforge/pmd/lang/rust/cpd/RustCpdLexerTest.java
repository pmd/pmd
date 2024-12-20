/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rust.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

public class RustCpdLexerTest extends CpdTextComparisonTest {

    public RustCpdLexerTest() {
        super("rust", ".rs");
    }

    @Test
    public void testHelloWorld() {
        doTest("helloworld");
    }

}