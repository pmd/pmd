package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RuleSetFactoryTest extends TestCase {

    private static final String EOL = System.getProperty("line.separator", "\n");

    private static final String EMPTY_RULE_SET =
            "<?xml version=\"1.0\"?>" + EOL +
            "<ruleset name=\"test\">" + EOL +
            "<description>testdesc</description>" + EOL +
            "</ruleset>";

    private static final String SINGLE_RULE_SET =
            "<?xml version=\"1.0\"?>" + EOL +
            "<ruleset name=\"test\">" + EOL +
            "<description>" + EOL +
            "testdesc" + EOL +
            "</description>" + EOL +
            "<rule " + EOL +
            "name=\"MockRuleName\" " + EOL +
            "message=\"avoid the mock rule\" " + EOL +
            "class=\"test.net.sourceforge.pmd.MockRule\">" +
            "</rule></ruleset>";

    private static final String MULTIPLE_RULE_SET =
            "<?xml version=\"1.0\"?>" + EOL +
            "<ruleset name=\"test\">" + EOL +
            "<description>" + EOL +
            "testdesc" + EOL + "</description>" + EOL +
            "<rule name=\"MockRuleName1\" " + EOL +
            "message=\"avoid the mock rule\" " + EOL +
            "class=\"test.net.sourceforge.pmd.MockRule\">" + EOL +
            "</rule>" + EOL +
            "<rule name=\"MockRuleName2\" " + EOL +
            "message=\"avoid the mock rule\" " + EOL +
            "class=\"test.net.sourceforge.pmd.MockRule\">" + EOL +
            "</rule></ruleset>";

    private static final String RULE_WITH_PROPERTIES =
            "<?xml version=\"1.0\"?>" + EOL +
            "<ruleset name=\"test\">" + EOL +
            "<description>" + EOL +
            "testdesc" + EOL +
            "</description>" + EOL +
            "<rule name=\"MockRuleName\" " + EOL +
            "message=\"avoid the mock rule\" " + EOL +
            "class=\"test.net.sourceforge.pmd.MockRule\">" + EOL +
            "<description>" + EOL + "testdesc2" + EOL +
            "</description>" + EOL +
            "<properties>" + EOL +
            "<property name=\"fooBoolean\" value=\"true\"/>" + EOL +
            "<property name=\"fooDouble\" value=\"1.0\" />" + EOL +
            "<property name=\"foo\" value=\"bar\"/>" + EOL +
            "<property name=\"fooint\" value=\"2\"/>" + EOL +
            "</properties>" + EOL +
            "</rule></ruleset>";

    private static final String RULE_WITH_XPATH =
            "<?xml version=\"1.0\"?>" + EOL +
            "<ruleset name=\"test\">" + EOL +
            "<description>" + EOL +
            "testdesc" + EOL +
            "</description>" + EOL +
            "<priority>3</priority>" + EOL +
            "<rule name=\"MockRuleName\" " + EOL +
            "message=\"avoid the mock rule\" " + EOL +
            "class=\"test.net.sourceforge.pmd.MockRule\">" + EOL +
            "<description>" + EOL +
            "testdesc2" + EOL +
            "</description>" + EOL +
            "<properties>" + EOL +
            "<property name=\"xpath\">" + EOL +
            "<value>" + EOL +
            "<![CDATA[ //Block ]]>" + EOL +
            "</value>" + EOL +
            "</property>" + EOL +
            "</properties>" + EOL +
            "</rule></ruleset>";


    private static final String SINGLE_RULE_SET_WITH_PRIORITY =
            "<?xml version=\"1.0\"?>" + EOL +
            "<ruleset name=\"test\">" + EOL +
            "<description>" + EOL +
            "testdesc" + EOL +
            "</description>" + EOL +
            "<rule " + EOL +
            "name=\"MockRuleName\" " + EOL +
            "message=\"avoid the mock rule\" " + EOL +
            "class=\"test.net.sourceforge.pmd.MockRule\">" +
            "<priority>3</priority>" + EOL +
            "</rule></ruleset>";

    public void testSingleRuleWithPriority() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new ByteArrayInputStream(SINGLE_RULE_SET_WITH_PRIORITY.getBytes()));
        Rule r = (Rule)rs.getRules().iterator().next();
        assertEquals(3, r.getPriority());
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
        assertEquals(1, rs.size());
        Rule r = (Rule)rs.getRules().iterator().next();
        assertEquals("MockRuleName", r.getName());
        assertEquals("avoid the mock rule", r.getMessage());
    }

    public void testMultipleRules() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new ByteArrayInputStream(MULTIPLE_RULE_SET.getBytes()));
        assertEquals(2, rs.size());
        Set expected = new HashSet();
        expected.add("MockRuleName1");
        expected.add("MockRuleName2");
        for (Iterator i = rs.getRules().iterator(); i.hasNext();) {
            assertTrue(expected.contains(((Rule) i.next()).getName()));
        }
    }

    public void testProps() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new ByteArrayInputStream(RULE_WITH_PROPERTIES.getBytes()));
        Rule r = (Rule) rs.getRules().iterator().next();
        assertTrue(r.hasProperty("foo"));
        assertEquals("bar", r.getStringProperty("foo"));
        assertEquals(2, r.getIntProperty("fooint"));
        assertTrue(r.hasProperty("fooBoolean"));
        assertTrue(r.getBooleanProperty("fooBoolean"));
        assertTrue(r.hasProperty("fooDouble"));
        assertEquals(1.0, r.getDoubleProperty("fooDouble"), 0.05);
        assertTrue(!r.hasProperty("BuggleFish"));
        assertTrue(r.getDescription().indexOf("testdesc2") != -1);
    }

    public void testXPath() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new ByteArrayInputStream(RULE_WITH_XPATH.getBytes()));
        Rule r = (Rule) rs.getRules().iterator().next();
        assertTrue(r.hasProperty("xpath"));
        assertEquals(" //Block ", r.getStringProperty("xpath"));
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
