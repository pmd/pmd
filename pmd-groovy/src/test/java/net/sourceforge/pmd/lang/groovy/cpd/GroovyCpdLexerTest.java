/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.groovy.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class GroovyCpdLexerTest extends CpdTextComparisonTest {

    GroovyCpdLexerTest() {
        super("groovy", ".groovy");
    }

    @Test
    void testSample() {
        doTest("sample");
    }
    
    @Test
    void testCpdOffAndOn() {
        doTest("cpdoff");
    }
}
