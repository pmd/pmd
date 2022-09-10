/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

public class XPathJspRuleTest extends AbstractJspNodesTst {

    /**
     * Test matching a XPath expression against a JSP source.
     */
    @Test
    public void testExpressionMatching() {
        Rule rule = jsp.newXpathRule("//Element [@Name='hr']");
        Report report = jsp.executeRule(rule, "<html><hr/></html>");

        assertEquals("One violation expected!", 1, report.getViolations().size());

        RuleViolation rv = report.getViolations().get(0);
        assertEquals(1, rv.getBeginLine());
    }

}
