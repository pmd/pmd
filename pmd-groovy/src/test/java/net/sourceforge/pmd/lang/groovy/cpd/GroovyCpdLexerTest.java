/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.groovy.cpd;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import org.junit.jupiter.api.Test;

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
