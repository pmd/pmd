/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.cpd;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.xml.XmlLanguageModule;
import org.junit.jupiter.api.Test;

class XmlCPDCpdLexerTest extends CpdTextComparisonTest {

    XmlCPDCpdLexerTest() {
        super(XmlLanguageModule.getInstance(), ".xml");
    }

    @Test
    void tokenizeTest() {
        doTest("simple");
    }
}
