/*
 * User: tom
 * Date: Jul 1, 2002
 * Time: 2:18:51 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.Rule;

import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

public class RuleSetFactoryTest  extends TestCase {

    private static final String EMPTY_RULE_SET = "<?xml version=\"1.0\"?><ruleset name=\"test\"></ruleset>";
    private static final String SINGLE_RULE_SET = "<?xml version=\"1.0\"?>" +
                         "<ruleset name=\"test\">" +
                         "<rule name=\"MockRuleName\" class=\"test.net.sourceforge.pmd.MockRule\">" +
                         "</rule></ruleset>";
    private static final String MULTIPLE_RULE_SET = "<?xml version=\"1.0\"?>" +
                         "<ruleset name=\"test\">" +
                         "<rule name=\"MockRuleName\" class=\"test.net.sourceforge.pmd.MockRule\"></rule>" +
                         "<rule name=\"MockRuleName2\" class=\"test.net.sourceforge.pmd.MockRule\"></rule>" +
                         "</ruleset>";

    public RuleSetFactoryTest(String name) {
        super(name);
    }

    public void testCreateEmptyRuleSet() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new ByteArrayInputStream(EMPTY_RULE_SET.getBytes()));
        assertEquals("test", rs.getName());
        assertEquals(0, rs.size());
    }

/*
    public void testCreateSingleRuleSet() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new ByteArrayInputStream(SINGLE_RULE_SET.getBytes()));
        assertEquals("test", rs.getName());
        assertEquals(1, rs.size());
        Iterator i = rs.getRules().iterator();
        Object o = i.next();
        assertTrue(o instanceof MockRule);
        assertTrue(!i.hasNext());
        Rule r = (Rule)o;
        assertEquals("MockRuleName", r.getName());
    }

    public void testCreateMultipleRuleSet() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new StringReader(SINGLE_RULE_SET));
        assertEquals("test", rs.getName());
        assertEquals(2, rs.size());
        Set expected = new HashSet();
        expected.add("MockRuleName");
        expected.add("MockRuleName2");
        for (Iterator i = rs.getRules().iterator(); i.hasNext();) {
            Rule rule = (Rule)i.next();
            assertTrue(expected.contains(rule.getName()));
        }
    }
*/
}
