/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.groovy.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class GroovyTokenizerTest extends CpdTextComparisonTest {

    GroovyTokenizerTest() {
        super("groovy", ".groovy");
    }


    @Test
    void testSample() {
        doTest("sample");
    }
}
