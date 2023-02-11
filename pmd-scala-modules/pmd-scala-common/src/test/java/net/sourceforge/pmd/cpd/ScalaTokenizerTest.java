/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;

class ScalaTokenizerTest extends CpdTextComparisonTest {

    ScalaTokenizerTest() {
        super(ScalaLanguageModule.getInstance(), ".scala");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/scala/cpd/testdata";
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
        assertThrows(TokenMgrError.class, () -> doTest("unlexable_sample"));
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }
}
