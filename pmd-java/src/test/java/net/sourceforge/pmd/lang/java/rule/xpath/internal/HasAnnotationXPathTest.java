/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import org.junit.Test;

import net.sourceforge.pmd.Rule;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class HasAnnotationXPathTest extends BaseXPathFunctionTest {


    @Test
    public void testHasAnnotation() {
        Rule rule = makeXpathRuleFromXPath("//MethodDeclaration[pmd-java:hasAnnotation('java.lang.Override')]");
        String code = "interface O { @Override void foo(); }";

        assertReportSize(rule, code, 1);
    }


    @Test
    public void testHasAnnotationNonQual() {
        Rule rule = makeXpathRuleFromXPath("//MethodDeclaration[pmd-java:hasAnnotation('Override')]");
        String code = "interface O { @Override void foo(); }";

        assertReportSize(rule, code, 0); //does not match
    }

    @Test
    public void testWrongTypeReturnsFalse() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceBody[pmd-java:hasAnnotation('java.lang.Override')]");
        String code = "interface O { @Override void foo(); }";

        assertReportSize(rule, code, 0);
    }

}
