/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.rule;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.testframework.RuleTst;

public class XPathRuleTest extends RuleTst {
    private static final String SCALA_TEST = "/parserFiles/helloworld.scala";

    XPathRule rule;

    @Before
    public void setUp() {
        rule = new XPathRule();
        rule.setLanguage(LanguageRegistry.getLanguage(ScalaLanguageModule.NAME));
        rule.setMessage("XPath Rule Failed");
    }

    @Test
    public void testPrintHelloWorld() throws Exception {
        String xpath = "//TermApply/TermName[@Image=\"println\"]";
        rule.setXPath(xpath);
        rule.setVersion(XPathRuleQuery.XPATH_2_0);
        Report report = getReportForTestString(rule,
                IOUtils.toString(getClass().getResourceAsStream(SCALA_TEST), "UTF-8"));
        RuleViolation rv = report.iterator().next();
        assertEquals(2, rv.getBeginLine());
    }

    private static Report getReportForTestString(Rule r, String test) throws PMDException {
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        Report report = new Report();
        ctx.setReport(report);
        ctx.setSourceCodeFile(new File("test.scala"));
        RuleSet rules = new RuleSetFactory().createSingleRuleRuleSet(r);
        p.getSourceCodeProcessor().processSourceCode(new StringReader(test), new RuleSets(rules), ctx);
        return report;
    }
}
