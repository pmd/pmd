/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class SaxonXPathRuleQueryTest {

    @Test
    public void testListAttribute() {
        DummyNodeWithListAndEnum dummy = new DummyNodeWithListAndEnum(1);

        assertQuery(1, "//dummyNode[@List = \"A\"]", dummy);
        assertQuery(1, "//dummyNode[@List = \"B\"]", dummy);
        assertQuery(0, "//dummyNode[@List = \"C\"]", dummy);
        assertQuery(1, "//dummyNode[@Enum = \"FOO\"]", dummy);
        assertQuery(0, "//dummyNode[@Enum = \"BAR\"]", dummy);
        assertQuery(1, "//dummyNode[@EnumList = \"FOO\"]", dummy);
        assertQuery(1, "//dummyNode[@EnumList = \"BAR\"]", dummy);
        assertQuery(1, "//dummyNode[@EnumList = (\"FOO\", \"BAR\")]", dummy);
        assertQuery(0, "//dummyNode[@EmptyList = (\"A\")]", dummy);
    }

    @Test
    public void ruleChainVisits() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[@Image='baz']/foo | //bar[@Public = 'true'] | //dummyNode[@Public = false()] | //dummyNode");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(2, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        Assert.assertTrue(ruleChainVisits.contains("bar"));

        Assert.assertEquals(3, query.nodeNameToXPaths.size());
        assertExpression("self::node()", query.nodeNameToXPaths.get("dummyNode").get(0).toString());
        assertExpression("(self::node()[QuantifiedExpression(Atomizer(attribute::attribute(Public, xs:anyAtomicType)), ($qq:qq609962972 singleton eq false()))])", query.nodeNameToXPaths.get("dummyNode").get(1).toString());
        assertExpression("((self::node()[QuantifiedExpression(Atomizer(attribute::attribute(Image, xs:anyAtomicType)), ($qq:qq106374177 singleton eq \"baz\"))])/child::element(foo, xs:anyType))", query.nodeNameToXPaths.get("dummyNode").get(2).toString());
        assertExpression("(self::node()[QuantifiedExpression(Atomizer(attribute::attribute(Public, xs:anyAtomicType)), ($qq:qq232307208 singleton eq \"true\"))])", query.nodeNameToXPaths.get("bar").get(0).toString());
        assertExpression("(((DocumentSorter(((((/)/descendant::element(dummyNode, xs:anyType))[QuantifiedExpression(Atomizer(attribute::attribute(Image, xs:anyAtomicType)), ($qq:qq000 singleton eq \"baz\"))])/child::element(foo, xs:anyType))) | (((/)/descendant::element(bar, xs:anyType))[QuantifiedExpression(Atomizer(attribute::attribute(Public, xs:anyAtomicType)), ($qq:qq000 singleton eq \"true\"))])) | (((/)/descendant::element(dummyNode, xs:anyType))[QuantifiedExpression(Atomizer(attribute::attribute(Public, xs:anyAtomicType)), ($qq:qq000 singleton eq false()))])) | ((/)/descendant::element(dummyNode, xs:anyType)))", query.nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0).toString());
    }

    private static void assertExpression(String expected, String actual) {
        Assert.assertEquals(expected.replaceAll("\\$qq:qq\\d+", "\\$qq:qq000"), actual.replaceAll("\\$qq:qq\\d+", "\\$qq:qq000"));
    }

    @Test
    public void ruleChainVisitsCompatibilityMode() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[@Image='baz']/foo | //bar[@Public = 'true'] | //dummyNode[@Public = 'false']");
        query.setVersion(XPathRuleQuery.XPATH_1_0_COMPATIBILITY);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(2, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        Assert.assertTrue(ruleChainVisits.contains("bar"));

        Assert.assertEquals(3, query.nodeNameToXPaths.size());
        assertExpression("(self::node()[QuantifiedExpression(Atomizer(attribute::attribute(Public, xs:anyAtomicType)), ($qq:qq609962972 singleton eq \"false\"))])", query.nodeNameToXPaths.get("dummyNode").get(0).toString());
        assertExpression("((self::node()[QuantifiedExpression(Atomizer(attribute::attribute(Image, xs:anyAtomicType)), ($qq:qq106374177 singleton eq \"baz\"))])/child::element(foo, xs:anyType))", query.nodeNameToXPaths.get("dummyNode").get(1).toString());
        assertExpression("(self::node()[QuantifiedExpression(Atomizer(attribute::attribute(Public, xs:anyAtomicType)), ($qq:qq232307208 singleton eq \"true\"))])", query.nodeNameToXPaths.get("bar").get(0).toString());
        assertExpression("((DocumentSorter(((((/)/descendant::element(dummyNode, xs:anyType))[QuantifiedExpression(Atomizer(attribute::attribute(Image, xs:anyAtomicType)), ($qq:qq106374177 singleton eq \"baz\"))])/child::element(foo, xs:anyType))) | (((/)/descendant::element(bar, xs:anyType))[QuantifiedExpression(Atomizer(attribute::attribute(Public, xs:anyAtomicType)), ($qq:qq232307208 singleton eq \"true\"))])) | (((/)/descendant::element(dummyNode, xs:anyType))[QuantifiedExpression(Atomizer(attribute::attribute(Public, xs:anyAtomicType)), ($qq:qq609962972 singleton eq \"false\"))]))", query.nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0).toString());
    }

    private static void assertQuery(int resultSize, String xpath, Node node) {
        SaxonXPathRuleQuery query = createQuery(xpath);
        List<Node> result = query.evaluate(node, new RuleContext());
        Assert.assertEquals(resultSize, result.size());
    }

    private static SaxonXPathRuleQuery createQuery(String xpath) {
        SaxonXPathRuleQuery query = new SaxonXPathRuleQuery();
        query.setVersion(XPathRuleQuery.XPATH_2_0);
        query.setProperties(Collections.<PropertyDescriptor<?>, Object>emptyMap());
        query.setXPath(xpath);
        return query;
    }
}
