/*
 * User: tom
 * Date: Jun 20, 2002
 * Time: 8:43:20 AM
 */
package test.com.infoether.pmd;

import junit.framework.TestCase;

import java.util.List;

import com.infoether.pmd.RuleFactory;
import com.infoether.pmd.Rule;
import com.infoether.pmd.DontCreateTimersRule;
import com.infoether.pmd.EmptyIfStmtRule;

public class RuleFactoryTest extends TestCase {
    public RuleFactoryTest(String name) {
        super(name);
    }

    public void testCougaar() {
        List r = RuleFactory.createRules(RuleFactory.COUGAAR);
        assertTrue(r.contains(new DontCreateTimersRule()));
    }

    public void testAll() {
        List r = RuleFactory.createRules(RuleFactory.ALL);
        assertTrue(r.contains(new EmptyIfStmtRule()));
    }

    public void testGeneral() {
        List r = RuleFactory.createRules(RuleFactory.GENERAL);
        assertTrue(r.contains(new EmptyIfStmtRule()));
        assertTrue(!r.contains(new DontCreateTimersRule()));
    }

    public void testException() {
        try {
            RuleFactory.createRules("blah");
        } catch (Exception e) {
            return; // cool
        }
        throw new RuntimeException("Should have thrown RuntimeException");
    }

}
