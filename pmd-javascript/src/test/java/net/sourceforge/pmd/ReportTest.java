/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSize;
import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSuppressed;

import org.junit.Test;

import net.sourceforge.pmd.lang.ecmascript.ast.ASTFunctionNode;
import net.sourceforge.pmd.lang.ecmascript.ast.JsParsingHelper;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;

public class ReportTest {

    @Test
    public void testExclusionsInReportWithNOPMDEcmascript() {
        Rule rule = new AbstractEcmascriptRule() {
            @Override
            public Object visit(ASTFunctionNode node, Object data) {
                addViolationWithMessage(data, node, "Test");
                return super.visit(node, data);
            }
        };
        Report rpt = JsParsingHelper.DEFAULT.executeRule(rule, "function(x) // NOPMD test suppress\n{ x = 1; }");

        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }
}
