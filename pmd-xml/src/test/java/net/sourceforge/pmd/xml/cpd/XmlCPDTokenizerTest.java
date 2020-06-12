/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.xml.cpd;

import org.junit.Test;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class XmlCPDTokenizerTest extends CpdTextComparisonTest {

    public XmlCPDTokenizerTest() {
        super(".xml");
    }

    @Override
    public Tokenizer newTokenizer() {
        return new XmlTokenizer();
    }

    @Test
    public void tokenizeTest() {
        doTest("simple");
    }
}
