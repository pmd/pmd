/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.rule;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.lang.scala.ast.BaseScalaTest;

public class XPathRuleTest extends BaseScalaTest {

    private static final String SCALA_TEST = "/parserFiles/helloworld.scala";

    @Test
    public void testPrintHelloWorld() {
        Report report = evaluate(SCALA_TEST, "//TermApply/TermName[@Image=\"println\"]");
        RuleViolation rv = report.iterator().next();
        assertEquals(2, rv.getBeginLine());
    }

    private Report evaluate(String testSource, String xpath) {
        XPathRule rule = new XPathRule(XPathVersion.XPATH_2_0, xpath);
        rule.setLanguage(LanguageRegistry.getLanguage(ScalaLanguageModule.NAME));
        rule.setMessage("XPath Rule Failed");
        return scala.getReportForResource(rule, testSource);
    }
}
