/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import org.junit.Test;

import net.sourceforge.pmd.Rule;

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
public class TypeIsFunctionTest extends BaseXPathFunctionTest {


    @Test
    public void testHasAnnotation() {
        Rule rule = makeXpathRuleFromXPath("//Annotation[pmd-java:typeIs('java.lang.Override')]");
        assertFinds(rule, 1, "interface O { @Override void foo(); }");
    }


    @Test
    public void testHasAnnotationNonQual() {
        Rule rule = makeXpathRuleFromXPath("//Annotation[pmd-java:typeIs('Override')]");

        //does not match
        assertFinds(rule, 0, "interface O { @Override void foo(); }");
    }

    @Test
    public void testTypeIsArray() {
        Rule rule = makeXpathRuleFromXPath("//*[pmd-java:typeIs('int[]')]");

        // ArrayType + VariableDeclaratorId
        assertFinds(rule, 2, "class K { int[] i; }");
    }

    @Test
    public void testWrongTypeReturnsFalse() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceBody[pmd-java:typeIs('java.lang.Override')]");

        assertFinds(rule, 0, "interface O { @Override void foo(); }");
    }

}
