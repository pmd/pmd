package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.UnnecessaryConversionTemporaryRule;

public class UnnecessaryTemporariesRuleTest extends RuleTst {

    public void testUnnecessaryTemporaries() throws Throwable {
        Report report = runTestFromFile("UnnecessaryTemporary.java", new UnnecessaryConversionTemporaryRule());
        assertEquals(6, report.size());
        assertEquals(new UnnecessaryConversionTemporaryRule(), ((RuleViolation) report.iterator().next()).getRule());
    }
}
