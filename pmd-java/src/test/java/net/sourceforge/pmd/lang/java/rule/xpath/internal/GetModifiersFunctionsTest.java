/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.xpath.PmdXPathException.Phase;

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class GetModifiersFunctionsTest extends BaseXPathFunctionTest {


    @Test
    void testEffectiveModifiers() {
        Rule rule = makeXpathRuleFromXPath("//ClassDeclaration[pmd-java:modifiers() = ('public', 'abstract')]");
        String code = "interface O { class Foo { } }";

        assertFinds(rule, 2, code);
    }

    @Test
    void testExplicitModifiers() {
        Rule rule = makeXpathRuleFromXPath("//ClassDeclaration[pmd-java:explicitModifiers() = ('public', 'abstract')]");
        String code = "interface O { class Foo { } }";

        assertFinds(rule, 0, code);
    }


    @Test
    void testNotModifierOwnerReturnsEmptySequence() {
        Rule rule = makeXpathRuleFromXPath("//ClassBody[pmd-java:modifiers()]");
        String code = "interface O { class Foo { } }";

        assertFinds(rule, 0, code);
    }


    @Test
    void testStaticTypeError() {
        testWithExpectedStaticException(
            "//MethodDeclaration[(., .) is pmd-java:modifiers()]",
            e -> {
                assertThat(e.getMessage(), containsString("Type error"));
                assertThat(e.getPhase(), equalTo(Phase.INITIALIZATION));
            });

    }


}
