package test.net.sourceforge.pmd.rules;

/**
 * RuleTst
 *
 * Extend your Rule TestCases from here to get some
 * juicy code sharing.
 */

import junit.framework.TestCase;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.ast.*;

public class RuleTst
    extends TestCase
{
    public Report process( String fileName,
			   Rule rule )
	throws Throwable
    {
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        Report report = new Report();
        ctx.setReport(report);
        ctx.setSourceCodeFilename(fileName);
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        p.processFile(getClass().getClassLoader().getResourceAsStream(fileName), rules, ctx);
        return ctx.getReport();
    }

    public void process(String fileName, Rule rule, Report report) {
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        ctx.setSourceCodeFilename(fileName);
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        p.processFile(getClass().getClassLoader().getResourceAsStream(fileName), rules, ctx);
    }

    public void runTest(String filename, int expectedResults, Rule rule) throws Throwable {
        Report report = process(filename, rule);
        assertEquals(expectedResults, report.size());
    }
}
