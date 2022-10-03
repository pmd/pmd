/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

class Java15TreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java15 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("15")
                                     .withResourceContext(Java15TreeDumpTest.class, "jdkversiontests/java15/");
    private final JavaParsingHelper java14 = java15.withDefaultVersion("14");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java15;
    }

    @Test
    void textBlocks() {
        doTest("TextBlocks");
    }

    @Test
    void textBlocksBeforeJava15ShouldFail() {
        assertThrows(ParseException.class, () -> java14.parseResource("TextBlocks.java"));
    }

    @Test
    void stringEscapeSequenceShouldFail() {
        assertThrows(ParseException.class, () -> java14.parse("class Foo { String s =\"a\\sb\"; }"));
    }

    @Test
    void sealedAndNonSealedIdentifiers() {
        doTest("NonSealedIdentifier");
    }
}
