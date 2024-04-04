/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ecmascript.EcmascriptLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;



class EcmascriptCpdLexerTest extends CpdTextComparisonTest {

    EcmascriptCpdLexerTest() {
        super(EcmascriptLanguageModule.getInstance(), ".js");
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

    @Test
    void testDecorators() {
        doTest("decorator");
    }
}
