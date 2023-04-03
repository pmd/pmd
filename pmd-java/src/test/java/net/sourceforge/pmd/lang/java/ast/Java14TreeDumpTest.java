/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

/**
 * Tests new java14 standard features.
 */
class Java14TreeDumpTest extends BaseJavaTreeDumpTest {

    private final JavaParsingHelper java14 =
        JavaParsingHelper.DEFAULT.withDefaultVersion("14")
                                 .withResourceContext(Java14TreeDumpTest.class, "jdkversiontests/java14/");

    private final JavaParsingHelper java13 = java14.withDefaultVersion("13");

    @Override
    public @NonNull BaseParsingHelper<?, ?> getParser() {
        return java14;
    }

    /**
     * Tests switch expressions with yield.
     */
    @Test
    void switchExpressions() {
        doTest("SwitchExpressions");
    }

    /**
     * In java13, switch expressions are only available with preview.
     */
    @Test
    void switchExpressions13ShouldFail() {
        assertThrows(ParseException.class, () -> java13.parseResource("SwitchExpressions.java"));
    }

    @Test
    void checkYieldConditionalBehaviour() {
        doTest("YieldStatements");
    }

    @Test
    void multipleCaseLabels() {
        doTest("MultipleCaseLabels");
    }

    @Test
    void switchRules() {
        doTest("SwitchRules");
    }

    @Test
    void simpleSwitchExpressions() {
        doTest("SimpleSwitchExpressions");
    }

}
