/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSize;

import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestBase;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

/**
 * @author daniels
 */
public class ApexXPathRuleTest extends ApexParserTestBase {

    private XPathRule makeXPath(String expression) {
        XPathRule rule = new XPathRule(XPathVersion.XPATH_2_0, expression);
        rule.setLanguage(LanguageRegistry.getLanguage(ApexLanguageModule.NAME));
        rule.setMessage("XPath Rule Failed");
        return rule;
    }


    @Test
    public void testFileNameInXpath() {
        Report report = apex.executeRule(makeXPath("/UserClass[pmd:fileName() = 'Foo.cls']"),
                                         "class Foo {}",
                                         "src/Foo.cls");

        assertSize(report, 1);
    }

    @Test
    public void testBooleanExpressions() {
        Report report = apex.executeRuleOnResource(makeXPath("//BooleanExpression[@Operator='&&']"),
                                                   "BooleanExpressions.cls");
        assertSize(report, 1);
    }


}
