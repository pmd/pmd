/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.sourceforge.pmd.Rule;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XPathMetricFunctionTest extends BaseXPathFunctionTest {

    @Test
    public void testWellFormedClassMetricRule() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[pmd-java:metric('NCSS') > 0]");
        String code = "class Foo { Foo() {} void bar() {}}";

        assertFinds(rule, 1, code);
    }


    @Test
    public void testWellFormedOperationMetricRule() {
        Rule rule = makeXpathRuleFromXPath("//ConstructorDeclaration[pmd-java:metric('CYCLO') > 1]");
        String code = "class Goo { Goo() {if(true){}} }";

        assertFinds(rule, 1, code);
    }


    @Test
    public void testBadCase() {
        Rule rule = makeXpathRuleFromXPath("//ConstructorDeclaration[pmd-java:metric('cYclo') > 1]");
        String code = "class Hoo { Hoo() {if(true){}} }";

        assertFinds(rule, 1, code);
    }


    @Test
    public void testNonexistentMetric() {
        testWithExpectedException(
            "//ConstructorDeclaration[pmd-java:metric('FOOBAR') > 1]",
            "class Joo { Joo() {if(true){}} }",
            e -> assertThat(e.getMessage(), containsString(MetricFunction.badMetricKeyMessage("FOOBAR"))));
    }


    @Test
    public void testWrongNodeTypeGeneric() {
        testWithExpectedException(
            "//IfStatement[pmd-java:metric('NCSS') > 1]",
            "class Koo { Koo() {if(true){}} }",
            e -> assertThat(e.getMessage(), containsString(MetricFunction.genericBadNodeMessage())));
    }


    @Test
    public void testWrongMetricKeyForTypeDeclaration() {
        testWithExpectedException(
            "//EnumDeclaration[pmd-java:metric('CYCLO') > 1]",
            "enum Loo { FOO; }",
            e -> assertThat(e.getMessage(), containsString(MetricFunction.badMetricKeyMessage("CYCLO"))));
    }


    @Test
    public void testWrongMetricKeyForOperationDeclaration() {
        testWithExpectedException(
            "//MethodDeclaration[pmd-java:metric('WMC') > 1]",
            "class Moo { void foo() {if(true){}} }",
            e -> assertThat(e.getMessage(), containsString(MetricFunction.badMetricKeyMessage("WMC"))));

    }


}
