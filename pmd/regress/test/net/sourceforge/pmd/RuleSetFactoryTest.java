/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RuleSetFactoryTest extends TestCase {

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

    public void testXPathPluginnameProperty() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new ByteArrayInputStream(RULE_WITH_XPATH_AND_PLUGINNAME.getBytes()));
        Rule r = (Rule) rs.getRules().iterator().next();
        assertTrue(r.hasProperty("pluginname"));
    }

    public void testXPath() {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet(new ByteArrayInputStream(RULE_WITH_XPATH.getBytes()));
        Rule r = (Rule) rs.getRules().iterator().next();
        assertTrue(r.hasProperty("xpath"));
        assertTrue(r.getStringProperty("xpath").indexOf(" //Block ") != -1);
    }

    private static final String EMPTY_RULE_SET =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>testdesc</description>" + PMD.EOL +
            "</ruleset>";

    private static final String SINGLE_RULE_SET =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>" + PMD.EOL +
            "testdesc" + PMD.EOL +
            "</description>" + PMD.EOL +
            "<rule " + PMD.EOL +
            "name=\"MockRuleName\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" +
            "</rule></ruleset>";

    private static final String MULTIPLE_RULE_SET =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>" + PMD.EOL +
            "testdesc" + PMD.EOL + "</description>" + PMD.EOL +
            "<rule name=\"MockRuleName1\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" + PMD.EOL +
            "</rule>" + PMD.EOL +
            "<rule name=\"MockRuleName2\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" + PMD.EOL +
            "</rule></ruleset>";

    private static final String RULE_WITH_PROPERTIES =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>" + PMD.EOL +
            "testdesc" + PMD.EOL +
            "</description>" + PMD.EOL +
            "<rule name=\"MockRuleName\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" + PMD.EOL +
            "<description>" + PMD.EOL + "testdesc2" + PMD.EOL +
            "</description>" + PMD.EOL +
            "<properties>" + PMD.EOL +
            "<property name=\"fooBoolean\" value=\"true\"/>" + PMD.EOL +
            "<property name=\"fooDouble\" value=\"1.0\" />" + PMD.EOL +
            "<property name=\"foo\" value=\"bar\"/>" + PMD.EOL +
            "<property name=\"fooint\" value=\"2\"/>" + PMD.EOL +
            "</properties>" + PMD.EOL +
            "</rule></ruleset>";

    private static final String RULE_WITH_XPATH =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>" + PMD.EOL +
            "testdesc" + PMD.EOL +
            "</description>" + PMD.EOL +
            "<priority>3</priority>" + PMD.EOL +
            "<rule name=\"MockRuleName\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" + PMD.EOL +
            "<description>" + PMD.EOL +
            "testdesc2" + PMD.EOL +
            "</description>" + PMD.EOL +
            "<properties>" + PMD.EOL +
            "<property name=\"xpath\">" + PMD.EOL +
            "<value>" + PMD.EOL +
            "<![CDATA[ //Block ]]>" + PMD.EOL +
            "</value>" + PMD.EOL +
            "</property>" + PMD.EOL +
            "</properties>" + PMD.EOL +
            "</rule></ruleset>";

    private static final String RULE_WITH_XPATH_AND_PLUGINNAME =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>" + PMD.EOL +
            "testdesc" + PMD.EOL +
            "</description>" + PMD.EOL +
            "<priority>3</priority>" + PMD.EOL +
            "<rule name=\"MockRuleName\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" + PMD.EOL +
            "<description>" + PMD.EOL +
            "testdesc2" + PMD.EOL +
            "</description>" + PMD.EOL +
            "<properties>" + PMD.EOL +
            "<property name=\"xpath\" pluginname=\"true\">" + PMD.EOL +
            "<value>" + PMD.EOL +
            "<![CDATA[ //Block ]]>" + PMD.EOL +
            "</value>" + PMD.EOL +
            "</property>" + PMD.EOL +
            "</properties>" + PMD.EOL +
            "</rule></ruleset>";


    private static final String SINGLE_RULE_SET_WITH_PRIORITY =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>" + PMD.EOL +
            "testdesc" + PMD.EOL +
            "</description>" + PMD.EOL +
            "<rule " + PMD.EOL +
            "name=\"MockRuleName\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" +
            "<priority>3</priority>" + PMD.EOL +
            "</rule></ruleset>";

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
                             "<rule name=\"MockRuleName\" message=\"avoid the mock rule\" class=\"test.net.sourceforge.pmd.testframework.MockRule\">" +
                             "<properties></properties>" +
                             "</rule></ruleset>";
    */
}
