/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.swift.SwiftLanguageModule;

class SwiftTokenizerTest extends CpdTextComparisonTest {

    SwiftTokenizerTest() {
        super(SwiftLanguageModule.getInstance(), ".swift");
    }

    @Test
    void testSwift42() {
        doTest("Swift4.2");
    }

    @Test
    void testSwift50() {
        doTest("Swift5.0");
    }

    @Test
    void testSwift51() {
        doTest("Swift5.1");
    }

    @Test
    void testSwift52() {
        doTest("Swift5.2");
    }

    @Test
    void testSwift53() {
        doTest("Swift5.3");
    }

    @Test
    void testSwift55() {
        doTest("Swift5.5");
    }

    @Test
    void testSwift56() {
        doTest("Swift5.6");
    }

    @Test
    void testStackoverflowOnLongLiteral() {
        doTest("Issue628");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }
}
