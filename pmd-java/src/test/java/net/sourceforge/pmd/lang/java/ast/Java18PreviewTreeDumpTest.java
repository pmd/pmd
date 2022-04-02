/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class Java18PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java18p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("18-preview")
                                     .withResourceContext(Java18PreviewTreeDumpTest.class, "jdkversiontests/java18p/");
    private final JavaParsingHelper java18 = java18p.withDefaultVersion("18");

    public Java18PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java18p;
    }

    @Test
    public void dealingWithNullBeforeJava18Preview() {
        ParseException thrown = Assert.assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java18.parseResource("DealingWithNull.java");
            }
        });
        Assert.assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Null case labels is a preview feature of JDK 18, you should select your language version accordingly"));
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
    public void guardedAndParenthesizedPatternsBeforeJava18Preview() {
        ParseException thrown = Assert.assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java18.parseResource("GuardedAndParenthesizedPatterns.java");
            }
        });
        assertThat(thrown.getMessage(), containsString("Pattern matching for switch is a preview feature of JDK 18, you should select your language version accordingly"));
    }

    @Test
    public void guardedAndParenthesizedPatterns() {
        doTest("GuardedAndParenthesizedPatterns");
    }

    @Test
    public void patternsInSwitchLabelsBeforeJava18Preview() {
        ParseException thrown = Assert.assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java18.parseResource("PatternsInSwitchLabels.java");
            }
        });
        Assert.assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Pattern matching for switch is a preview feature of JDK 18, you should select your language version accordingly"));
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
}
