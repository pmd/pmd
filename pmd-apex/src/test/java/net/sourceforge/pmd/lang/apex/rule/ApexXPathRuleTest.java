/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import static net.sourceforge.pmd.lang.test.ast.TestUtilsKt.assertSize;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.apex.ast.ApexParserTestBase;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.rule.xpath.XPathRule;
import net.sourceforge.pmd.reporting.Report;

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
                                         FileId.fromPathLikeString("src/Foo.cls"));

        assertSize(report, 1);
    }

    @Test
    void testBooleanExpressions() {
        Report report = apex.executeRuleOnResource(makeXPath("//BooleanExpression[@Op='&&']"),
                                                   "BooleanExpressions.cls");
        assertSize(report, 1);
    }
}
