/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class Java20PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java20p =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("20-preview")
                    .withResourceContext(Java20PreviewTreeDumpTest.class, "jdkversiontests/java20p/");
    private final JavaParsingHelper java20 = java20p.withDefaultVersion("20");

    public Java20PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java20p;
    }

    @Test
    public void dealingWithNullBeforeJava20Preview() {
        ParseException thrown = assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java20.parseResource("DealingWithNull.java");
            }
        });
        assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Null case labels in switch are only supported with JDK 19 Preview or JDK 20 Preview."));
    }

    @Test
    public void dealingWithNull() {
        doTest("DealingWithNull");
    }

    @Test
    public void enhancedTypeCheckingSwitch() {
        doTest("EnhancedTypeCheckingSwitch");
    }

    @Test
    public void exhaustiveSwitch() {
        doTest("ExhaustiveSwitch");
    }

    @Test
    public void guardedAndParenthesizedPatternsBeforeJava20Preview() {
        ParseException thrown = assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java20.parseResource("GuardedAndParenthesizedPatterns.java");
            }
        });
        assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Pattern Matching in Switch is only supported with JDK 19 Preview or JDK 20 Preview."));
    }

    @Test
    public void guardedAndParenthesizedPatterns() {
        doTest("GuardedAndParenthesizedPatterns");
    }

    @Test
    public void patternsInSwitchLabelsBeforeJava20Preview() {
        ParseException thrown = assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java20.parseResource("PatternsInSwitchLabels.java");
            }
        });
        assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Pattern Matching in Switch is only supported with JDK 19 Preview or JDK 20 Preview."));
    }

    @Test
    public void patternsInSwitchLabels() {
        doTest("PatternsInSwitchLabels");
    }

    @Test
    public void refiningPatternsInSwitch() {
        doTest("RefiningPatternsInSwitch");
    }

    @Test
    public void scopeOfPatternVariableDeclarations() {
        doTest("ScopeOfPatternVariableDeclarations");
    }

    @Test
    public void recordPatterns() {
        doTest("RecordPatterns");
    }

    @Test
    public void recordPatternsBeforeJava20Preview() {
        ParseException thrown = assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java20.parseResource("RecordPatterns.java");
            }
        });
        assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Record Patterns are only supported with JDK 19 Preview or JDK 20 Preview."));
    }

    @Test
    public void recordPatternsInEnhancedFor() {
        doTest("RecordPatternsInEnhancedFor");
    }

    @Test
    public void recordPatternsInEnhancedForBeforeJava20Preview() {
        ParseException thrown = assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java20.parseResource("RecordPatternsInEnhancedFor.java");
            }
        });
        assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Record Patterns in enhanced for statements are only supported with JDK 20 Preview."));
    }

    @Test
    public void recordPatternsExhaustiveSwitch() {
        doTest("RecordPatternsExhaustiveSwitch");
    }
}
