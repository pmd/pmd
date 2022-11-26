/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class EcmascriptTokenizerTest extends CpdTextComparisonTest {

    EcmascriptTokenizerTest() {
        super(".js");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new EcmascriptTokenizer();
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/ecmascript/cpd/testdata";
    }

    @Test
    void testSimple() {
        doTest("simple");
    }

    @Test
    void testSimplewithSemis() {
        doTest("simpleWithSemis");
    }

    @Test
    void testIgnoreBetweenSpecialComments() {
        doTest("specialComments");
    }

    /**
     * See: https://sourceforge.net/p/pmd/bugs/1239/
     */
    @Test
    void parseStringNotAsMultiline() {
        doTest("lineContinuations");
    }

    @Test
    void testIgnoreSingleLineComments() {
        doTest("singleLineCommentIgnore");
    }

    @Test
    void testIgnoreMultiLineComments() {
        doTest("multilineCommentIgnore");
    }

    @Test
    void testTemplateStrings() {
        doTest("templateStrings");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }
}
