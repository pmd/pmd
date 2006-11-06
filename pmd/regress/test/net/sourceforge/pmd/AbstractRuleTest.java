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
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.properties.StringProperty;
import net.sourceforge.pmd.symboltable.SourceFileScope;

import java.util.HashMap;
import java.util.Map;

public class AbstractRuleTest extends TestCase {
	
    private static class MyRule extends AbstractRule {
    	private static final PropertyDescriptor pd = new StringProperty("foo", "foo property", "x", 1.0f);

    	private static final PropertyDescriptor xpath = new StringProperty("xpath", "xpath property", "", 2.0f);

        private static final Map propertyDescriptorsByName = asFixedMap(new PropertyDescriptor[] { pd, xpath });

        protected Map propertiesByName() {
        	return propertyDescriptorsByName;
        }

        public MyRule() {
            setName("MyRule");
            setMessage("my rule msg");
            setPriority(3);
            setProperty(pd, "value");
        }
    }

    private static class MyOtherRule extends AbstractRule {
    	private static final PropertyDescriptor pd = new StringProperty("foo", "foo property", "x", 1.0f);

		private static final Map propertyDescriptorsByName = asFixedMap(new PropertyDescriptor[] { pd });

        protected Map propertiesByName() {
        	return propertyDescriptorsByName;
        }

		public MyOtherRule() {
            setName("MyOtherRule");
            setMessage("my other rule");
            setPriority(3);
            setProperty(pd, "value");
        }
    }

    public AbstractRuleTest(String name) {
        super(name);
    }

    public void testCreateRV() {
        MyRule r = new MyRule();
        r.setRuleSetName("foo");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginColumn(5);
        s.testingOnly__setBeginLine(5);
        s.setScope(new SourceFileScope("foo"));
        RuleViolation rv = new RuleViolation(r, ctx, s);
        assertEquals("Line number mismatch!", 5, rv.getBeginLine());
        assertEquals("Filename mismatch!", "filename", rv.getFilename());
        assertEquals("Rule object mismatch!", r, rv.getRule());
        assertEquals("Rule msg mismatch!", "my rule msg", rv.getDescription());
        assertEquals("RuleSet name mismatch!", "foo", rv.getRule().getRuleSetName());
    }

    public void testCreateRV2() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginColumn(5);
        s.testingOnly__setBeginLine(5);
        s.setScope(new SourceFileScope("foo"));
        RuleViolation rv = new RuleViolation(r, ctx, s, "specificdescription");
        assertEquals("Line number mismatch!", 5, rv.getBeginLine());
        assertEquals("Filename mismatch!", "filename", rv.getFilename());
        assertEquals("Rule object mismatch!", r, rv.getRule());
        assertEquals("Rule description mismatch!", "specificdescription", rv.getDescription());
    }

    public void testRuleExclusion() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        Map m = new HashMap();
        m.put(new Integer(5), "");
        ctx.setReport(new Report());
        ctx.excludeLines(m);
        ctx.setSourceCodeFilename("filename");
        SimpleNode n = new SimpleJavaNode(1);
        n.testingOnly__setBeginColumn(5);
        n.testingOnly__setBeginLine(5);
        n.setScope(new SourceFileScope("foo"));
        RuleViolation rv = new RuleViolation(r, ctx, n, "specificdescription");
        ctx.getReport().addRuleViolation(rv);
        assertTrue(ctx.getReport().isEmpty());
    }

    public void testEquals1() {
        MyRule r = new MyRule();
        assertFalse("A rule is never equals to null!", r.equals(null));
    }

    public void testEquals2() {
        MyRule r = new MyRule();
        assertEquals("A rule must be equals to itself", r, r);
    }

    public void testEquals3() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        assertEquals("2 instances of the same rule are equals", r1, r2);
        assertEquals("hasCode for 2 instances of the same rule must be equals", r1.hashCode(), r2.hashCode());
    }

    public void testEquals4() {
        MyRule myRule = new MyRule();
        assertFalse("A rule cannot be equals to an object of another class", myRule.equals("MyRule"));
    }

    public void testEquals5() {
        MyRule myRule = new MyRule();
        MyOtherRule myOtherRule = new MyOtherRule();
        assertFalse("2 rules of different classes cannot be equals", myRule.equals(myOtherRule));
        assertFalse("Rules that are not equals should not have the same hashcode", myRule.hashCode() == myOtherRule.hashCode());
    }

    public void testEquals6() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setName("MyRule2");
        assertFalse("Rules with different names cannot be equals", r1.equals(r2));
        assertFalse("Rules that are not equals should not have the same hashcode", r1.hashCode() == r2.hashCode());
    }

    public void testEquals7() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setPriority(1);
        assertFalse("Rules with different priority cannot be equals", r1.equals(r2));
        assertFalse("Rules that are not equals should not have the same hashcode", r1.hashCode() == r2.hashCode());
    }

    public void testEquals8() {
        MyRule r1 = new MyRule();
        r1.setProperty(MyRule.xpath, "something");
        MyRule r2 = new MyRule();
        r2.setProperty(MyRule.xpath, "something else");
        assertFalse("Rules with different properties values cannot be equals", r1.equals(r2));
        assertFalse("Rules that are not equals should not have the same hashcode", r1.hashCode() == r2.hashCode());
    }

    public void testEquals9() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setProperty(MyRule.xpath, "something else");
        assertFalse("Rules with different properties cannot be equals", r1.equals(r2));
        assertFalse("Rules that are not equals should not have the same hashcode", r1.hashCode() == r2.hashCode());
    }

    public void testEquals10() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setMessage("another message");
        assertTrue("Rules with different message are still equals", r1.equals(r2));
        assertTrue("Rules that are equals must have the same hashcode", r1.hashCode() == r2.hashCode());
    }

}
