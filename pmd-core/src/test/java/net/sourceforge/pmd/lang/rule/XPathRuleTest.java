/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.junit.JavaUtilLoggingRule;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNodeWithDeprecatedAttribute;
import net.sourceforge.pmd.lang.ast.DummyNodeWithListAndEnum;
import net.sourceforge.pmd.lang.ast.DummyRoot;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

public class XPathRuleTest {

    @Rule
    public JavaUtilLoggingRule loggingRule = new JavaUtilLoggingRule(Attribute.class.getName());

    @Test
    public void testAttributeDeprecation10() {
        testDeprecation(XPathVersion.XPATH_1_0);
    }

    @Test
    public void testAttributeDeprecation20() {
        testDeprecation(XPathVersion.XPATH_2_0);
    }

    @Test
    public void testListAttributeDeprecation20() {
        XPathRule xpr = makeRuleWithList("TestRuleWithListAccess");
        loggingRule.clear();

        RuleContext ctx = new RuleContext();
        ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        DummyNode firstNode = newNodeWithList();
        xpr.apply(firstNode, ctx);
        assertEquals(1, ctx.getReport().getViolations().size());

        String log = loggingRule.getLog();
        assertThat(log, Matchers.containsString("Use of deprecated attribute 'dummyNode/@List' by XPath rule 'TestRuleWithListAccess'"));

        loggingRule.clear();
        xpr.apply(newNodeWithList(), ctx); // with another node
        assertEquals(2, ctx.getReport().getViolations().size());
        assertEquals("", loggingRule.getLog()); // no addtional warnings

        // with another rule forked from the same one (in multithreaded processor)
        xpr.deepCopy().apply(newNodeWithList(), ctx);
        assertEquals(3, ctx.getReport().getViolations().size());
        assertEquals("", loggingRule.getLog()); // no addtional warnings

        // with another rule on the same node, new warnings
        XPathRule otherRule = makeRuleWithList("OtherTestRuleWithListAccess");
        otherRule.setRuleSetName("rset.xml");
        otherRule.apply(firstNode, ctx);
        assertEquals(4, ctx.getReport().getViolations().size());
        log = loggingRule.getLog();
        assertThat(log, Matchers.containsString("Use of deprecated attribute 'dummyNode/@List' by XPath rule 'OtherTestRuleWithListAccess' (in ruleset 'rset.xml')"));
    }


    private XPathRule makeRuleWithList(String name) {
        XPathRule xpr = new XPathRule(XPathVersion.XPATH_2_0, "//dummyNode[@List = 'A']");
        xpr.setName(name);
        xpr.setMessage("list is 'a'");
        return xpr;
    }


    private DummyNode newNodeWithList() {
        DummyRoot root = new DummyRoot();
        DummyNode firstNode = new DummyNodeWithListAndEnum(0);
        firstNode.setCoords(1, 1, 1, 2);
        root.addChild(firstNode, 0);
        return root;
    }

    public void testDeprecation(XPathVersion version) {
        XPathRule xpr = makeRule(version, "SomeRule");

        loggingRule.clear();

        RuleContext ctx = new RuleContext();
        ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        DummyNode firstNode = newNode();
        xpr.apply(firstNode, ctx);
        assertEquals(1, ctx.getReport().getViolations().size());

        String log = loggingRule.getLog();
        assertThat(log, Matchers.containsString("Use of deprecated attribute 'dummyNode/@Size' by XPath rule 'SomeRule'"));
        assertThat(log, Matchers.containsString("Use of deprecated attribute 'dummyNode/@Name' by XPath rule 'SomeRule', please use @Image instead"));


        loggingRule.clear();

        // with another node
        xpr.apply(newNode(), ctx);
        assertEquals(2, ctx.getReport().getViolations().size());

        assertEquals("", loggingRule.getLog()); // no additional warnings


        // with another rule forked from the same one (in multithreaded processor)
        xpr.deepCopy().apply(newNode(), ctx);
        assertEquals(3, ctx.getReport().getViolations().size());

        assertEquals("", loggingRule.getLog()); // no additional warnings

        // with another rule on the same node, new warnings
        XPathRule otherRule = makeRule(version, "OtherRule");
        otherRule.setRuleSetName("rset.xml");
        otherRule.apply(firstNode, ctx);
        assertEquals(4, ctx.getReport().getViolations().size());

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

    public DummyNode newNode() {
        DummyRoot root = new DummyRoot();
        DummyNode dummy = new DummyNodeWithDeprecatedAttribute(2);
        dummy.setCoords(1, 1, 1, 2);
        root.addChild(dummy, 0);
        return root;
    }
}
