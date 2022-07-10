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

public class Java19PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java19p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("19-preview")
                    .withResourceContext(Java19PreviewTreeDumpTest.class, "jdkversiontests/java19p/");
    private final JavaParsingHelper java19 = java19p.withDefaultVersion("19");

    public Java19PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java19p;
    }

    @Test
    public void dealingWithNullBeforeJava19Preview() {
        ParseException thrown = assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java19.parseResource("DealingWithNull.java");
            }
        });
        assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Null case labels is a preview feature of JDK 19, you should select your language version accordingly"));
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
    public void guardedAndParenthesizedPatternsBeforeJava19Preview() {
        ParseException thrown = assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java19.parseResource("GuardedAndParenthesizedPatterns.java");
            }
        });
        assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Pattern matching for switch is a preview feature of JDK 19, you should select your language version accordingly"));
    }

    @Test
    public void guardedAndParenthesizedPatterns() {
        doTest("GuardedAndParenthesizedPatterns");
    }

    @Test
    public void patternsInSwitchLabelsBeforeJava19Preview() {
        ParseException thrown = assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java19.parseResource("PatternsInSwitchLabels.java");
            }
        });
        assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Pattern matching for switch is a preview feature of JDK 19, you should select your language version accordingly"));
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
    public void recordPatternsBeforeJava19Preview() {
        ParseException thrown = assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java19.parseResource("RecordPatterns.java");
            }
        });
        assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Record patterns is a preview feature of JDK 19, you should select your language version accordingly"));
    }
}
