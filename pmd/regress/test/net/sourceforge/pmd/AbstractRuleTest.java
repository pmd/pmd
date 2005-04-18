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
import net.sourceforge.pmd.IPositionProvider;

import java.util.HashSet;
import java.util.Set;

public class AbstractRuleTest extends TestCase {

    private static class MyPosProv implements IPositionProvider {
        private int begline, endline, begcol,endcol;
        public MyPosProv(int beg, int end, int begcol, int endcol) {
            this.begline = beg;
            this.endline = end;
            this.begcol = begcol;
            this.endcol = endcol;
        }
        public int getBeginLine() {
            return begline;
        }
        public int getEndLine() {
            return endline;
        }
        public int getBeginColumn() {
            return begcol;
        }
        public int getEndColumn() {
            return endcol;
        }
    }
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
        RuleViolation rv = r.createRuleViolation(ctx, new MyPosProv(5,5,5,5));
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
        RuleViolation rv = r.createRuleViolation(ctx, new MyPosProv(5,5,5,5), "specificdescription");
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
        r.createRuleViolation(ctx, new MyPosProv(5,5,5,5), "specificdescription");
        assertTrue(ctx.getReport().isEmpty());
    }

}
