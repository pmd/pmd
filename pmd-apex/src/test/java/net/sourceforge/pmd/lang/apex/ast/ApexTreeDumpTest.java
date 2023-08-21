/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.apex.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;

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
}
