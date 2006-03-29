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
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.JavaParser;
import test.net.sourceforge.pmd.testframework.MockRule;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RuleSetTest extends TestCase {

    private String javaCode = "public class Test { }";

    public void testNoDFA() {
        RuleSet rs = new RuleSet();
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        rs.addRule(mock);
        assertFalse(rs.usesDFA());
    }

    public void testIncludesRuleWithDFA() {
        RuleSet rs = new RuleSet();
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        mock.setUsesDFA();
        rs.addRule(mock);
        assertTrue(rs.usesDFA());
    }

    public void testAccessors() {
        RuleSet rs = new RuleSet();
        rs.setName("foo");
        assertEquals("name mismatch", "foo", rs.getName());
        rs.setDescription("bar");
        assertEquals("description mismatch", "bar", rs.getDescription());
    }

    public void testGetRuleByName() {
        RuleSet rs = new RuleSet();
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        rs.addRule(mock);
        assertEquals("unable to fetch rule by name", mock, rs.getRuleByName("name"));
    }

    public void testGetRuleByName2() {
        RuleSet rs = new RuleSet();
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        rs.addRule(mock);
        assertNull("the rule FooRule must not be found!", rs.getRuleByName("FooRule"));
    }

    public void testRuleList() {
        RuleSet IUT = new RuleSet();

        assertEquals("Size of RuleSet isn't zero.", 0, IUT.size());

        MockRule rule = new MockRule("name", "desc", "msg", "rulesetname");
        IUT.addRule(rule);

        assertEquals("Size of RuleSet isn't one.", 1, IUT.size());

        Collection rules = IUT.getRules();

        Iterator i = rules.iterator();
        assertTrue("Empty Set", i.hasNext());
        assertEquals("Returned set of wrong size.", 1, rules.size());
        assertEquals("Rule isn't in ruleset.", rule, i.next());
    }

    public void testAddRuleSet() {
        RuleSet set1 = new RuleSet();
        set1.addRule(new MockRule("name", "desc", "msg", "rulesetname"));
        RuleSet set2 = new RuleSet();
        set2.addRule(new MockRule("name2", "desc", "msg", "rulesetname"));
        set1.addRuleSet(set2);
        assertEquals("ruleset size wrong", 2, set1.size());
    }

    public void testApply0Rules() throws Throwable {
        RuleSet IUT = new RuleSet();
        verifyRuleSet(IUT, 0, new HashSet());
    }

    public void testEquals1() {
        RuleSet s = new RuleSet();
        assertFalse("A ruleset cannot be equals to null", s.equals(null));
    }

    public void testEquals2() {
        RuleSet s = new RuleSet();
        assertTrue("A rulset must be equals to itself", s.equals(s));
    }

    public void testEquals3() {
        RuleSet s = new RuleSet();
        s.setName("basic rules");
        assertFalse("A ruleset cannot be equals to another kind of object", s.equals("basic rules"));
    }

    public void testEquals4() {
        RuleSet s1 = new RuleSet();
        s1.setName("my ruleset");
        s1.addRule(new MockRule("name", "desc", "msg", "rulesetname"));

        RuleSet s2 = new RuleSet();
        s2.setName("my ruleset");
        s2.addRule(new MockRule("name", "desc", "msg", "rulesetname"));

        assertEquals("2 rulesets with same name and rules must be equals", s1, s2);
        assertEquals("Equals rulesets must have the same hashcode", s1.hashCode(), s2.hashCode());
    }

    public void testEquals5() {
        RuleSet s1 = new RuleSet();
        s1.setName("my ruleset");
        s1.addRule(new MockRule("name", "desc", "msg", "rulesetname"));

        RuleSet s2 = new RuleSet();
        s2.setName("my other ruleset");
        s2.addRule(new MockRule("name", "desc", "msg", "rulesetname"));

        assertFalse("2 rulesets with different name but same rules must not be equals", s1.equals(s2));
        assertFalse("Rulesets that are not equals should not have the same hashcode", s1.hashCode() == s2.hashCode());
    }

    public void testEquals6() {
        RuleSet s1 = new RuleSet();
        s1.setName("my ruleset");
        s1.addRule(new MockRule("name", "desc", "msg", "rulesetname"));

        RuleSet s2 = new RuleSet();
        s2.setName("my ruleset");
        s2.addRule(new MockRule("other rule", "desc", "msg", "rulesetname"));

        assertFalse("2 rulesets with same name but different rules must not be equals", s1.equals(s2));
        assertFalse("Rulesets that are not equals should not have the same hashcode", s1.hashCode() == s2.hashCode());
    }

    protected void verifyRuleSet(RuleSet IUT, int size, Set values) throws Throwable {

        RuleContext context = new RuleContext();
        Set reportedValues = new HashSet();
        context.setReport(new Report());
        IUT.apply(makeCompilationUnits(), context);

        assertEquals("Invalid number of Violations Reported", size, context.getReport().size());

        Iterator violations = context.getReport().iterator();
        while (violations.hasNext()) {
            RuleViolation violation = (RuleViolation) violations.next();

            reportedValues.add(violation);
            assertTrue("Unexpected Violation Returned: " + violation, values.contains(violation));
        }

        Iterator expected = values.iterator();
        while (expected.hasNext()) {
            RuleViolation violation = (RuleViolation) expected.next();
            assertTrue("Expected Violation not Returned: " + violation, reportedValues.contains(violation));
        }
    }


    protected List makeCompilationUnits() throws Throwable {
        List RC = new ArrayList();
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(javaCode));
        RC.add(parser.CompilationUnit());
        return RC;
    }
}
