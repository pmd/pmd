package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.CyclomaticComplexityRule;

import java.util.Iterator;

public class CyclomaticComplexityRuleTest extends RuleTst {

    private CyclomaticComplexityRule rule = new CyclomaticComplexityRule();

    public void setUp() {
        rule.setMessage("The {0} ''{1}'' has a Cyclomatic Complexity of {2}.");
    }

    public void testOneMethod() throws Throwable {
        rule.addProperty("reportLevel", "1");
        Report report = process("CyclomaticComplexity1.java", rule);
        Iterator i = report.iterator();
        RuleViolation rv = (RuleViolation)i.next();
        assertTrue(rv.getDescription().indexOf("Highest = 1") != -1);
    }

    public void testNastyComplicatedMethod() throws Throwable {
        rule.addProperty("reportLevel", "10");
        Report report = process("CyclomaticComplexity2.java", rule);
        Iterator i = report.iterator();
        RuleViolation rv = (RuleViolation)i.next();
        assertTrue(rv.getDescription().indexOf("Highest = 12") != -1);
    }

    public void testConstructor() throws Throwable {
        rule.addProperty("reportLevel", "1");
        Report report = process("CyclomaticComplexity3.java", rule);
        Iterator i = report.iterator();
        RuleViolation rv = (RuleViolation)i.next();
        assertTrue(rv.getDescription().indexOf("Highest = 1") != -1);
    }

    public void testLessComplicatedThanReportLevel() throws Throwable {
        rule.addProperty("reportLevel", "10");
        Report report = process("CyclomaticComplexity1.java", rule);
        assertEquals(0, report.size());
    }

}
