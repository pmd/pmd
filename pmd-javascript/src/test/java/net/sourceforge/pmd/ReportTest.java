/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptLanguageModule;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTFunctionNode;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;
import net.sourceforge.pmd.lang.ecmascript.rule.EcmascriptRuleViolationFactory;
import net.sourceforge.pmd.testframework.RuleTst;

public class ReportTest extends RuleTst {

    @Test
    public void testExclusionsInReportWithNOPMDEcmascript() throws Exception {
        Report rpt = new Report();
        Rule rule = new AbstractEcmascriptRule() {
            @Override
            public Object visit(ASTFunctionNode node, Object data) {
                EcmascriptRuleViolationFactory.INSTANCE.addViolation((RuleContext) data, this, node, "Test", null);
                return super.visit(node, data);
            }
        };
        String code = "function(x) // NOPMD test suppress\n" + "{ x = 1; }";
        runTestFromString(code, rule, rpt,
                LanguageRegistry.getLanguage(EcmascriptLanguageModule.NAME).getDefaultVersion());
        assertTrue(rpt.isEmpty());
        assertEquals(1, rpt.getSuppressedRuleViolations().size());
    }
}
