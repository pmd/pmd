/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 10:58:49 AM
 */
package test.net.sourceforge.pmd.rules;

import junit.framework.TestCase;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.renderers.Renderer;
import org.cougaar.util.pmd.SystemPropsRule;

public class SystemPropsRuleTest extends RuleTst {
    public SystemPropsRuleTest(String name) {
        super(name);
    }

    public void testProps() throws Throwable {
        Report report = process("ContainsSystemGetProps.java", new SystemPropsRule());
        assertEquals(3, report.size());
        assertEquals(new SystemPropsRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testProps2() throws Throwable {
        Report report = process("ContainsSystemGetProps2.java", new SystemPropsRule());
        assertTrue(report.isEmpty());
    }
}
