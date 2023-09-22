/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSize;
import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSuppressed;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ecmascript.ast.ASTFunctionNode;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParserTestBase;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;

class ReportTest extends EcmascriptParserTestBase {

    @Test
    void testExclusionsInReportWithNOPMDEcmascript() {
        Rule rule = new AbstractEcmascriptRule() {
            @Override
            public Object visit(ASTFunctionNode node, Object data) {
                addViolationWithMessage(data, node, "Test");
                return data;
            }
        };
        rule.setLanguage(js.getDefaultVersion().getLanguage());
        Report rpt = js.executeRule(rule, "function(x) // NOPMD test suppress\n{ x = 1; }");

        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }
}
