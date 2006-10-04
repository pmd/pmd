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
import net.sourceforge.pmd.util.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RuleSetFactoryTest extends TestCase {

    public void testRefs() throws Throwable {
        InputStream in = ResourceLoader.loadResourceAsStream("rulesets/favorites.xml", this.getClass().getClassLoader());
        if (in == null) {
            throw new RuleSetNotFoundException("Can't find resource   Make sure the resource is a valid file or URL or is on the CLASSPATH.  Here's the current classpath: " + System.getProperty("java.class.path"));
        }
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createSingleRuleSet("rulesets/favorites.xml");
        assertNotNull(rs.getRuleByName("WhileLoopsMustUseBraces"));
    }

    public void testRuleSetNotFound() {
        RuleSetFactory rsf = new RuleSetFactory();
        try {
            rsf.createSingleRuleSet("fooooo");
            fail("Should have thrown a RuleSetNotFoundException");
        } catch (RuleSetNotFoundException rsnfe) {
            // cool
        }
    }

    public void testCreateEmptyRuleSet() {
        RuleSet rs = loadRuleSet(EMPTY_RULESET);
        assertEquals("test", rs.getName());
        assertEquals(0, rs.size());
    }

    public void testSingleRule() {
        RuleSet rs = loadRuleSet(SINGLE_RULE);
        assertEquals(1, rs.size());
        Rule r = (Rule) rs.getRules().iterator().next();
        assertEquals("MockRuleName", r.getName());
        assertEquals("avoid the mock rule", r.getMessage());
    }

    public void testMultipleRules() {
        RuleSet rs = loadRuleSet(MULTIPLE_RULES);
        assertEquals(2, rs.size());
        Set expected = new HashSet();
        expected.add("MockRuleName1");
        expected.add("MockRuleName2");
        for (Iterator i = rs.getRules().iterator(); i.hasNext();) {
            assertTrue(expected.contains(((Rule) i.next()).getName()));
        }
    }

    public void testSingleRuleWithPriority() {
        assertEquals(3, loadFirstRule(PRIORITY).getPriority());
    }

    public void testProps() {
        Rule r = loadFirstRule(PROPERTIES);
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
        Rule r = loadFirstRule(XPATH_PLUGINNAME);
        assertTrue(r.hasProperty("pluginname"));
    }

    public void testXPath() {
        Rule r = loadFirstRule(XPATH);
        assertTrue(r.hasProperty("xpath"));
        assertTrue(r.getStringProperty("xpath").indexOf(" //Block ") != -1);
    }

    public void testFacadesOffByDefault() {
        Rule r = loadFirstRule(XPATH);
        assertFalse(r.usesDFA());
    }

    public void testDFAFlag() {
        assertTrue(loadFirstRule(DFA).usesDFA());
    }

    public void testExternalReferenceOverride() {
        Rule r = loadFirstRule(REF_OVERRIDE);
        assertEquals("TestNameOverride", r.getName());
        assertEquals("Test message override", r.getMessage());
        assertEquals("Test description override", r.getDescription());
        assertEquals("Test example override", r.getExample());
        assertEquals(3, r.getPriority());
        assertTrue(r.hasProperty("test2"));
        assertEquals("override2", r.getStringProperty("test2"));
        assertTrue(r.hasProperty("test3"));
        assertEquals("override3", r.getStringProperty("test3"));
        assertTrue(r.hasProperty("test4"));
        assertEquals("new property", r.getStringProperty("test4"));
    }

    public void testOverrideMessage() {
        Rule r = loadFirstRule(REF_OVERRIDE_ORIGINAL_NAME);
        assertEquals("TestMessageOverride", r.getMessage());
    }

    public void testOverrideMessageOneElem() {
        Rule r = loadFirstRule(REF_OVERRIDE_ORIGINAL_NAME_ONE_ELEM);
        assertEquals("TestMessageOverride", r.getMessage());
    }

    public void testExternalRef() {
        try {
            loadFirstRule(REF_MISPELLED_XREF);
            fail("Whoa, should have gotten an IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // cool
        }
    }

    public void testSetPriority() {
        RuleSetFactory rsf = new RuleSetFactory();
        rsf.setMinimumPriority(2);
        assertEquals(0, rsf.createRuleSet(new ByteArrayInputStream(SINGLE_RULE.getBytes())).size());
        rsf.setMinimumPriority(4);
        assertEquals(1, rsf.createRuleSet(new ByteArrayInputStream(SINGLE_RULE.getBytes())).size());
    }

    private static final String REF_OVERRIDE_ORIGINAL_NAME =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            " <description>testdesc</description>" + PMD.EOL +
            " <rule " + PMD.EOL +
            "  ref=\"rulesets/unusedcode.xml/UnusedLocalVariable\" message=\"TestMessageOverride\"> " + PMD.EOL +
            " </rule>" + PMD.EOL +
            "</ruleset>";

    private static final String REF_MISPELLED_XREF =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            " <description>testdesc</description>" + PMD.EOL +
            " <rule " + PMD.EOL +
            "  ref=\"rulesets/unusedcode.xml/FooUnusedLocalVariable\"> " + PMD.EOL +
            " </rule>" + PMD.EOL +
            "</ruleset>";

    private static final String REF_OVERRIDE_ORIGINAL_NAME_ONE_ELEM =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            " <description>testdesc</description>" + PMD.EOL +
            " <rule ref=\"rulesets/unusedcode.xml/UnusedLocalVariable\" message=\"TestMessageOverride\"/> " + PMD.EOL +
            "</ruleset>";

    private static final String REF_OVERRIDE =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            " <description>testdesc</description>" + PMD.EOL +
            " <rule " + PMD.EOL +
            "  ref=\"rulesets/unusedcode.xml/UnusedLocalVariable\" " + PMD.EOL +
            "  name=\"TestNameOverride\" " + PMD.EOL +
            "  message=\"Test message override\"> " + PMD.EOL +
            "  <description>Test description override</description>" + PMD.EOL +
            "  <example>Test example override</example>" + PMD.EOL +
            "  <priority>3</priority>" + PMD.EOL +
            "  <properties>" + PMD.EOL +
            "   <property name=\"test2\" value=\"override2\"/>" + PMD.EOL +
            "   <property name=\"test3\"><value>override3</value></property>" + PMD.EOL +
            "   <property name=\"test4\" value=\"new property\"/>" + PMD.EOL +
            "  </properties>" + PMD.EOL +
            " </rule>" + PMD.EOL +
            "</ruleset>";

    private static final String EMPTY_RULESET =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>testdesc</description>" + PMD.EOL +
            "</ruleset>";

    private static final String SINGLE_RULE =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>testdesc</description>" + PMD.EOL +
            "<rule " + PMD.EOL +
            "name=\"MockRuleName\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" +
            "<priority>3</priority>" + PMD.EOL +
            "</rule></ruleset>";

    private static final String MULTIPLE_RULES =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>testdesc</description>" + PMD.EOL +
            "<rule name=\"MockRuleName1\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" + PMD.EOL +
            "</rule>" + PMD.EOL +
            "<rule name=\"MockRuleName2\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" + PMD.EOL +
            "</rule></ruleset>";

    private static final String PROPERTIES =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>testdesc</description>" + PMD.EOL +
            "<rule name=\"MockRuleName\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" + PMD.EOL +
            "<description>testdesc2</description>" + PMD.EOL +
            "<properties>" + PMD.EOL +
            "<property name=\"fooBoolean\" value=\"true\"/>" + PMD.EOL +
            "<property name=\"fooDouble\" value=\"1.0\" />" + PMD.EOL +
            "<property name=\"foo\" value=\"bar\"/>" + PMD.EOL +
            "<property name=\"fooint\" value=\"2\"/>" + PMD.EOL +
            "</properties>" + PMD.EOL +
            "</rule></ruleset>";

    private static final String XPATH =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>testdesc</description>" + PMD.EOL +
            "<priority>3</priority>" + PMD.EOL +
            "<rule name=\"MockRuleName\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" + PMD.EOL +
            "<description>testdesc2</description>" + PMD.EOL +
            "<properties>" + PMD.EOL +
            "<property name=\"xpath\">" + PMD.EOL +
            "<value>" + PMD.EOL +
            "<![CDATA[ //Block ]]>" + PMD.EOL +
            "</value>" + PMD.EOL +
            "</property>" + PMD.EOL +
            "</properties>" + PMD.EOL +
            "</rule></ruleset>";

    private static final String XPATH_PLUGINNAME =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>testdesc</description>" + PMD.EOL +
            "<priority>3</priority>" + PMD.EOL +
            "<rule name=\"MockRuleName\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" + PMD.EOL +
            "<description>testdesc2</description>" + PMD.EOL +
            "<properties>" + PMD.EOL +
            "<property name=\"xpath\" pluginname=\"true\">" + PMD.EOL +
            "<value>" + PMD.EOL +
            "<![CDATA[ //Block ]]>" + PMD.EOL +
            "</value>" + PMD.EOL +
            "</property>" + PMD.EOL +
            "</properties>" + PMD.EOL +
            "</rule></ruleset>";


    private static final String PRIORITY =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>testdesc</description>" + PMD.EOL +
            "<rule " + PMD.EOL +
            "name=\"MockRuleName\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" +
            "<priority>3</priority>" + PMD.EOL +
            "</rule></ruleset>";

    private static final String DFA =
            "<?xml version=\"1.0\"?>" + PMD.EOL +
            "<ruleset name=\"test\">" + PMD.EOL +
            "<description>testdesc</description>" + PMD.EOL +
            "<rule " + PMD.EOL +
            "name=\"MockRuleName\" " + PMD.EOL +
            "message=\"avoid the mock rule\" " + PMD.EOL +
            "dfa=\"true\" " + PMD.EOL +
            "class=\"test.net.sourceforge.pmd.testframework.MockRule\">" +
            "<priority>3</priority>" + PMD.EOL +
            "</rule></ruleset>";


    private Rule loadFirstRule(String ruleSetName) {
        RuleSet rs = loadRuleSet(ruleSetName);
        return ((Rule) (rs.getRules().iterator().next()));
    }

    private RuleSet loadRuleSet(String ruleSetName) {
        RuleSetFactory rsf = new RuleSetFactory();
        return rsf.createRuleSet(new ByteArrayInputStream(ruleSetName.getBytes()));
    }
/*
    public void testExternalReferences() {
        RuleSet rs = loadRuleSet(EXTERNAL_REFERENCE_RULE_SET);
        assertEquals(1, rs.size());
        assertEquals(UnusedLocalVariableRule.class, rs.getRuleByName("UnusedLocalVariable").getClass());
    }
        private static final String EXTERNAL_REFERENCE_RULE_SET =
                "<?xml version=\"1.0\"?>" + PMD.EOL +
                "<ruleset name=\"test\">" + PMD.EOL +
                "<description>testdesc</description>" + PMD.EOL +
                "<rule ref=\"rulesets/unusedcode.xml/UnusedLocalVariable\"/>" + PMD.EOL +
                "</ruleset>";
*/
}
