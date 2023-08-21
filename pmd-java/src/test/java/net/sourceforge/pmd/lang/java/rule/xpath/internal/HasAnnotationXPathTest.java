/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Rule;

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class HasAnnotationXPathTest extends BaseXPathFunctionTest {


    @Test
    void testHasAnnotation() {
        Rule rule = makeXpathRuleFromXPath("//MethodDeclaration[pmd-java:hasAnnotation('java.lang.Override')]");
        String code = "interface O { @Override void foo(); }";

        assertFinds(rule, 1, code);
    }


    @Test
    void testHasAnnotationNonQual() {
        Rule rule = makeXpathRuleFromXPath("//MethodDeclaration[pmd-java:hasAnnotation('Override')]");
        String code = "interface O { @Override void foo(); }";

        //does not match
        assertFinds(rule, 0, code);
    }

    @Test
    void testWrongTypeReturnsFalse() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceBody[pmd-java:hasAnnotation('java.lang.Override')]");
        String code = "interface O { @Override void foo(); }";

        assertFinds(rule, 0, code);
    }

}
