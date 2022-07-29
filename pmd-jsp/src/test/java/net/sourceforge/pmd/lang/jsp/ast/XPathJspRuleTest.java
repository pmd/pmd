/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.jsp.JspLanguageModule;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.testframework.RuleTst;

class XPathJspRuleTest extends RuleTst {

    /**
     * Test matching a XPath expression against a JSP source.
     */
    @Test
    void testExpressionMatching() {
        Rule rule = new XPathRule(XPathVersion.XPATH_3_1, XPATH_EXPRESSION);
        rule.setMessage("Test");
        rule.setLanguage(LanguageRegistry.getLanguage(JspLanguageModule.NAME));

        Report report = JspParsingHelper.DEFAULT.executeRule(rule, MATCH);

        assertEquals(1, report.getViolations().size(), "One violation expected!");

        RuleViolation rv = report.getViolations().get(0);
        assertEquals(1, rv.getBeginLine());
    }

    private static final String MATCH = "<html><hr/></html>";

    private static final String XPATH_EXPRESSION = "//Element [@Name='hr']";
}
