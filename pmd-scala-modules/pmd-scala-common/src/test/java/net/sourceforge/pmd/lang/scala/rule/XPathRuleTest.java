/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.rule.xpath.XPathRule;
import net.sourceforge.pmd.lang.scala.ast.BaseScalaTest;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

class XPathRuleTest extends BaseScalaTest {

    private static final String SCALA_TEST = "/parserFiles/helloworld.scala";

    @Test
    void testPrintHelloWorld() {
        Report report = evaluate(SCALA_TEST, "//TermApply/TermName[@Value=\"println\"]");
        RuleViolation rv = report.getViolations().get(0);
        assertEquals(2, rv.getBeginLine());
    }

    private Report evaluate(String testSource, String xpath) {
        XPathRule rule = scala.newXpathRule(xpath);
        return scala.executeRuleOnResource(rule, testSource);
    }
}
