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
import net.sourceforge.pmd.TargetJDKVersion;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;

import java.io.StringReader;

public class RuleTst extends TestCase {

    public void runTestFromString(String code, int expectedResults, Rule rule) throws Throwable {
        assertEquals(expectedResults, processUsingStringReader(code, rule).size());
    }

    public Rule findRule(String rs, String r) {
        try {
			return new RuleSetFactory().createRuleSet(rs).getRuleByName(r);
		} catch (RuleSetNotFoundException e) {
			e.printStackTrace();
			fail("Rule "+r+" not found in ruleset "+rs);
			return null;
		}
    }

    public void runTestFromString(String code, Rule rule, Report report) throws Throwable {
        runTestFromString(code, rule, report, new TargetJDK1_4());
    }

    public void runTestFromString15(String code, Rule rule, Report report) throws Throwable {
        runTestFromString(code, rule, report, new TargetJDK1_5());
    }

    public void runTestFromString(String code, Rule rule, Report report, TargetJDKVersion jdk) throws Throwable {
        PMD p = new PMD(jdk);
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
