/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.scala.cpd;

import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import org.junit.jupiter.api.Test;

class ScalaCpdLexerTest extends CpdTextComparisonTest {

    ScalaCpdLexerTest() {
        super(ScalaLanguageModule.getInstance(), ".scala");
    }

    @Test
    void testSample() {
        doTest("sample-LiftActor");
    }

    @Test
    void testSuppressionComments() {
        doTest("special_comments");
    }

    @Test
    void unclosedLiteral() {
        // note: this failed before PMD 7.10.0 with a LexException, but now the string literal is just
        // expanded to the end of the line
        doTest("unclosed_literal");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }
}
