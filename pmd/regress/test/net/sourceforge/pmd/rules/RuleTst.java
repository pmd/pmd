package test.net.sourceforge.pmd.rules;

/**
 * RuleTst
 *
 * Extend your Rule TestCases from here to get some
 * juicy code sharing.
 */

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.util.ResourceLoader;

import java.io.StringReader;

public class RuleTst extends TestCase {

    private static final String TEST_FILE_DIR = "test-data/";

    public void runTest(String filename, int expectedResults, Rule rule) throws Throwable {
        assertEquals(expectedResults, process(filename, rule).size());
    }

    public void runTestString(String filename, int expectedResults, Rule rule) throws Throwable {
        assertEquals(expectedResults, processWithStringReader(filename, rule).size());
    }

    public Report process(String fileName, Rule rule) throws Throwable {
        Report report = new Report();
        process(fileName, rule, report);
        return report;
    }

    public void process(String fileName, Rule rule, Report report) throws Throwable {
        processWithFullPath(TEST_FILE_DIR + fileName, rule, report);
    }

    public void processWithFullPath(String fullFileName, Rule rule, Report report) throws Throwable {
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        ctx.setSourceCodeFilename(fullFileName);
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        p.processFile(ResourceLoader.loadResourceAsStream(fullFileName), rules, ctx);
    }

    public Report processWithStringReader(String code, Rule rule) throws Throwable {
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        Report report = new Report();
        ctx.setReport(report);
        ctx.setSourceCodeFilename("n/a");
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        p.processFile(new StringReader(code), rules, ctx);
        return report;
    }


}
