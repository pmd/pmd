/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.xpath.PmdXPathException;
import net.sourceforge.pmd.lang.rule.xpath.PmdXPathException.Phase;

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
public class MatchesSignatureXPathTest extends BaseXPathFunctionTest {

    @Test
    public void testMatchSig1() {
        Rule rule = makeXpathRuleFromXPath("//MethodCall[pmd-java:matchesSig('_#equals(java.lang.Object)')]");

        assertFinds(rule, 1, "class O { { this.equals(\"\"); } }");
        assertFinds(rule, 0, "class O { { this.equals(\"\", 2); }  void equals(String i, int a) {}}");
    }


    @Test
    public void testMatchSigWithReceiver() {
        Rule rule = makeXpathRuleFromXPath("//MethodCall[pmd-java:matchesSig('java.lang.Enum#equals(java.lang.Object)')]");

        assertFinds(rule, 1, "enum O {; { this.equals(\"\"); } }");
        assertFinds(rule, 0, "enum O {; { \"\".equals(\"\"); } }");
    }

    @Test
    public void testMatchSigUnresolved() {
        Rule rule = makeXpathRuleFromXPath("//MethodCall[pmd-java:matchesSig('java.lang.String#foobar()')]");

        assertFinds(rule, 0, "enum O {; { \"\".foobar(); } }");
    }

    @Test
    public void testMatchSigNoName() {
        Rule rule = makeXpathRuleFromXPath("//MethodCall[pmd-java:matchesSig('_#_(int,int)')]");

        assertFinds(rule, 2, "enum O {; { \"\".substring(1, 2); this.foo(1, 'c');} void foo(int a, int b) {} }");
    }


    @Test
    public void testMatchSigWrongTypeReturnsFalse() {
        Rule rule = makeXpathRuleFromXPath("//EnumDeclaration[pmd-java:matchesSig('_#_(int,int)')]");

        assertFinds(rule, 0, "enum O {; { \"\".substring(1, 2); this.foo(1, 'c');} void foo(int a, int b) {} }");
    }

    @Test
    public void testMatchInvalidSig() {
        Rule rule = makeXpathRuleFromXPath("//*[pmd-java:matchesSig('_#')]");

        PmdXPathException e = Assert.assertThrows(PmdXPathException.class, rule::getTargetSelector);
        assertEquals(Phase.INITIALIZATION, e.getPhase());
    }


}
