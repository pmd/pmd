/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.rule;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.lang.scala.ast.BaseScalaTest;

public class XPathRuleTest extends BaseScalaTest {

    private static final String SCALA_TEST = "/parserFiles/helloworld.scala";

    XPathRule rule;

    @Before
    public void setUp() {
        rule = new XPathRule();
        rule.setLanguage(LanguageRegistry.getLanguage(ScalaLanguageModule.NAME));
        rule.setMessage("XPath Rule Failed");
    }

    @Test
    public void testPrintHelloWorld() {
        String xpath = "//TermApply/TermName[@Image=\"println\"]";
        rule.setXPath(xpath);
        rule.setVersion(XPathRuleQuery.XPATH_2_0);
        Report report = scala.getReportForResource(rule, SCALA_TEST);
        RuleViolation rv = report.iterator().next();
        assertEquals(2, rv.getBeginLine());
    }
}
