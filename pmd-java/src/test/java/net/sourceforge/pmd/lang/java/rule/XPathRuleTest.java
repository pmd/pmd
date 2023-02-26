/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.lang.rule.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.internal.SaxonXPathRuleQuery;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * @author daniels
 */
class XPathRuleTest {

    private XPathRule makeXPath(String expression) {
        return JavaParsingHelper.DEFAULT.newXpathRule(expression);
    }

    @Test
    void testPluginname() {
        XPathRule rule = makeXPath("//VariableDeclaratorId[string-length(@Name) < 3]");
        rule.setMessage("{0}");
        Report report = getReportForTestString(rule, TEST1);
        RuleViolation rv = report.getViolations().get(0);
        assertEquals("a", rv.getDescription());
    }


    @Test
    void testXPathMultiProperty() throws Exception {
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
    void testVariables() throws Exception {
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
    void testFnPrefixOnSaxon() throws Exception {
        XPathRule rule = makeXPath("//VariableDeclaratorId[fn:matches(@Name, 'fiddle')]");
        Report report = getReportForTestString(rule, TEST2);
        RuleViolation rv = report.getViolations().get(0);
        assertEquals(3, rv.getBeginLine());
    }

    @Test
    void testNoFnPrefixOnSaxon() {
        XPathRule rule = makeXPath("//VariableDeclaratorId[matches(@Name, 'fiddle')]");
        Report report = getReportForTestString(rule, TEST2);
        RuleViolation rv = report.getViolations().get(0);
        assertEquals(3, rv.getBeginLine());
    }

    @Test
    void testSimpleQueryIsRuleChain() {
        // ((/)/descendant::element(Q{}VariableDeclaratorId))[matches(convertUntyped(data(@Name)), "fiddle", "")]
        assertIsRuleChain("//VariableDeclaratorId[matches(@Name, 'fiddle')]");
    }

    @Test
    void testSimpleQueryIsRuleChain2() {
        // docOrder(((/)/descendant-or-self::node())/(child::element(ClassOrInterfaceType)[typeIs("java.util.Vector")]))
        assertIsRuleChain("//ClassOrInterfaceType[pmd-java:typeIs('java.util.Vector')]");
    }

    private void assertIsRuleChain(String xpath) {
        XPathRule rule = makeXPath(xpath);
        try (LanguageProcessor proc = JavaParsingHelper.DEFAULT.newProcessor()) {
            rule.initialize(proc);
            assertTrue(rule.getTargetSelector().isRuleChain(), "Not recognized as a rulechain query: " + xpath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Following sibling check: See https://sourceforge.net/p/pmd/bugs/1209/
     *
     * @throws Exception any error
     */
    @Test
    void testFollowingSibling() throws Exception {
        final String source = "public interface dummy extends Foo, Bar, Baz {}";
        ASTCompilationUnit cu = JavaParsingHelper.DEFAULT.parse(source);

        String xpath = "//ExtendsList/ClassOrInterfaceType/following-sibling::ClassOrInterfaceType";


        SaxonXPathRuleQuery xpathRuleQuery = new SaxonXPathRuleQuery(xpath,
                                                                     XPathVersion.DEFAULT,
                                                                     new HashMap<>(),
                                                                     XPathHandler.noFunctionDefinitions(),
                                                                     DeprecatedAttrLogger.noop());
        List<Node> nodes = xpathRuleQuery.evaluate(cu);
        assertEquals(2, nodes.size());
        assertEquals("Bar", ((JavaNode) nodes.get(0)).getText().toString());
        assertEquals("Baz", ((JavaNode) nodes.get(1)).getText().toString());
    }

    private static Report getReportForTestString(Rule r, String test) {
        return JavaParsingHelper.DEFAULT.executeRule(r, test);
    }


    private static final String TEST1 = "public class Foo {" + PMD.EOL + " int a;" + PMD.EOL + "}";

    private static final String TEST2 = "public class Foo {" + PMD.EOL + " int faddle;" + PMD.EOL + " int fiddle;"
            + PMD.EOL + "}";


    private static final String TEST3 = "public class Foo {" + PMD.EOL + " int forbid1; int forbid2; int forbid1$forbid2;" + PMD.EOL + "}";

}
