package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.XPathRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

import java.io.StringReader;

/**
 * @author daniels
 */
public class XPathRuleTest extends RuleTst {

    XPathRule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.setMessage("XPath Rule Failed");
    }

    public void testPluginname() throws Throwable {
        Rule rule = new XPathRule();
        rule.addProperty("xpath", "//VariableDeclaratorId[string-length(@Image) < 3]");
        rule.setMessage("{0}");
        rule.addProperty("pluginname", "true");
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        Report report = new Report();
        ctx.setReport(report);
        ctx.setSourceCodeFilename("n/a");
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        p.processFile(new StringReader(TEST1), rules, ctx);
        RuleViolation rv = (RuleViolation)report.iterator().next();
        assertEquals("a", rv.getDescription());
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    "    int a;" + PMD.EOL +
    "}";
}
