/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 2:34:20 PM
 */
package test.net.sourceforge.pmd.rules;

import junit.framework.TestCase;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.UnnecessaryConversionTemporaryRule;

public class UnnecessaryTemporariesRuleTest extends RuleTst{

    public UnnecessaryTemporariesRuleTest(String name) {
        super(name);
    }

    public void testUnnecessaryTemporaries() {
        Report report = process2("UnnecessaryTemporary.java", new UnnecessaryConversionTemporaryRule());
        assertEquals(6, report.countViolationsInCurrentFile());
        assertEquals(new UnnecessaryConversionTemporaryRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }
}
