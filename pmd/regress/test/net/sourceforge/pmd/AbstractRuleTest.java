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
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.SourceFileScope;

import java.util.HashSet;
import java.util.Set;

public class AbstractRuleTest extends TestCase {

    private static class MyRule extends AbstractRule{
        public String getMessage() {
            return "myrule";
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
        SimpleNode s = new SimpleNode(1);
        s.testingOnly__setBeginColumn(5);
        s.testingOnly__setBeginLine(5);
        s.setScope(new SourceFileScope("foo"));
        RuleViolation rv = r.createRuleViolation(ctx, s);
        assertEquals("Line number mismatch!", 5, rv.getLine());
        assertEquals("Filename mismatch!", "filename", rv.getFilename());
        assertEquals("Rule object mismatch!", r, rv.getRule());
        assertEquals("Rule description mismatch!", "myrule", rv.getDescription());
        assertEquals("RuleSet name mismatch!", "foo", rv.getRule().getRuleSetName());
    }

    public void testCreateRV2() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        SimpleNode s = new SimpleNode(1);
        s.testingOnly__setBeginColumn(5);
        s.testingOnly__setBeginLine(5);
        s.setScope(new SourceFileScope("foo"));
        RuleViolation rv = r.createRuleViolation(ctx, s, "specificdescription");
        assertEquals("Line number mismatch!", 5, rv.getLine());
        assertEquals("Filename mismatch!", "filename", rv.getFilename());
        assertEquals("Rule object mismatch!", r, rv.getRule());
        assertEquals("Rule description mismatch!", "specificdescription", rv.getDescription());
    }

    public void testRuleExclusion() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        Set s = new HashSet();
        s.add(new Integer(5));
        ctx.setReport(new Report());
        ctx.excludeLines(s);
        ctx.setSourceCodeFilename("filename");
        SimpleNode n = new SimpleNode(1);
        n.testingOnly__setBeginColumn(5);
        n.testingOnly__setBeginLine(5);
        n.setScope(new SourceFileScope("foo"));
        r.createRuleViolation(ctx, n, "specificdescription");
        assertTrue(ctx.getReport().isEmpty());
    }

}
