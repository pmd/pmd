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
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.XMLRenderer;
import test.net.sourceforge.pmd.MockRule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLRendererTest extends TestCase {
    private XMLRenderer IUT = null;
    private MockRule RULE1 = new MockRule("RULE1", "RULE1", "msg");
    private MockRule RULE2 = new MockRule("RULE2", "RULE2", "msg");
    private RuleContext ctx = new RuleContext();

    public XMLRendererTest(String name) {
        super(name);
    }

    public void setUp() {
        IUT = new XMLRenderer();
    }

    public void testEmptyReport() throws Throwable {
        String rendered = IUT.render(new Report());
        assertTrue("Expected empty PMD tag.", rendered.indexOf("violation") < 0);
    }

    public void testSingleReport() throws Throwable {
        Report report = new Report();
        ctx.setSourceCodeFilename("testSingleReport");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

        String rendered = IUT.render(report);

        // <?xml version="1.0"?>
        // <pmd>
        //   <file name="testSingleReport">
        //     <violation line="1" rule="RULE1">
        // Rule1
        //     </violation>
        //   </file>
        // </pmd>

        List expectedStrings = new ArrayList();
        expectedStrings.add("<pmd>");
        expectedStrings.add("<file name=\"testSingleReport\">");
        expectedStrings.add("<violation line=\"1\" rule=\"RULE1\">");
        expectedStrings.add("Rule1");
        expectedStrings.add("</violation>");
        expectedStrings.add("</file>");
        expectedStrings.add("</pmd>");

        verifyPositions(rendered, expectedStrings);
    }

    public void testDoubleReport() throws Throwable {
        Report report = new Report();
        ctx.setSourceCodeFilename("testDoubleReport");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

        report.addRuleViolation(new RuleViolation(RULE2, 2, "Rule2", ctx));

        String rendered = IUT.render(report);

        // <?xml version="1.0"?>
        // <pmd>
        //   <file name="testSingleReport">
        //     <violation line="1" rule="RULE1">
        // Rule1
        //     </violation>
        //     <violation line="2" rule="RULE2">
        // Rule2
        //     </violation>
        //   </file>
        // </pmd>

        List expectedStrings = new ArrayList();
        expectedStrings.add("<pmd>");
        expectedStrings.add("<file name=\"testDoubleReport\">");
        expectedStrings.add("<violation line=\"1\" rule=\"RULE1\">");
        expectedStrings.add("Rule1");
        expectedStrings.add("</violation>");
        expectedStrings.add("<violation line=\"2\" rule=\"RULE2\">");
        expectedStrings.add("Rule2");
        expectedStrings.add("</violation>");
        expectedStrings.add("</file>");
        expectedStrings.add("</pmd>");

        verifyPositions(rendered, expectedStrings);
    }

    public void testTwoFiles() throws Throwable {
        Report report = new Report();
        ctx.setSourceCodeFilename("testTwoFiles_0");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

        ctx.setSourceCodeFilename("testTwoFiles_1");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

        String rendered = IUT.render(report);

        // <?xml version="1.0"?>
        // <pmd>
        //   <file name="testTwoFiles_0">
        //     <violation line="1" rule="RULE1">
        // Rule1
        //     </violation>
        //   </file>
        //   <file name="testTwoFiles_1">
        //     <violation line="1" rule="RULE1">
        // Rule1
        //     </violation>
        //   </file>
        // </pmd>

        List expectedStrings = new ArrayList();
        expectedStrings.add("<pmd>");
        expectedStrings.add("<file name=\"testTwoFiles_0\">");
        expectedStrings.add("<violation line=\"1\" rule=\"RULE1\">");
        expectedStrings.add("Rule1");
        expectedStrings.add("</violation>");
        expectedStrings.add("</file>");
        expectedStrings.add("<file name=\"testTwoFiles_1\">");
        expectedStrings.add("<violation line=\"1\" rule=\"RULE1\">");
        expectedStrings.add("Rule1");
        expectedStrings.add("</violation>");
        expectedStrings.add("</file>");
        expectedStrings.add("</pmd>");

        verifyPositions(rendered, expectedStrings);
    }

    public void testUnorderedFiles() throws Throwable {
        Report report = new Report();
        ctx.setSourceCodeFilename("testTwoFiles_0");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

        ctx.setSourceCodeFilename("testTwoFiles_1");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

        ctx.setSourceCodeFilename("testTwoFiles_0");
        report.addRuleViolation(new RuleViolation(RULE2, 2, "Rule2", ctx));

        String rendered = IUT.render(report);

        // <?xml version="1.0"?>
        // <pmd>
        //   <file name="testTwoFiles_0">
        //     <violation line="1" rule="RULE1">
        // Rule1
        //     </violation>
        //   </file>
        //   <file name="testTwoFiles_1">
        //     <violation line="1" rule="RULE1">
        // Rule1
        //     </violation>
        //   </file>
        // </pmd>

        List expectedStrings = new ArrayList();
        expectedStrings.add("<pmd>");
        expectedStrings.add("<file name=\"testTwoFiles_0\">");
        expectedStrings.add("<violation line=\"1\" rule=\"RULE1\">");
        expectedStrings.add("Rule1");
        expectedStrings.add("</violation>");
        expectedStrings.add("<violation line=\"2\" rule=\"RULE2\">");
        expectedStrings.add("Rule2");
        expectedStrings.add("</violation>");
        expectedStrings.add("</file>");
        expectedStrings.add("<file name=\"testTwoFiles_1\">");
        expectedStrings.add("<violation line=\"1\" rule=\"RULE1\">");
        expectedStrings.add("Rule1");
        expectedStrings.add("</violation>");
        expectedStrings.add("</file>");
        expectedStrings.add("</pmd>");

        verifyPositions(rendered, expectedStrings);
    }

    public void verifyPositions(String rendered, List strings) {
        Iterator i = strings.iterator();
        int currPos = 0;
        String lastString = "<?xml version=\"1.0\"?>";

        while (i.hasNext()) {
            String str = (String) i.next();

            int strPos = rendered.indexOf(str, currPos);
            assertTrue("Expecting: " + str + " after " + lastString, strPos > currPos);
            currPos = strPos;
            lastString = str;
        }
    }
}
