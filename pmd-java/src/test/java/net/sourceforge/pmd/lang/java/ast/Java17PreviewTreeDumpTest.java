/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Ignore;
import org.junit.Test;

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

    @Test(expected = ParseException.class)
    public void patternMatchingForSwitchBeforeJava17Preview() {
        java17.parseResource("PatternsInSwitchLabels.java");
    }

    @Test(expected = ParseException.class)
    public void dealingWithNullBeforeJava17Preview() {
        java17.parseResource("DealingWithNull.java");
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
    public void dealingWithNull() {
        doTest("DealingWithNull");
    }

    @Test
    @Ignore("not finished yet")
    public void guardedAndParenthesizedPatterns() {
        doTest("GuardedAndParenthesizedPatterns");
    }
}
