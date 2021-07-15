/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class Java15TreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java15 =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("15")
                    .withResourceContext(Java15TreeDumpTest.class, "jdkversiontests/java15/");
    private final JavaParsingHelper java14 = java15.withDefaultVersion("14");

    public Java15TreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java15;
    }

    @Test
    public void textBlocks() {
        doTest("TextBlocks");
    }

    @Test(expected = net.sourceforge.pmd.lang.ast.ParseException.class)
    public void textBlocksBeforeJava15ShouldFail() {
        java14.parseResource("TextBlocks.java");
    }

    @Test(expected = ParseException.class)
    public void stringEscapeSequenceShouldFail() {
        java14.parse("class Foo { String s =\"a\\sb\"; }");
    }

    @Test
    public void sealedAndNonSealedIdentifiers() {
        doTest("NonSealedIdentifier");
    }
}
