/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

class XPathJspRuleTest extends AbstractJspNodesTst {

    /**
     * Test matching a XPath expression against a JSP source.
     */
    @Test
    void testExpressionMatching() {
        Rule rule = jsp.newXpathRule("//Element [@Name='hr']");
        Report report = jsp.executeRule(rule, "<html><hr/></html>");

        assertEquals(1, report.getViolations().size(), "One violation expected!");

        RuleViolation rv = report.getViolations().get(0);
        assertEquals(1, rv.getBeginLine());
    }

}
