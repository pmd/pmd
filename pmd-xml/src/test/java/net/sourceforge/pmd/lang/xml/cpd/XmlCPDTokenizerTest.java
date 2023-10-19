/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.xml.XmlLanguageModule;

class XmlCPDTokenizerTest extends CpdTextComparisonTest {

    XmlCPDTokenizerTest() {
        super(XmlLanguageModule.getInstance(), ".xml");
    }

    @Test
    void tokenizeTest() {
        doTest("simple");
    }
}
