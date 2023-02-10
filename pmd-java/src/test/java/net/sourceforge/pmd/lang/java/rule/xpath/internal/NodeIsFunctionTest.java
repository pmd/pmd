/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.xpath.PmdXPathException.Phase;

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class NodeIsFunctionTest extends BaseXPathFunctionTest {

    @Test
    void testWellFormedNodeName() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[pmd-java:nodeIs('ClassOrInterfaceDeclaration')]");
        String code = "class Foo { Foo() {} void bar() {}}";

        assertFinds(rule, 1, code);
    }

    @Test
    void testNodeNameStaticallyUnknown() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[pmd-java:nodeIs(name())]");
        String code = "class Foo { Foo() {} void bar() {}}";

        assertFinds(rule, 1, code);
    }


    @Test
    void testWellFormedNodeNameForSupertype() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[pmd-java:nodeIs('AnyTypeDeclaration')]");
        String code = "class Foo { Foo() {} void bar() {}}";

        assertFinds(rule, 1, code);
    }



    @Test
    void testNonExistentNodeName() {
        // note that this would fail with a type error (boolean > integer)
        // if nodeIs fails to fail
        testWithExpectedStaticException(
            "//MethodDeclaration[pmd-java:nodeIs('ohio') > 1]",
            e -> {
                assertThat(e.getMessage(), containsString("ASTohio"));
            });

    }


    @Test
    void testNonExistentNodeNameStaticallyUnknown() {
        testWithExpectedException(
            "//MethodDeclaration[pmd-java:nodeIs(name() || 'qqq')]",
            "class Moo { void foo() {if(true){}} }",
            e -> {
                assertThat(e.getMessage(), containsString("MethodDeclarationqqq"));
                assertThat(e.getPhase(), equalTo(Phase.EVALUATION));
            });

    }


}
