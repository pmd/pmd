/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.testframework;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class RuleTst extends TestCase {

    private Map rulesets = new HashMap();
    private RuleSetFactory rsf = new RuleSetFactory();
    public void runTestFromString(String code, int expectedResults, Rule rule) throws Throwable {
        assertEquals(expectedResults, processUsingStringReader(code, rule).size());
    }

    public Rule findRule(String rs, String r) throws RuleSetNotFoundException {
        if (!rulesets.containsKey(rs)) {
            rulesets.put(rs, rsf.createRuleSet(rs));
        }
        return ((RuleSet)rulesets.get(rs)).getRuleByName(r);
    }

    public void runTestFromString(String code, Rule rule, Report report) throws Throwable {
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        ctx.setSourceCodeFilename("n/a");
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        p.processFile(new StringReader(code), rules, ctx);
    }

    private Report processUsingStringReader(String code, Rule rule) throws Throwable {
        Report report = new Report();
        runTestFromString(code, rule, report);
        return report;
    }
}
