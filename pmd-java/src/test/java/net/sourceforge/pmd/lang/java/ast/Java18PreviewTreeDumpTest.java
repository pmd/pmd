/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

class Java18PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java18p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("18-preview")
                                     .withResourceContext(Java18PreviewTreeDumpTest.class, "jdkversiontests/java18p/");
    private final JavaParsingHelper java18 = java18p.withDefaultVersion("18");

    Java18PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java18p;
    }

    @Test
    void dealingWithNullBeforeJava18Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java18.parseResource("DealingWithNull.java"));
        assertTrue(thrown.getMessage().contains("Null case labels is a preview feature of JDK 18, you should select your language version accordingly"),
                "Unexpected message: " + thrown.getMessage());
    }

    @Test
    void dealingWithNull() {
        doTest("DealingWithNull");
    }

    @Test
    void enhancedTypeCheckingSwitch() {
        doTest("EnhancedTypeCheckingSwitch");
    }

    @Test
    void exhaustiveSwitch() {
        doTest("ExhaustiveSwitch");
    }

    @Test
    void guardedAndParenthesizedPatternsBeforeJava18Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java18.parseResource("GuardedAndParenthesizedPatterns.java"));
        assertThat(thrown.getMessage(), containsString("Pattern matching for switch is a preview feature of JDK 18, you should select your language version accordingly"));
    }

    @Test
    void guardedAndParenthesizedPatterns() {
        doTest("GuardedAndParenthesizedPatterns");
    }

    @Test
    void patternsInSwitchLabelsBeforeJava18Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java18.parseResource("PatternsInSwitchLabels.java"));
        assertTrue(thrown.getMessage().contains("Pattern matching for switch is a preview feature of JDK 18, you should select your language version accordingly"),
                "Unexpected message: " + thrown.getMessage());
    }

    @Test
    void patternsInSwitchLabels() {
        doTest("PatternsInSwitchLabels");
    }

    @Test
    void refiningPatternsInSwitch() {
        doTest("RefiningPatternsInSwitch");
    }

    @Test
    void scopeOfPatternVariableDeclarations() {
        doTest("ScopeOfPatternVariableDeclarations");
    }
}
