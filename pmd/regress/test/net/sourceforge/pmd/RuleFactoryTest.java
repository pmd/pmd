/*
 * User: tom
 * Date: Jun 20, 2002
 * Time: 8:43:20 AM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;

import java.util.List;

import net.sourceforge.pmd.RuleFactory;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.DontCreateTimersRule;
import net.sourceforge.pmd.rules.EmptyIfStmtRule;

public class RuleFactoryTest extends TestCase {

    public RuleFactoryTest(String name) {
        super(name);
    }

    public void testCougaar() {
        RuleFactory rf = new RuleFactory();
        List r = rf.createRules(RuleFactory.COUGAAR);
        assertTrue(r.contains(new DontCreateTimersRule()));
    }

    public void testAll() {
        RuleFactory rf = new RuleFactory();
        List r = rf.createRules(RuleFactory.ALL);
        assertTrue(r.contains(new EmptyIfStmtRule()));
    }

    public void testGeneral() {
        RuleFactory rf = new RuleFactory();
        List r = rf.createRules(RuleFactory.GENERAL);
        assertTrue(r.contains(new EmptyIfStmtRule()));
        assertTrue(!r.contains(new DontCreateTimersRule()));
    }

    public void testException() {
        RuleFactory rf = new RuleFactory();
        try {
            rf.createRules("blah");
        } catch (Exception e) {
            return; // cool
        }
        throw new RuntimeException("Should have thrown RuntimeException");
    }

    public void testConcatenatedList() {
        RuleFactory rf = new RuleFactory();
        String list = rf.getConcatenatedRuleSetList();
        assertTrue(list.indexOf("design") != -1);
    }

    public void testContains() {
        RuleFactory rf = new RuleFactory();
        assertTrue(rf.containsRuleSet("all"));
        assertTrue(!rf.containsRuleSet("foo"));
    }

}
