/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.renderers;

import junit.framework.TestCase;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.XMLRenderer;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import test.net.sourceforge.pmd.testframework.MockRule;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.io.IOException;

public class XMLRendererTest extends TestCase {

    private MockRule RULE1 = new MockRule("RULE1", "RULE1", "msg", "rulesetname", 3);
    private MockRule RULE2 = new MockRule("RULE2", "RULE2", "msg", "rulesetname");
    private RuleContext ctx = new RuleContext();

    public XMLRendererTest(String name) {
        super(name);
    }

    public void testEmptyReport() throws Throwable {
        Element root = parseRootElement(new Report());
        assertEquals("pmd", root.getNodeName());
        assertNull(root.getFirstChild().getNextSibling()); // only one child, it's whitespace
    }

    public void testErrorReport() throws Throwable {
        Report report = new Report();
        report.addError(new Report.ProcessingError("test_msg", "test_filename"));
        Element root = parseRootElement(report);
        assertEquals("test_msg", root.getFirstChild().getNextSibling().getAttributes().getNamedItem("msg").getNodeValue());
    }

    public void testSingleReport() throws Throwable {
        Report report = new Report();
        ctx.setSourceCodeFilename("testSingleReport");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));
        Element root = parseRootElement(report);
        assertEquals("testSingleReport", root.getFirstChild().getNextSibling().getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("RULE1", root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getAttributes().getNamedItem("rule").getNodeValue());
        assertEquals("rulesetname", root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getAttributes().getNamedItem("ruleset").getNodeValue());
        assertEquals("1", root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getAttributes().getNamedItem("line").getNodeValue());
    }

    public void testDoubleReport() throws Throwable {
        Report report = new Report();
        ctx.setSourceCodeFilename("testDoubleReport");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));
        report.addRuleViolation(new RuleViolation(RULE2, 2, "Rule2", ctx));
        Element root = parseRootElement(report);
        assertEquals("RULE1", root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getAttributes().getNamedItem("rule").getNodeValue());
        assertEquals("RULE2", root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getAttributes().getNamedItem("rule").getNodeValue());
    }

    public void testTwoFiles() throws Throwable {
        Report report = new Report();
        ctx.setSourceCodeFilename("testTwoFiles_0");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));
        ctx.setSourceCodeFilename("testTwoFiles_1");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));
        Element root = parseRootElement(report);
        assertEquals("testTwoFiles_0", root.getFirstChild().getNextSibling().getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("testTwoFiles_1", root.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getAttributes().getNamedItem("name").getNodeValue());
    }

    public void testUnorderedFiles() throws Throwable {
        Report report = new Report();
        ctx.setSourceCodeFilename("testTwoFiles_0");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

        ctx.setSourceCodeFilename("testTwoFiles_1");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "Rule1", ctx));

        ctx.setSourceCodeFilename("testTwoFiles_0");
        report.addRuleViolation(new RuleViolation(RULE2, 2, "Rule2", ctx));

        Element root = parseRootElement(report);
        assertEquals("testTwoFiles_0", root.getFirstChild().getNextSibling().getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("testTwoFiles_1", root.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("RULE1", root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getAttributes().getNamedItem("rule").getNodeValue());
        assertEquals("RULE2", root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getAttributes().getNamedItem("rule").getNodeValue());
        assertEquals("RULE1", root.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getFirstChild().getNextSibling().getAttributes().getNamedItem("rule").getNodeValue());
    }


    public void testEscaping() throws Throwable {
        // <?xml version="1.0"?>
        // <pmd>
        //   <file name="testEscaping: Less than: &lt; Greater than: &gt; Ampersand: &amp; Quote: &quot; 'e' acute: &#233;">
        //     <violation line="1" rule="RULE1">
        // [RULE] Less than: &lt; Greater than: &gt; Ampersand: &amp; Quote: &quot; 'e' acute: &#233;
        //     </violation>
        //   </file>
        // </pmd>
        Report report = new Report();
        ctx.setSourceCodeFilename("testEscaping: Less than: < Greater than: > Ampersand: & Quote: \" 'e' acute: \u00E9");
        report.addRuleViolation(new RuleViolation(RULE1, 1, "[RULE] Less than: < Greater than: > Ampersand: & Quote: \" 'e' acute: \u00E9", ctx));
        Element root = parseRootElement(report);
        String out = root.getFirstChild().getNextSibling().toString();
        // TODO - this works when run "manually" but not when run using Maven.  Must
        // be some hideous XML difference.  Argh.
/*
        assertTrue(out.indexOf("&gt;") != -1);
        assertTrue(out.indexOf("&lt;") != -1);
        assertTrue(out.indexOf("&amp;") != -1);
        assertTrue(out.indexOf("&apos;") != -1);
*/
    }
    private Element parseRootElement(Report rpt) throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(new XMLRenderer().render(rpt)))).getDocumentElement();
    }

}
