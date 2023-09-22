/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Rule;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
class XPathMetricFunctionTest extends BaseXPathFunctionTest {

    @Test
    void testWellFormedClassMetricRule() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[pmd-java:metric('NCSS') > 0]");
        String code = "class Foo { Foo() {} void bar() {}}";

        assertFinds(rule, 1, code);
    }


    @Test
    void testWellFormedOperationMetricRule() {
        Rule rule = makeXpathRuleFromXPath("//ConstructorDeclaration[pmd-java:metric('CYCLO') > 1]");
        String code = "class Goo { Goo() {if(true){}} }";

        assertFinds(rule, 1, code);
    }


    @Test
    void testBadCase() {
        Rule rule = makeXpathRuleFromXPath("//ConstructorDeclaration[pmd-java:metric('cYclo') > 1]");
        String code = "class Hoo { Hoo() {if(true){}} }";

        assertFinds(rule, 1, code);
    }


    @Test
    void testNonexistentMetric() {
        testWithExpectedException(
            "//ConstructorDeclaration[pmd-java:metric('FOOBAR') > 1]",
            "class Joo { Joo() {if(true){}} }",
            e -> assertThat(e.getMessage(), containsString(MetricFunction.badMetricKeyMessage("FOOBAR"))));
    }


    @Test
    void testIfStmt() {
        Rule rule = makeXpathRuleFromXPath("//IfStatement[pmd-java:metric('NCSS') = 1]");
        String code = "class Hoo { Hoo() {if(true){}} }";

        assertFinds(rule, 1, code);
    }


    @Test
    void testWrongNodeTypeMeansEmptySequence() {
        Rule rule = makeXpathRuleFromXPath("//EnumDeclaration[not(pmd-java:metric('NPATH'))]");
        String code = "enum Loo { FOO; }";

        assertFinds(rule, 1, code);
    }

}
