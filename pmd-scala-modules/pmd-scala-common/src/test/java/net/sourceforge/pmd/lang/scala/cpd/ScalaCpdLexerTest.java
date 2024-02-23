/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.cpd;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

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
    void tokenizeFailTest() {
        assertThrows(LexException.class, () -> doTest("unlexable_sample"));
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }
}
