/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.xpath;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringReader;
import java.util.Iterator;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.xpath.MetricFunction;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XPathMetricFunctionTest {

    // TODO 7.0 when removing jaxen these tests need to be updated to use pmd-java:metric

    private static final String VIOLATION_MESSAGE = "violation";

    @org.junit.Rule
    public ExpectedException expected = ExpectedException.none();


    private Rule makeXpathRuleFromXPath(String xpath) {
        XPathRule rule = new XPathRule(XPathVersion.XPATH_1_0, xpath);
        rule.setMessage(VIOLATION_MESSAGE);
        rule.setLanguage(LanguageRegistry.getLanguage(JavaLanguageModule.NAME));
        return rule;
    }


    private Iterator<RuleViolation> getViolations(Rule rule, String code) throws PMDException {
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        Report report = new Report();
        ctx.setReport(report);
        ctx.setSourceCodeFile(new File("n/a"));
        ctx.setIgnoreExceptions(false); // for test, we want immediate exceptions thrown and not collect them
        RuleSet rules = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(rule);
        p.getSourceCodeProcessor().processSourceCode(new StringReader(code), new RuleSets(rules), ctx);
        return report.iterator();
    }


    @Test
    public void testWellFormedClassMetricRule() throws PMDException {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[metric('NCSS') > 0]");
        final String code = "class Foo { Foo() {} void bar() {}}";

        Iterator<RuleViolation> violations = getViolations(rule, code);
        assertTrue(violations.hasNext());
    }


    @Test
    public void testWellFormedOperationMetricRule() throws PMDException {
        Rule rule = makeXpathRuleFromXPath("//ConstructorDeclaration[metric('CYCLO') > 1]");
        final String code = "class Goo { Goo() {if(true){}} }";

        Iterator<RuleViolation> violations = getViolations(rule, code);
        assertTrue(violations.hasNext());
    }


    @Test
    public void testBadCase() throws PMDException {
        Rule rule = makeXpathRuleFromXPath("//ConstructorDeclaration[metric('cYclo') > 1]");
        final String code = "class Hoo { Hoo() {if(true){}} }";

        Iterator<RuleViolation> violations = getViolations(rule, code);
        assertTrue(violations.hasNext());
    }


    @Test
    public void testNonexistentMetric() throws Exception {
        testWithExpectedException("//ConstructorDeclaration[metric('FOOBAR') > 1]",
                                  "class Joo { Joo() {if(true){}} }",
                                  IllegalArgumentException.class,
                                  MetricFunction.badOperationMetricKeyMessage());
    }


    @Test
    public void testWrongNodeTypeGeneric() throws Exception {
        testWithExpectedException("//IfStatement[metric('NCSS') > 1]",
                                  "class Koo { Koo() {if(true){}} }",
                                  IllegalStateException.class,
                                  MetricFunction.genericBadNodeMessage());
    }


    @Test
    public void testWrongMetricKeyForTypeDeclaration() throws Exception {
        testWithExpectedException("//EnumDeclaration[metric('CYCLO') > 1]",
                                  "enum Loo { FOO; }",
                                  IllegalArgumentException.class,
                                  MetricFunction.badClassMetricKeyMessage());
    }


    @Test
    public void testWrongMetricKeyForOperationDeclaration() throws Exception {
        testWithExpectedException("//MethodDeclaration[metric('WMC') > 1]",
                                  "class Moo { void foo() {if(true){}} }",
                                  IllegalArgumentException.class,
                                  MetricFunction.badOperationMetricKeyMessage());
    }


    private void testWithExpectedException(String xpath, String code,
                                           Class<? extends Exception> expectedThrowable,
                                           String expectedMessage) throws Exception {

        Rule rule = makeXpathRuleFromXPath(xpath);

        expected.expect(expectedThrowable);
        expected.expectMessage(expectedMessage);

        try {
            getViolations(rule, code);
        } catch (PMDException pmdE) {
            throw (Exception) pmdE.getCause();
        }

    }


}
