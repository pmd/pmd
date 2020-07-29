/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.lang.rule.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.internal.SaxonXPathRuleQuery;
import net.sourceforge.pmd.processor.FileAnalysisListener;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.testframework.RuleTst;

/**
 * @author daniels
 */
public class XPathRuleTest extends RuleTst {

    private XPathRule makeXPath(String expression) {
        XPathRule rule = new XPathRule(XPathVersion.XPATH_2_0, expression);
        rule.setLanguage(LanguageRegistry.getLanguage(JavaLanguageModule.NAME));
        rule.setMessage("XPath Rule Failed");
        return rule;
    }

    @Test
    public void testPluginname() throws Exception {
        XPathRule rule = makeXPath("//VariableDeclaratorId[string-length(@Name) < 3]");
        rule.setMessage("{0}");
        Report report = getReportForTestString(rule, TEST1);
        RuleViolation rv = report.getViolations().get(0);
        assertEquals("a", rv.getDescription());
    }


    @Test
    public void testXPathMultiProperty() throws Exception {
        XPathRule rule = makeXPath("//VariableDeclaratorId[@Name=$forbiddenNames]");
        rule.setMessage("Avoid vars");
        PropertyDescriptor<List<String>> varDescriptor
            = PropertyFactory.stringListProperty("forbiddenNames")
                             .desc("Forbidden names")
                             .defaultValues("forbid1", "forbid2")
                             .delim('$')
                             .build();

        rule.definePropertyDescriptor(varDescriptor);

        Report report = getReportForTestString(rule, TEST3);
        assertEquals(2, report.getViolations().size());
    }


    @Test
    public void testVariables() throws Exception {
        XPathRule rule = makeXPath("//VariableDeclaratorId[@Name=$var]");
        rule.setMessage("Avoid vars");
        PropertyDescriptor<String> varDescriptor =
            PropertyFactory.stringProperty("var").desc("Test var").defaultValue("").build();
        rule.definePropertyDescriptor(varDescriptor);
        rule.setProperty(varDescriptor, "fiddle");
        Report report = getReportForTestString(rule, TEST2);
        RuleViolation rv = report.getViolations().get(0);
        assertEquals(3, rv.getBeginLine());
    }

    @Test
    public void testFnPrefixOnSaxon() throws Exception {
        XPathRule rule = makeXPath("//VariableDeclaratorId[fn:matches(@Name, 'fiddle')]");
        Report report = getReportForTestString(rule, TEST2);
        RuleViolation rv = report.getViolations().get(0);
        assertEquals(3, rv.getBeginLine());
    }

    @Test
    public void testNoFnPrefixOnSaxon() throws Exception {
        XPathRule rule = makeXPath("//VariableDeclaratorId[matches(@Name, 'fiddle')]");
        Report report = getReportForTestString(rule, TEST2);
        RuleViolation rv = report.getViolations().get(0);
        assertEquals(3, rv.getBeginLine());
    }


    /**
     * Test for problem reported in bug #1219 PrimarySuffix/@Image does not work
     * in some cases in xpath 2.0
     *
     * @throws Exception
     *             any error
     */
    @Test
    public void testImageOfPrimarySuffix() throws Exception {
        final String SUFFIX = "import java.io.File;\n" + "\n" + "public class TestSuffix {\n"
            + "    public static void main(String args[]) {\n" + "        new File(\"subdirectory\").list();\n"
            + "    }\n" + "}";
        ASTCompilationUnit cu = JavaParsingHelper.WITH_PROCESSING.parse(SUFFIX);
        try (RuleContext ruleContext = new RuleContext(FileAnalysisListener.noop())) {

            String xpath = "//PrimarySuffix[@Image='list']";

            SaxonXPathRuleQuery xpathRuleQuery = new SaxonXPathRuleQuery(xpath,
                                                                         XPathVersion.DEFAULT,
                                                                         new HashMap<>(),
                                                                         XPathHandler.noFunctionDefinitions(),
                                                                         DeprecatedAttrLogger.noop());
            List<Node> nodes = xpathRuleQuery.evaluate(cu);
            assertEquals(1, nodes.size());
        }
    }

    /**
     * Following sibling check: See https://sourceforge.net/p/pmd/bugs/1209/
     *
     * @throws Exception any error
     */
    @Test
    public void testFollowingSibling() throws Exception {
        final String source = "public class dummy {\n"
            + "  public String toString() {\n"
            + "    String test = \"bad example\";\n"
            + "    test = \"a\";\n"
            + "    return test;\n"
            + "  }\n"
            + "}";
        ASTCompilationUnit cu = JavaParsingHelper.WITH_PROCESSING.parse(source);
        try (RuleContext ruleContext = new RuleContext(FileAnalysisListener.noop())) {

            String xpath = "//Block/BlockStatement/following-sibling::BlockStatement";


            SaxonXPathRuleQuery xpathRuleQuery = new SaxonXPathRuleQuery(xpath,
                                                                         XPathVersion.DEFAULT,
                                                                         new HashMap<>(),
                                                                         XPathHandler.noFunctionDefinitions(),
                                                                         DeprecatedAttrLogger.noop());
            List<Node> nodes = xpathRuleQuery.evaluate(cu);
            assertEquals(2, nodes.size());
            assertEquals(4, nodes.get(0).getBeginLine());
            assertEquals(5, nodes.get(1).getBeginLine());
        }
    }

    private static Report getReportForTestString(Rule r, String test) throws PMDException {
        return JavaParsingHelper.WITH_PROCESSING.executeRule(r, test);
    }


    private static final String TEST1 = "public class Foo {" + PMD.EOL + " int a;" + PMD.EOL + "}";

    private static final String TEST2 = "public class Foo {" + PMD.EOL + " int faddle;" + PMD.EOL + " int fiddle;"
            + PMD.EOL + "}";


    private static final String TEST3 = "public class Foo {" + PMD.EOL + " int forbid1; int forbid2; int forbid1$forbid2;" + PMD.EOL + "}";

}
