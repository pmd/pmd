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
import net.sourceforge.pmd.RuleSetNotFoundException;

import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

public class RuleSetFactoryTest  extends TestCase {

    private static final String SEP = System.getProperty("line.separator");

    private static final String EMPTY_RULE_SET =
                        "<?xml version=\"1.0\"?>" + SEP +
                        "<ruleset name=\"test\">" + SEP +
                        "<description>testdesc</description>" + SEP +
                        "</ruleset>";

    private static final String SINGLE_RULE_SET =
                        "<?xml version=\"1.0\"?>" + SEP +
                        "<ruleset name=\"test\">" + SEP +
                        "<description>" + SEP +
                        "testdesc" + SEP +
                        "</description>" + SEP +
                        "<rule " + SEP +
                        "name=\"MockRuleName\" " + SEP +
                        "message=\"avoid the mock rule\" " + SEP +
                        "class=\"test.net.sourceforge.pmd.MockRule\">" +
                        "</rule>" + SEP +
                        "</ruleset>";

    private static final String MULTIPLE_RULE_SET =
                        "<?xml version=\"1.0\"?>" + SEP +
                        "<ruleset name=\"test\">" + SEP +
                        "<description>" + SEP +
                        "testdesc" + SEP +
                        "</description>" + SEP +
                        "<rule name=\"MockRuleName1\" "  + SEP +
                        "message=\"avoid the mock rule\" " + SEP +
                        "class=\"test.net.sourceforge.pmd.MockRule\">" + SEP +
                        "</rule>" + SEP +
                        "<rule name=\"MockRuleName2\" "  + SEP +
                        "message=\"avoid the mock rule\" " + SEP +
                        "class=\"test.net.sourceforge.pmd.MockRule\">" + SEP +
                        "</rule>" + SEP +
                        "</ruleset>";

    private static final String RULE_WITH_PROPERTIES = "<?xml version=\"1.0\"?>" + SEP +
                        "<ruleset name=\"test\">" + SEP +
                        "<description>" + SEP +
                        "testdesc" + SEP +
                        "</description>" + SEP +
                        "<rule name=\"MockRuleName\" " + SEP +
                        "message=\"avoid the mock rule\" " + SEP +
                        "class=\"test.net.sourceforge.pmd.MockRule\">" +  SEP +
                        "<description>" + SEP +
                        "testdesc2" + SEP +
                        "</description>" + SEP +
                        "<properties>" + SEP +
                        "<property name=\"fooBoolean\" value=\"true\"/>" + SEP +
                        "<property name=\"fooDouble\" value=\"1.0\" />" + SEP +
                        "<property name=\"foo\" value=\"bar\"/>" + SEP +
                        "<property name=\"fooint\" value=\"2\"/>" + SEP +
                        "</properties>" +  SEP +
                        "</rule>" + SEP +
                        "</ruleset>";

    public RuleSetFactoryTest(String name) {
        super(name);
    }

    public void testRuleSetNotFound() {
        RuleSetFactory rsf = new RuleSetFactory();
        try {
            rsf.createRuleSet("fooooo");
            throw new RuntimeException("Should have thrown a RuleSetNotFoundException");
        } catch (RuleSetNotFoundException rsnfe) {
            // cool
        }
    }

    public void testCreateEmptyRuleSet() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new ByteArrayInputStream(EMPTY_RULE_SET.getBytes()));
        assertEquals("test", rs.getName());
        assertEquals(0, rs.size());
    }

    public void testSingleRule() {
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
        assertEquals("avoid the mock rule", r.getMessage());
    }

    public void testMultipleRules() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new ByteArrayInputStream(MULTIPLE_RULE_SET.getBytes()));
        assertEquals("test", rs.getName());
        assertEquals(2, rs.size());
        Set expected = new HashSet();
        expected.add("MockRuleName1");
        expected.add("MockRuleName2");
        for (Iterator i = rs.getRules().iterator(); i.hasNext();) {
            Rule rule = (Rule)i.next();
            assertTrue(expected.contains(rule.getName()));
        }
    }

    public void testProps() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new ByteArrayInputStream(RULE_WITH_PROPERTIES.getBytes()));
        Rule r = (Rule)rs.getRules().iterator().next();
		assertTrue( r.hasProperty("foo"));
        assertEquals("bar", r.getStringProperty("foo"));
        assertEquals(2, r.getIntProperty("fooint"));
        assertTrue( r.hasProperty("fooBoolean"));
        assertTrue(r.getBooleanProperty("fooBoolean"));
        assertTrue( r.hasProperty("fooDouble"));
        assertEquals( 1.0, r.getDoubleProperty("fooDouble"),  0.05 );
        assertTrue( !r.hasProperty("BuggleFish"));
        assertTrue(r.getDescription().indexOf("testdesc2") != -1);
    }

/*
    public void testExternalReferences() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new ByteArrayInputStream(EXTERNAL_REFERENCE_RULE_SET.getBytes()));
        assertEquals(1, rs.size());
    }
    private static final String EXTERNAL_REFERENCE_RULE_SET = "<?xml version=\"1.0\"?>" +
                         "<ruleset name=\"test\">\r\n<description>testdesc</description><rule ref=\"rulesets/basic.xml/EmptyCatchBlock\"/></ruleset>";
    private static final String SINGLE_RULE_NO_PROPS = "<?xml version=\"1.0\"?>" +
                         "<ruleset name=\"test\">\r\n<description>testdesc</description>" +
                         "<rule name=\"MockRuleName\" message=\"avoid the mock rule\" class=\"test.net.sourceforge.pmd.MockRule\">" +
                         "<properties></properties>" +
                         "</rule></ruleset>";
*/
}
