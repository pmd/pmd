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

    /**
     * @see <a href="https://github.com/pmd/pmd/issues/4922">[apex] TYPEOF in sub-query throws error #4922</a>
     */
    @Test
    void typeOfSubQuery() {
        doTest("TypeofTest");
    }

    /**
     * @see <a href="https://github.com/google/summit-ast/issues/53">Fail to parses SOSL with WITH USER_MODE or WITH SYSTEM_MODE #53</a>
     */
    @Test
    void soslWithUsermode() {
        doTest("SoslWithUsermode");
    }

    /**
     * @see <a href="https://github.com/pmd/pmd/issues/5094">[apex] "No adapter exists for type" error message printed to stdout instead of stderr</a>
     */
    @Test
    void switchStatements() {
        doTest("SwitchStatements");
    }

    @Test
    void trigger() {
        doTest("AccountTrigger");
    }
}
