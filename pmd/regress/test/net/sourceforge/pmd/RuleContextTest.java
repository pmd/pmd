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
import static org.junit.Assert.assertNull;

import java.io.File;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class RuleContextTest {

    @Test
    public void testReport() {
        RuleContext ctx = new RuleContext();
        assertEquals(0, ctx.getReport().size());
        Report r = new Report();
        ctx.setReport(r);
        Report r2 = ctx.getReport();
        assertEquals("report object mismatch", r, r2);
    }

    @Test
    public void testSourceCodeFilename() {
        RuleContext ctx = new RuleContext();
        assertNull("filename should be null", ctx.getSourceCodeFilename());
        ctx.setSourceCodeFilename("foo");
        assertEquals("filename mismatch", "foo", ctx.getSourceCodeFilename());
    }

    @Test
    public void testSourceCodeFile() {
    	RuleContext ctx = new RuleContext();
    	assertNull("file should be null", ctx.getSourceCodeFile());
    	ctx.setSourceCodeFile(new File("somefile.java"));
    	assertEquals("filename mismatch", new File("somefile.java"), ctx.getSourceCodeFile());
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(RuleContextTest.class);
    }
}
