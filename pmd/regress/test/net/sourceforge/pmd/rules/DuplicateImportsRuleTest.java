/*
 * User: tom
 * Date: Jul 12, 2002
 * Time: 10:51:59 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.DuplicateImportsRule;
import org.cougaar.util.pmd.DontCreateTimersRule;

public class DuplicateImportsRuleTest extends RuleTst {
    public DuplicateImportsRuleTest(String name) {
        super(name);
    }

    public void test1() throws Throwable {
        Report report = process("DuplicateImports.java", new DuplicateImportsRule());
        assertEquals(1, report.size());
        assertEquals(new DuplicateImportsRule(), ((RuleViolation)report.iterator().next()).getRule());
    }
}
