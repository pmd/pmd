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
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import test.net.sourceforge.pmd.testframework.MockRule;

public class RuleViolationTest extends TestCase {

    public void testConstructor1() {
        Rule rule = new MockRule("name", "desc", "msg");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        RuleViolation r = new RuleViolation(rule, 2, ctx);
        assertEquals("object mismatch", rule, r.getRule());
        assertEquals("line number is wrong", 2, r.getLine());
        assertEquals("filename is wrong", "filename", r.getFilename());
    }

    public void testConstructor2() {
        Rule rule = new MockRule("name", "desc", "msg");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        RuleViolation r = new RuleViolation(rule, 2, "description", ctx);
        assertEquals("object mismatch", rule, r.getRule());
        assertEquals("line number is wrong", 2, r.getLine());
        assertEquals("filename is wrong", "filename", r.getFilename());
        assertEquals("description is wrong", "description", r.getDescription());
    }

    public void testComparatorWithDifferentFilenames() {
        Rule rule = new MockRule("name", "desc", "msg");
        RuleViolation.RuleViolationComparator comp = new RuleViolation.RuleViolationComparator();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename1");
        RuleViolation r1 = new RuleViolation(rule, 10, "description", ctx);
        ctx.setSourceCodeFilename("filename2");
        RuleViolation r2 = new RuleViolation(rule, 20, "description", ctx);
        assertEquals(-1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }

    public void testComparatorWithSameFileDifferentLines() {
        Rule rule = new MockRule("name", "desc", "msg");
        RuleViolation.RuleViolationComparator comp = new RuleViolation.RuleViolationComparator();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        RuleViolation r1 = new RuleViolation(rule, 10, "description", ctx);
        RuleViolation r2 = new RuleViolation(rule, 20, "description", ctx);
        assertTrue(comp.compare(r1, r2) < 0);
        assertTrue(comp.compare(r2, r1) > 0);
    }

    public void testComparatorWithSameFileSameLines() {
        Rule rule = new MockRule("name", "desc", "msg");
        RuleViolation.RuleViolationComparator comp = new RuleViolation.RuleViolationComparator();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        RuleViolation r1 = new RuleViolation(rule, 10, "description", ctx);
        RuleViolation r2 = new RuleViolation(rule, 10, "description", ctx);
        assertEquals(0, comp.compare(r1, r2));
        assertEquals(0, comp.compare(r2, r1));
    }
}
