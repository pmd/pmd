/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.renderers;

import junit.framework.TestCase;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.XMLRenderer;
import test.net.sourceforge.pmd.testframework.MockRule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLRendererTest extends TestCase {

    private MockRule RULE1 = new MockRule("RULE1", "RULE1", "msg");
    private MockRule RULE2 = new MockRule("RULE2", "RULE2", "msg");
    private RuleContext ctx = new RuleContext();

    public XMLRendererTest(String name) {
        super(name);
    }

    public void testEmptyReport() throws Throwable {
        XMLRenderer renderer = new XMLRenderer();
        String rendered = renderer.render(new Report());
        assertTrue("Expected empty PMD tag.", rendered.indexOf("violation") < 0);
    }

    public void testErrorReport() throws Throwable {
        Report r = new Report();
        r.addError(new Report.ProcessingError("test_msg", "test_filename"));
        XMLRenderer renderer = new XMLRenderer();
        assertTrue(renderer.render(r).indexOf("msg=\"test_msg\"/>") != -1);
    }

    public void testSingleReport() throws Throwable {
        Report report = new Report();
        ctx.setSourceCodeFilename("testSingleReport");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

        XMLRenderer renderer = new XMLRenderer();
        String rendered = renderer.render(report);

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

        XMLRenderer renderer = new XMLRenderer();
        verifyPositions(renderer.render(report), expectedStrings);
    }

    public void testTwoFiles() throws Throwable {
        Report report = new Report();
        ctx.setSourceCodeFilename("testTwoFiles_0");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

        ctx.setSourceCodeFilename("testTwoFiles_1");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

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

        XMLRenderer renderer = new XMLRenderer();
        verifyPositions(renderer.render(report), expectedStrings);
    }

    public void testUnorderedFiles() throws Throwable {
        Report report = new Report();
        ctx.setSourceCodeFilename("testTwoFiles_0");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

        ctx.setSourceCodeFilename("testTwoFiles_1");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

        ctx.setSourceCodeFilename("testTwoFiles_0");
        report.addRuleViolation(new RuleViolation(RULE2, 2, "Rule2", ctx));

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

        XMLRenderer renderer = new XMLRenderer();
        verifyPositions(renderer.render(report), expectedStrings);
    }

    /**
     * Verify correct escaping in generated XML.
     */
    public void testEscaping() throws Throwable {
        Report report = new Report();
        ctx.setSourceCodeFilename("testEscaping: Less than: < Greater than: > Ampersand: & Quote: \" 'e' acute: \u00E9");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "[RULE] Less than: < Greater than: > Ampersand: & Quote: \" 'e' acute: \u00E9", ctx));

        // <?xml version="1.0"?>
        // <pmd>
        //   <file name="testEscaping: Less than: &lt; Greater than: &gt; Ampersand: &amp; Quote: &quot; 'e' acute: &#233;">
        //     <violation line="1" rule="RULE1">
        // [RULE] Less than: &lt; Greater than: &gt; Ampersand: &amp; Quote: &quot; 'e' acute: &#233;
        //     </violation>
        //   </file>
        // </pmd>

        List expectedStrings = new ArrayList();
        expectedStrings.add("<pmd>");
        expectedStrings.add("<file name=\"testEscaping: Less than: ");
        expectedStrings.add("&lt;");
        expectedStrings.add(" Greater than: ");
        expectedStrings.add("&gt;");
        expectedStrings.add(" Ampersand: ");
        expectedStrings.add("&amp;");
        expectedStrings.add(" Quote: ");
        expectedStrings.add("&quot;");
        expectedStrings.add(" 'e' acute: ");
        expectedStrings.add("&#233;");
        expectedStrings.add("\">");
        expectedStrings.add("<violation line=\"1\" rule=\"RULE1\">");
        expectedStrings.add("[RULE] Less than: ");
        expectedStrings.add("&lt;");
        expectedStrings.add(" Greater than: ");
        expectedStrings.add("&gt;");
        expectedStrings.add(" Ampersand: ");
        expectedStrings.add("&amp;");
        expectedStrings.add(" Quote: ");
        expectedStrings.add("&quot;");
        expectedStrings.add(" 'e' acute: ");
        expectedStrings.add("&#233;");
        expectedStrings.add("</violation>");
        expectedStrings.add("</file>");
        expectedStrings.add("</pmd>");

        XMLRenderer renderer = new XMLRenderer();
        verifyPositions(renderer.render(report), expectedStrings);
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
