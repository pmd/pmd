/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XPathMetricFunctionTest {

    private static final String VIOLATION_MESSAGE = "violation";

    @org.junit.Rule
    public ExpectedException expected = ExpectedException.none();


    private Rule makeXpathRuleFromXPath(String xpath) {
        XPathRule rule = new XPathRule(XPathVersion.XPATH_1_0, xpath);
        rule.setMessage(VIOLATION_MESSAGE);
        rule.setLanguage(LanguageRegistry.getLanguage(JavaLanguageModule.NAME));
        return rule;
    }


    private Iterator<RuleViolation> getViolations(Rule rule, String code) throws Exception {
        return JavaParsingHelper.WITH_PROCESSING.executeRule(rule, code).getViolations().iterator();
    }


    @Test
    public void testWellFormedClassMetricRule() throws Exception {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[pmd-java:metric('NCSS') > 0]");
        final String code = "class Foo { Foo() {} void bar() {}}";

        Iterator<RuleViolation> violations = getViolations(rule, code);
        assertTrue(violations.hasNext());
    }


    @Test
    public void testWellFormedOperationMetricRule() throws Exception {
        Rule rule = makeXpathRuleFromXPath("//ConstructorDeclaration[pmd-java:metric('CYCLO') > 1]");
        final String code = "class Goo { Goo() {if(true){}} }";

        Iterator<RuleViolation> violations = getViolations(rule, code);
        assertTrue(violations.hasNext());
    }


    @Test
    public void testBadCase() throws Exception {
        Rule rule = makeXpathRuleFromXPath("//ConstructorDeclaration[pmd-java:metric('cYclo') > 1]");
        final String code = "class Hoo { Hoo() {if(true){}} }";

        Iterator<RuleViolation> violations = getViolations(rule, code);
        assertTrue(violations.hasNext());
    }


    @Test
    public void testNonexistentMetric() throws Exception {
        testWithExpectedException("//ConstructorDeclaration[pmd-java:metric('FOOBAR') > 1]",
                                  "class Joo { Joo() {if(true){}} }",
                                  IllegalArgumentException.class,
                                  MetricFunction.badOperationMetricKeyMessage());
    }


    @Test
    public void testWrongNodeTypeGeneric() throws Exception {
        testWithExpectedException("//IfStatement[pmd-java:metric('NCSS') > 1]",
                                  "class Koo { Koo() {if(true){}} }",
                                  IllegalStateException.class,
                                  MetricFunction.genericBadNodeMessage());
    }


    @Test
    public void testWrongMetricKeyForTypeDeclaration() throws Exception {
        testWithExpectedException("//EnumDeclaration[pmd-java:metric('CYCLO') > 1]",
                                  "enum Loo { FOO; }",
                                  IllegalArgumentException.class,
                                  MetricFunction.badClassMetricKeyMessage());
    }


    @Test
    public void testWrongMetricKeyForOperationDeclaration() throws Exception {
        testWithExpectedException("//MethodDeclaration[pmd-java:metric('WMC') > 1]",
                                  "class Moo { void foo() {if(true){}} }",
                                  IllegalArgumentException.class,
                                  MetricFunction.badOperationMetricKeyMessage());
    }


    private void testWithExpectedException(String xpath, String code,
                                           Class<? extends Exception> expectedThrowable,
                                           String expectedMessage) throws Exception {

        Rule rule = makeXpathRuleFromXPath(xpath);

        expected.expect(FileAnalysisException.class);
        expected.expectCause(Matchers.allOf(CoreMatchers.instanceOf(expectedThrowable),
                                            Matchers.hasProperty("message", Matchers.equalTo(expectedMessage))));

        getViolations(rule, code);
    }


}
