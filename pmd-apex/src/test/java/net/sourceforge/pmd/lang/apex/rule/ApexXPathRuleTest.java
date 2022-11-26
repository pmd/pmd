/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSize;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestBase;
import net.sourceforge.pmd.lang.rule.XPathRule;

/**
 * @author daniels
 */
class ApexXPathRuleTest extends ApexParserTestBase {

    private XPathRule makeXPath(String expression) {
        return apex.newXpathRule(expression);
    }


    @Test
    void testFileNameInXpath() {
        Report report = apex.executeRule(makeXPath("/UserClass[pmd:fileName() = 'Foo.cls']"),
                                         "class Foo {}",
                                         "src/Foo.cls");

        assertSize(report, 1);
    }

    @Test
    void testBooleanExpressions() {
        Report report = apex.executeRuleOnResource(makeXPath("//BooleanExpression[@Operator='&&']"),
                                                   "BooleanExpressions.cls");
        assertSize(report, 1);
    }


}
