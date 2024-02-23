/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import static net.sourceforge.pmd.lang.test.ast.TestUtilsKt.assertSize;
import static net.sourceforge.pmd.lang.test.ast.TestUtilsKt.assertSuppressed;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ecmascript.ast.ASTFunctionNode;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParserTestBase;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.Report;

class ReportTest extends EcmascriptParserTestBase {

    @Test
    void testExclusionsInReportWithNOPMDEcmascript() {
        Rule rule = new AbstractEcmascriptRule() {
            @Override
            public Object visit(ASTFunctionNode node, Object data) {
                asCtx(data).addViolationWithMessage(node, "Test");
                return data;
            }
        };
        rule.setLanguage(js.getDefaultVersion().getLanguage());
        Report rpt = js.executeRule(rule, "function(x) // NOPMD test suppress\n{ x = 1; }");

        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }
}
