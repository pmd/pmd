/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class JaxenXPathRuleQueryTest {

    @Test
    public void testListAttribute() {
        DummyNodeWithListAndEnum dummy = new DummyNodeWithListAndEnum(1);

        assertQuery(1, "//dummyNode[@SimpleAtt = \"foo\"]", dummy);
        assertQuery(1, "//dummyNode[@Enum = \"FOO\"]", dummy);
        assertQuery(0, "//dummyNode[@Enum = \"BAR\"]", dummy);

        // queries with lists are not supported with xpath 1.0
        assertQuery(0, "//dummyNode[@List = \"[A, B]\"]", dummy);
        assertQuery(0, "//dummyNode[contains(@List, \"B\")]", dummy);
        assertQuery(0, "//dummyNode[@List = \"C\"]", dummy);
        assertQuery(0, "//dummyNode[@EnumList = \"[FOO, BAR]\"]", dummy);
        assertQuery(0, "//dummyNode[contains(@EnumList, \"BAR\")]", dummy);
        assertQuery(0, "//dummyNode[@EmptyList = \"A\"]", dummy);
    }

    @Test
    public void ruleChainVisits() {
        final String xpath = "//dummyNode[@Image='baz']/foo | //bar[@Public = 'true'] | //dummyNode[@Public = 'false'] | //dummyNode";
        JaxenXPathRuleQuery query = createQuery(xpath);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(3, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        Assert.assertTrue(ruleChainVisits.contains("bar"));
        // Note: Having AST_ROOT in the rule chain visits is probably a mistake. But it doesn't hurt, it shouldn't
        // match a real node name.
        Assert.assertTrue(ruleChainVisits.contains(JaxenXPathRuleQuery.AST_ROOT));

        DummyNodeWithListAndEnum dummy = new DummyNodeWithListAndEnum(1);
        RuleContext data = new RuleContext();
        data.setLanguageVersion(LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion());

        query.evaluate(dummy, data);
        // note: the actual xpath queries are only available after evaluating
        Assert.assertEquals(3, query.nodeNameToXPaths.size());
        Assert.assertEquals("self::node()", query.nodeNameToXPaths.get("dummyNode").get(0).toString());
        Assert.assertEquals("self::node()[(attribute::Public = \"false\")]", query.nodeNameToXPaths.get("dummyNode").get(1).toString());
        Assert.assertEquals("self::node()[(attribute::Image = \"baz\")]/child::foo", query.nodeNameToXPaths.get("dummyNode").get(2).toString());
        Assert.assertEquals("self::node()[(attribute::Public = \"true\")]", query.nodeNameToXPaths.get("bar").get(0).toString());
        Assert.assertEquals(xpath, query.nodeNameToXPaths.get(JaxenXPathRuleQuery.AST_ROOT).get(0).toString());
    }

    @Test
    public void ruleChainVisitsMultipleFilters() {
        final String xpath = "//dummyNode[@Test1 = 'false'][@Test2 = 'true']";
        JaxenXPathRuleQuery query = createQuery(xpath);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(2, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        // Note: Having AST_ROOT in the rule chain visits is probably a mistake. But it doesn't hurt, it shouldn't
        // match a real node name.
        Assert.assertTrue(ruleChainVisits.contains(JaxenXPathRuleQuery.AST_ROOT));

        DummyNodeWithListAndEnum dummy = new DummyNodeWithListAndEnum(1);
        RuleContext data = new RuleContext();
        data.setLanguageVersion(LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion());

        query.evaluate(dummy, data);
        // note: the actual xpath queries are only available after evaluating
        Assert.assertEquals(2, query.nodeNameToXPaths.size());
        Assert.assertEquals("self::node()[(attribute::Test1 = \"false\")][(attribute::Test2 = \"true\")]", query.nodeNameToXPaths.get("dummyNode").get(0).toString());
        Assert.assertEquals(xpath, query.nodeNameToXPaths.get(JaxenXPathRuleQuery.AST_ROOT).get(0).toString());
    }

    @Test
    public void ruleChainVisitsNested() {
        final String xpath = "//dummyNode/foo/*/bar[@Test = 'false']";
        JaxenXPathRuleQuery query = createQuery(xpath);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(2, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        // Note: Having AST_ROOT in the rule chain visits is probably a mistake. But it doesn't hurt, it shouldn't
        // match a real node name.
        Assert.assertTrue(ruleChainVisits.contains(JaxenXPathRuleQuery.AST_ROOT));

        DummyNodeWithListAndEnum dummy = new DummyNodeWithListAndEnum(1);
        RuleContext data = new RuleContext();
        data.setLanguageVersion(LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion());

        query.evaluate(dummy, data);
        // note: the actual xpath queries are only available after evaluating
        Assert.assertEquals(2, query.nodeNameToXPaths.size());
        Assert.assertEquals("self::node()/child::foo/child::*/child::bar[(attribute::Test = \"false\")]", query.nodeNameToXPaths.get("dummyNode").get(0).toString());
        Assert.assertEquals(xpath, query.nodeNameToXPaths.get(JaxenXPathRuleQuery.AST_ROOT).get(0).toString());
    }

    @Test
    public void ruleChainVisitsNested2() {
        final String xpath = "//dummyNode/foo[@Baz = 'a']/*/bar[@Test = 'false']";
        JaxenXPathRuleQuery query = createQuery(xpath);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(2, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        // Note: Having AST_ROOT in the rule chain visits is probably a mistake. But it doesn't hurt, it shouldn't
        // match a real node name.
        Assert.assertTrue(ruleChainVisits.contains(JaxenXPathRuleQuery.AST_ROOT));

        DummyNodeWithListAndEnum dummy = new DummyNodeWithListAndEnum(1);
        RuleContext data = new RuleContext();
        data.setLanguageVersion(LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion());

        query.evaluate(dummy, data);
        // note: the actual xpath queries are only available after evaluating
        Assert.assertEquals(2, query.nodeNameToXPaths.size());
        Assert.assertEquals("self::node()/child::foo[(attribute::Baz = \"a\")]/child::*/child::bar[(attribute::Test = \"false\")]", query.nodeNameToXPaths.get("dummyNode").get(0).toString());
        Assert.assertEquals(xpath, query.nodeNameToXPaths.get(JaxenXPathRuleQuery.AST_ROOT).get(0).toString());
    }

    private static void assertQuery(int resultSize, String xpath, Node node) {
        JaxenXPathRuleQuery query = createQuery(xpath);
        RuleContext data = new RuleContext();
        data.setLanguageVersion(LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion());
        List<Node> result = query.evaluate(node, data);
        Assert.assertEquals(resultSize, result.size());
    }

    private static JaxenXPathRuleQuery createQuery(String xpath) {
        JaxenXPathRuleQuery query = new JaxenXPathRuleQuery();
        query.setVersion("1.0");
        query.setProperties(Collections.<PropertyDescriptor<?>, Object>emptyMap());
        query.setXPath(xpath);
        return query;
    }
}
