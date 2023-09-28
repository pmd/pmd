/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.vm.VmLanguageModule;

class VmTokenizerTest extends CpdTextComparisonTest {
    VmTokenizerTest() {
        super(VmLanguageModule.getInstance(), ".vm");
    }

    @Test
    void sampleTest() {
        doTest("sample_vm");
    }
}
