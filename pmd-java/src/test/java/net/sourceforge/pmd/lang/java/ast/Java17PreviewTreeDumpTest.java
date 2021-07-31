/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class Java17PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java17p =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("17-preview")
                    .withResourceContext(Java17PreviewTreeDumpTest.class, "jdkversiontests/java17p/");
    private final JavaParsingHelper java17 = java17p.withDefaultVersion("17");

    public Java17PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java17p;
    }

    @Test
    public void patternMatchingForSwitchBeforeJava17Preview() {
        ParseException thrown = Assert.assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java17.parseResource("PatternsInSwitchLabels.java");
            }
        });
        Assert.assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Pattern Matching in Switch is only supported with JDK 17 Preview."));
    }

    @Test
    public void patternMatchingForSwitch() {
        doTest("PatternsInSwitchLabels");
    }

    @Test
    public void enhancedTypeCheckingSwitch() {
        doTest("EnhancedTypeCheckingSwitch");
    }

    @Test
    public void scopeOfPatternVariableDeclarations() {
        doTest("ScopeOfPatternVariableDeclarations");
    }

    @Test
    public void dealingWithNullBeforeJava17Preview() {
        ParseException thrown = Assert.assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java17.parseResource("DealingWithNull.java");
            }
        });
        Assert.assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Null case labels in switch are only supported with JDK 17 Preview."));
    }

    @Test
    public void dealingWithNull() {
        doTest("DealingWithNull");
    }

    @Test
    public void guardedAndParenthesizedPatternsBeforeJava17Preview() {
        ParseException thrown = Assert.assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java17.parseResource("GuardedAndParenthesizedPatterns.java");
            }
        });
        Assert.assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Guarded patterns are only supported with JDK 17 Preview."));
    }

    @Test
    public void guardedAndParenthesizedPatterns() {
        doTest("GuardedAndParenthesizedPatterns");
    }
}
