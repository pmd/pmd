/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.xml.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class XmlCPDTokenizerTest extends CpdTextComparisonTest {

    XmlCPDTokenizerTest() {
        super(".xml");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new XmlTokenizer();
    }

    @Test
    void tokenizeTest() {
        doTest("simple");
    }
}
