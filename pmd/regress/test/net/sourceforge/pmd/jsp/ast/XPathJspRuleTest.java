package test.net.sourceforge.pmd.jsp.ast;

import net.sourceforge.pmd.Language;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.rules.XPathRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

import java.io.StringReader;

public class XPathJspRuleTest extends RuleTst {

    /**
     * Test matching a XPath expression against a JSP source.
     *
     * @throws Throwable
     */
    public void testExpressionMatching() throws Throwable {
        Rule rule = new XPathRule();
        rule.addProperty("xpath", XPATH_EXPRESSION);
        rule.setMessage("Test");
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        rules.setLanguage(Language.JSP);

        RuleContext ctx = new RuleContext();
        Report report = new Report();
        ctx.setReport(report);
        ctx.setSourceCodeFilename("n/a");

        PMD p = new PMD();

        p.processFile(new StringReader(MATCH), new RuleSets(rules), ctx, SourceType.JSP);

        assertEquals("One violation expected!", 1, report.size());

        RuleViolation rv = (RuleViolation) report.iterator().next();
        assertEquals(1, rv.getBeginLine());
    }

    private static final String MATCH
            = "<html><hr/></html>";

    private static final String XPATH_EXPRESSION
            = "//Element [@Name='hr']";

}
