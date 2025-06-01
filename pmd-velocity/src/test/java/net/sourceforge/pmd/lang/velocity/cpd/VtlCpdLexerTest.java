/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity.cpd;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.velocity.VtlLanguageModule;
import org.junit.jupiter.api.Test;

class VtlCpdLexerTest extends CpdTextComparisonTest {
    VtlCpdLexerTest() {
        super(VtlLanguageModule.getInstance(), ".vm");
    }

    @Test
    void sampleTest() {
        doTest("sample_vm");
    }
}
