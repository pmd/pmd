/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.jsp.JspLanguageModule;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.processor.PmdRunnable;
import net.sourceforge.pmd.testframework.RuleTst;
import net.sourceforge.pmd.util.datasource.DataSource;

public class XPathJspRuleTest extends RuleTst {

    /**
     * Test matching a XPath expression against a JSP source.
     * @throws PMDException
     */
    @Test
    public void testExpressionMatching() throws PMDException {
        Rule rule = new XPathRule(XPathVersion.XPATH_3_1, XPATH_EXPRESSION);
        rule.setMessage("Test");
        rule.setLanguage(LanguageRegistry.getLanguage(JspLanguageModule.NAME));

        RuleSet rules = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(rule);

        Report report = new PmdRunnable(
            DataSource.forString(MATCH, "test.jsp"),
            Collections.emptyList(),
            RuleContext.throwingExceptions(),
            listOf(rules),
            new PMDConfiguration()
        ).call();

        assertEquals("One violation expected!", 1, report.getViolations().size());

        RuleViolation rv = report.getViolations().get(0);
        assertEquals(1, rv.getBeginLine());
    }

    private static final String MATCH = "<html><hr/></html>";

    private static final String XPATH_EXPRESSION = "//Element [@Name='hr']";
}
