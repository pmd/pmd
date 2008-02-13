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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.MockRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.ast.SimpleNode;

import org.junit.Ignore;
import org.junit.Test;

public class RuleViolationTest {

    @Ignore
    @Test
    public void testConstructor1() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginLine(2);
        RuleViolation r = new RuleViolation(rule, ctx, s);
        assertEquals("object mismatch", rule, r.getRule());
        assertEquals("line number is wrong", 2, r.getBeginLine());
        assertEquals("filename is wrong", "filename", r.getFilename());
    }

    @Ignore
    @Test
    public void testConstructor2() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginLine(2);
        RuleViolation r = new RuleViolation(rule, ctx, s, "description");
        assertEquals("object mismatch", rule, r.getRule());
        assertEquals("line number is wrong", 2, r.getBeginLine());
        assertEquals("filename is wrong", "filename", r.getFilename());
        assertEquals("description is wrong", "description", r.getDescription());
    }

    @Ignore
    @Test
    public void testComparatorWithDifferentFilenames() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleViolation.RuleViolationComparator comp = new RuleViolation.RuleViolationComparator();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename1");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginLine(10);
        RuleViolation r1 = new RuleViolation(rule, ctx, s, "description");
        ctx.setSourceCodeFilename("filename2");
        SimpleNode s1 = new SimpleJavaNode(1);
        s1.testingOnly__setBeginLine(10);
        RuleViolation r2 = new RuleViolation(rule, ctx, s1, "description");
        assertEquals(-1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }

    @Ignore
    @Test
    public void testComparatorWithSameFileDifferentLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleViolation.RuleViolationComparator comp = new RuleViolation.RuleViolationComparator();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginLine(10);
        SimpleNode s1 = new SimpleJavaNode(1);
        s1.testingOnly__setBeginLine(20);
        RuleViolation r1 = new RuleViolation(rule, ctx, s, "description");
        RuleViolation r2 = new RuleViolation(rule, ctx, s1, "description");
        assertTrue(comp.compare(r1, r2) < 0);
        assertTrue(comp.compare(r2, r1) > 0);
    }

    @Ignore
    @Test
    public void testComparatorWithSameFileSameLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleViolation.RuleViolationComparator comp = new RuleViolation.RuleViolationComparator();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginLine(10);
        SimpleNode s1 = new SimpleJavaNode(1);
        s1.testingOnly__setBeginLine(10);
        RuleViolation r1 = new RuleViolation(rule, ctx, s, "description");
        RuleViolation r2 = new RuleViolation(rule, ctx, s1, "description");
        assertEquals(1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(RuleViolationTest.class);
    }
}
