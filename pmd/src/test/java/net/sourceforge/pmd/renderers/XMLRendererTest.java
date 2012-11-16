/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.testframework.RuleTst;

import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XMLRendererTest extends RuleTst {

    private static class FooRule extends AbstractJavaRule {
        public Object visit(ASTClassOrInterfaceDeclaration c, Object ctx) {
            if (c.getImage().equals("Foo")) addViolation(ctx, c);
            return ctx;
        }

        public String getMessage() {
            return "blah";
        }

        public String getName() {
            return "Foo";
        }

        public String getRuleSetName() {
            return "RuleSet";
        }

        public String getDescription() {
            return "desc";
        }
    }

    @Test
    public void testEmptyReport() throws Throwable {
        Element root = parseRootElement(new Report());
        assertEquals("pmd", root.getNodeName());
        assertNull(root.getFirstChild().getNextSibling()); // only one child, it's whitespace
    }

    @Test
    public void testErrorReport() throws Throwable {
        Report report = new Report();
        report.addError(new Report.ProcessingError("test_msg", "test_filename"));
        Element root = parseRootElement(report);
        assertEquals("test_msg", root.getFirstChild().getNextSibling().getAttributes().getNamedItem("msg").getNodeValue());
    }

    @Test
    public void testSingleReport() throws Throwable {
        Report report = new Report();
        runTestFromString(TEST1, new FooRule(), report);
        Element root = parseRootElement(report);
        assertEquals("n/a", root.getFirstChild().getNextSibling().getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("Foo", root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getAttributes().getNamedItem("rule").getNodeValue());
        assertEquals("RuleSet", root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getAttributes().getNamedItem("ruleset").getNodeValue());
        assertEquals("1", root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getAttributes().getNamedItem("beginline").getNodeValue());
    }

    private static final String TEST1 =
            "public class Foo {}" + PMD.EOL;

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public class Foo {}" + PMD.EOL +
            "}" + PMD.EOL;


    @Test
    public void testDoubleReport() throws Throwable {
        Report report = new Report();
        runTestFromString(TEST2, new FooRule(), report);
        runTestFromString(TEST2, new FooRule(), report);
        Element root = parseRootElement(report);
        assertEquals("Foo", root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getAttributes().getNamedItem("rule").getNodeValue());
        assertEquals("Foo", root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getAttributes().getNamedItem("rule").getNodeValue());
    }

    @Test
    public void testTwoFiles() throws Throwable {
        Report report = new Report();
        FooRule rule = new FooRule();
        runTestFromString(TEST2, rule, report);
        PMD p = new PMD();
        p.getConfiguration().setDefaultLanguageVersion(LanguageVersion.JAVA_14);
        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        ctx.setSourceCodeFilename("bar");
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        p.getSourceCodeProcessor().processSourceCode(new StringReader(TEST2), new RuleSets(rules), ctx);
        Element root = parseRootElement(report);
        assertEquals("bar", root.getFirstChild().getNextSibling().getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("n/a", root.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getAttributes().getNamedItem("name").getNodeValue());
    }

    private Element parseRootElement(Report rpt) throws SAXException, IOException, ParserConfigurationException {
	String result = ReportTest.render(new XMLRenderer(), rpt);
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(result))).getDocumentElement();
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(XMLRendererTest.class);
    }
}
