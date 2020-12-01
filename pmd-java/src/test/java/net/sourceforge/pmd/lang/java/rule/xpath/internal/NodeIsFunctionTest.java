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
public class NodeIsFunctionTest extends BaseXPathFunctionTest {

    @Test
    public void testWellFormedNodeName() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[pmd-java:nodeIs('ClassOrInterfaceDeclaration')]");
        String code = "class Foo { Foo() {} void bar() {}}";

        assertFinds(rule, 1, code);
    }

    @Test
    public void testNodeNameStaticallyUnknown() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[pmd-java:nodeIs(name())]");
        String code = "class Foo { Foo() {} void bar() {}}";

        assertFinds(rule, 1, code);
    }


    @Test
    public void testWellFormedNodeNameForSupertype() {
        Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[pmd-java:nodeIs('AnyTypeDeclaration')]");
        String code = "class Foo { Foo() {} void bar() {}}";

        assertFinds(rule, 1, code);
    }



    @Test
    public void testNonExistentNodeName() {
        // note that this would fail with a type error (boolean > integer)
        // if nodeIs fails to fail
        testWithExpectedException(
            "//MethodDeclaration[pmd-java:nodeIs('ohio') > 1]",
            "class Moo { void foo() {if(true){}} }",
            e -> {
                assertThat(e.getMessage(), containsString("ASTohio"));
                assertThat(e.getPhase(), equalTo(Phase.INITIALIZATION));
            });

    }


    @Test
    public void testNonExistentNodeNameStaticallyUnknown() {
        testWithExpectedException(
            "//MethodDeclaration[pmd-java:nodeIs(name() || 'qqq')]",
            "class Moo { void foo() {if(true){}} }",
            e -> {
                assertThat(e.getMessage(), containsString("MethodDeclarationqqq"));
                assertThat(e.getPhase(), equalTo(Phase.EVALUATION));
            });

    }


}
