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

class Java19PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java19p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("19-preview")
                    .withResourceContext(Java19PreviewTreeDumpTest.class, "jdkversiontests/java19p/");
    private final JavaParsingHelper java19 = java19p.withDefaultVersion("19");

    Java19PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java19p;
    }

    @Test
    void dealingWithNullBeforeJava19Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java19.parseResource("DealingWithNull.java"));
        assertTrue(thrown.getMessage().contains("Null case labels is a preview feature of JDK 19, you should select your language version accordingly"),
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
    void guardedAndParenthesizedPatternsBeforeJava19Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java19.parseResource("GuardedAndParenthesizedPatterns.java"));
        assertTrue(thrown.getMessage().contains("Pattern matching for switch is a preview feature of JDK 19, you should select your language version accordingly"),
                "Unexpected message: " + thrown.getMessage());
    }

    @Test
    void guardedAndParenthesizedPatterns() {
        doTest("GuardedAndParenthesizedPatterns");
    }

    @Test
    void patternsInSwitchLabelsBeforeJava19Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java19.parseResource("PatternsInSwitchLabels.java"));
        assertTrue(thrown.getMessage().contains("Pattern matching for switch is a preview feature of JDK 19, you should select your language version accordingly"),
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
    void recordPatternsBeforeJava19Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java19.parseResource("RecordPatterns.java"));
        assertTrue(thrown.getMessage().contains("Record patterns is a preview feature of JDK 19, you should select your language version accordingly"),
                "Unexpected message: " + thrown.getMessage());
    }
}
