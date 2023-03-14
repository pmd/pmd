/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

class Java20PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java20p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("20-preview")
                    .withResourceContext(Java20PreviewTreeDumpTest.class, "jdkversiontests/java20p/");
    private final JavaParsingHelper java20 = java20p.withDefaultVersion("20");

    Java20PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java20p;
    }

    @Test
    void dealingWithNullBeforeJava20Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java20.parseResource("DealingWithNull.java"));
        assertTrue(thrown.getMessage().contains("Null in switch cases is a preview feature of JDK 20, you should select your language version accordingly"),
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
    void guardedAndParenthesizedPatternsBeforeJava20Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java20.parseResource("GuardedAndParenthesizedPatterns.java"));
        assertTrue(thrown.getMessage().contains("Patterns in switch statements is a preview feature of JDK 20, you should select your language version accordingly"),
                "Unexpected message: " + thrown.getMessage());
    }

    @Test
    void guardedAndParenthesizedPatterns() {
        doTest("GuardedAndParenthesizedPatterns");
    }

    @Test
    void patternsInSwitchLabelsBeforeJava20Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java20.parseResource("PatternsInSwitchLabels.java"));
        assertTrue(thrown.getMessage().contains("Patterns in switch statements is a preview feature of JDK 20, you should select your language version accordingly"),
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

    @Test
    void recordPatterns() {
        doTest("RecordPatterns");
    }

    @Test
    void recordPatternsBeforeJava20Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java20.parseResource("RecordPatterns.java"));
        assertTrue(thrown.getMessage().contains("Deconstruction patterns is a preview feature of JDK 20, you should select your language version accordingly"),
                "Unexpected message: " + thrown.getMessage());
    }

    @Test
    void recordPatternsInEnhancedFor() {
        doTest("RecordPatternsInEnhancedFor");
    }

    @Test
    void recordPatternsInEnhancedForBeforeJava20Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java20.parseResource("RecordPatternsInEnhancedFor.java"));
        assertTrue(thrown.getMessage().contains("Deconstruction patterns in enhanced for statement is a preview feature of JDK 20, you should select your language version accordingly"),
                "Unexpected message: " + thrown.getMessage());
    }

    @Test
    void recordPatternsExhaustiveSwitch() {
        doTest("RecordPatternsExhaustiveSwitch");
    }
}
