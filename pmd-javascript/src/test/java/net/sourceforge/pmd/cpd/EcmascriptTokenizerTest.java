/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class EcmascriptTokenizerTest extends CpdTextComparisonTest {

    public EcmascriptTokenizerTest() {
        super(".js");
    }

    @NotNull
    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new EcmascriptTokenizer();
    }

    @NotNull
    @Override
    protected String getResourcePrefix() {
        return "../lang/ecmascript/cpd/testdata";
    }

    @Test
    public void testSimple() {
        doTest("simple");
    }

    @Test
    public void testSimplewithSemis() {
        doTest("simpleWithSemis");
    }

    @Test
    public void testIgnoreBetweenSpecialComments() {
        doTest("specialComments");
    }

    /**
     * See: https://sourceforge.net/p/pmd/bugs/1239/
     */
    @Test
    public void parseStringNotAsMultiline() {
        doTest("lineContinuations");
    }

    @Test
    public void testIgnoreSingleLineComments() {
        doTest("singleLineCommentIgnore");
    }

    @Test
    public void testIgnoreMultiLineComments() {
        doTest("multilineCommentIgnore");
    }

    @Test
    public void testTemplateStrings() {
        doTest("templateStrings");
    }
}
