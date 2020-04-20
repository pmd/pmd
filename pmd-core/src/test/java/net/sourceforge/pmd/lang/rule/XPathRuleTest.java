/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.junit.JavaUtilLoggingRule;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNodeWithDeprecatedAttribute;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

public class XPathRuleTest {

    @Rule
    public JavaUtilLoggingRule loggingRule = new JavaUtilLoggingRule(Attribute.class.getName());

    /**
     * It's easy to forget the attribute "typeResolution=true" when
     * defining XPath rules in xml. Therefore we by default enable
     * typeresolution. For Java rules, type resolution was enabled by
     * default long time ago.
     *
     * @see <a href="https://github.com/pmd/pmd/issues/2048">#2048 [core] Enable type resolution by default for XPath
     *     rules</a>
     */
    @Test
    public void typeResolutionShouldBeEnabledByDefault() {
        XPathRule rule = new XPathRule();
        Assert.assertTrue(rule.isTypeResolution());

        XPathRule rule2 = new XPathRule(".");
        Assert.assertTrue(rule2.isTypeResolution());
    }


    @Test
    public void testAttributeDeprecation10() {
        testDeprecation(XPathVersion.XPATH_1_0);
    }

    @Test
    public void testAttributeDeprecation20() {
        testDeprecation(XPathVersion.XPATH_2_0);
    }

    public void testDeprecation(XPathVersion version) {
        XPathRule xpr = makeRule(version, "SomeRule");

        loggingRule.clear();

        RuleContext ctx = new RuleContext();
        ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        DummyNode firstNode = newNode();
        eval(ctx, xpr, firstNode);
        assertEquals(1, ctx.getReport().size());

        String log = loggingRule.getLog();
        assertThat(log, Matchers.containsString("Use of deprecated attribute 'dummyNode/@Size' by XPath rule 'SomeRule'"));
        assertThat(log, Matchers.containsString("Use of deprecated attribute 'dummyNode/@Name' by XPath rule 'SomeRule', please use @Image instead"));


        loggingRule.clear();

        eval(ctx, xpr, newNode()); // with another node
        assertEquals(2, ctx.getReport().size());

        assertEquals("", loggingRule.getLog()); // no additional warnings


        // with another rule forked from the same one (in multithreaded processor)
        eval(ctx, xpr.deepCopy(), newNode());
        assertEquals(3, ctx.getReport().size());

        assertEquals("", loggingRule.getLog()); // no additional warnings

        // with another rule on the same node, new warnings
        XPathRule otherRule = makeRule(version, "OtherRule");
        otherRule.setRuleSetName("rset.xml");
        eval(ctx, otherRule, firstNode);
        assertEquals(4, ctx.getReport().size());

        log = loggingRule.getLog();
        assertThat(log, Matchers.containsString("Use of deprecated attribute 'dummyNode/@Size' by XPath rule 'OtherRule' (in ruleset 'rset.xml')"));
        assertThat(log, Matchers.containsString("Use of deprecated attribute 'dummyNode/@Name' by XPath rule 'OtherRule' (in ruleset 'rset.xml'), please use @Image instead"));

    }

    public XPathRule makeRule(XPathVersion version, String name) {
        XPathRule xpr = new XPathRule(version, "//dummyNode[@Size >= 2 and @Name='foo']");
        xpr.setName(name);
        xpr.setMessage("gotcha");
        return xpr;
    }

    public void eval(RuleContext ctx, net.sourceforge.pmd.Rule rule, DummyNode node) {
        rule.apply(singletonList(node), ctx);
    }

    public DummyNode newNode() {
        DummyNode dummy = new DummyNodeWithDeprecatedAttribute(2);
        dummy.setCoords(1, 1, 1, 2);
        return dummy;
    }


}
