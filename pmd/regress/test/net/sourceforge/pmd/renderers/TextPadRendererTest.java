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
package test.net.sourceforge.pmd.renderers;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.TextPadRenderer;
import test.net.sourceforge.pmd.MockRule;

public class TextPadRendererTest extends TestCase  {

    public void testNullPassedIn() {
        try  {
            (new TextPadRenderer()).render(null);
            fail("Providing a render(null) should throw an npx");
        }  catch(NullPointerException npx)  {
            // cool
        }
    }

    public void testRenderer()  {
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("Foo.java");
        Report rep = new Report();
        rep.addRuleViolation(new RuleViolation(new MockRule("DontImportJavaLang", "Avoid importing anything from the package 'java.lang'", "Avoid importing anything from the package 'java.lang'"), 3,ctx));
        String actual = (new TextPadRenderer()).render(rep);
        String expected = PMD.EOL + "Foo.java(3,  DontImportJavaLang):  Avoid importing anything from the package 'java.lang'" ;
        assertEquals(expected, actual);
    }
}









