/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.xpath.PmdXPathException.Phase;

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
public class GetModifiersFunctionsTest extends BaseXPathFunctionTest {


    @Test
    public void testEffectiveModifiers() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[pmd-java:modifiers() = ('public', 'abstract')]");
        String code = "interface O { class Foo { } }";

        assertFinds(rule, 2, code);
    }

    @Test
    public void testExplicitModifiers() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[pmd-java:explicitModifiers() = ('public', 'abstract')]");
        String code = "interface O { class Foo { } }";

        assertFinds(rule, 0, code);
    }


    @Test
    public void testNotAccessNodeReturnsEmptySequence() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceBody[pmd-java:modifiers()]");
        String code = "interface O { class Foo { } }";

        assertFinds(rule, 0, code);
    }


    @Test
    public void testStaticTypeError() {
        testWithExpectedException(
            "//MethodDeclaration[(., .) is pmd-java:modifiers()]",
            "class Moo { void foo() {if(true){}} }",
            e -> {
                assertThat(e.getMessage(), containsString("Type error"));
                assertThat(e.getPhase(), equalTo(Phase.INITIALIZATION));
            });

    }


}
