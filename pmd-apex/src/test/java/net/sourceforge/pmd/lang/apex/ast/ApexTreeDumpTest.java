/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.apex.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.test.ast.RelevantAttributePrinter;

class ApexTreeDumpTest extends BaseTreeDumpTest {

    ApexTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".cls");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return ApexParsingHelper.DEFAULT;
    }

    @Test
    void safeNavigationOperator() {
        doTest("SafeNavigationOperator");
    }

    @Test
    void userEnumType() {
        doTest("UserEnumType");
    }

    @Test
    void innerClassLocations() {
        doTest("InnerClassLocations");
    }

    @Test
    void nullCoalescingOperator() {
        doTest("NullCoalescingOperator");
    }
}
