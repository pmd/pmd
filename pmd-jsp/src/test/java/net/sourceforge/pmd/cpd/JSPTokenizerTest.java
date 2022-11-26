/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;


class JSPTokenizerTest extends CpdTextComparisonTest {

    JSPTokenizerTest() {
        super(".jsp");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/jsp/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new JSPTokenizer();
    }

    @Test
    void scriptletWithString() {
        doTest("scriptletWithString");
    }
}
