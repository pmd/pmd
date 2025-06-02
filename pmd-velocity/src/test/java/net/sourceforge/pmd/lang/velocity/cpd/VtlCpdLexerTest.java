/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.velocity.VtlLanguageModule;

class VtlCpdLexerTest extends CpdTextComparisonTest {
    VtlCpdLexerTest() {
        super(VtlLanguageModule.getInstance(), ".vm");
    }

    @Test
    void sampleTest() {
        doTest("sample_vm");
    }
}
