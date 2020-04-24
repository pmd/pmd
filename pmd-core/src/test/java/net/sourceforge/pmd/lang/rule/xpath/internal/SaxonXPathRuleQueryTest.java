/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.xpath.internal.AbstractXPathFunctionDef;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.DummyNodeWithListAndEnum;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;

public class SaxonXPathRuleQueryTest {

    @Test
    public void testListAttribute() {
        RootNode dummy = new DummyNodeWithListAndEnum();

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
        assertExpression("(self::node()[data(@Image) = \"baz\"])/child::element(Q{}foo)", query.nodeNameToXPaths.get("dummyNode").get(0));
        assertExpression("self::node()[data(@Public) = false()]", query.nodeNameToXPaths.get("dummyNode").get(1));
        assertExpression("self::node()", query.nodeNameToXPaths.get("dummyNode").get(2));
        assertExpression("self::node()[data(@Public) = \"true\"]", query.nodeNameToXPaths.get("bar").get(0));
        assertExpression("(((docOrder((((/)/descendant::element(Q{}dummyNode))[data(@Image) = \"baz\"])/child::element(Q{}foo))) | (((/)/descendant::element(Q{}bar))[data(@Public) = \"true\"])) | (((/)/descendant::element(Q{}dummyNode))[data(@Public) = false()])) | ((/)/descendant::element(Q{}dummyNode))", query.nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0));
    }

    @Test
    public void ruleChainVisitsMultipleFilters() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[@Test1 = false()][@Test2 = true()]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(1, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        Assert.assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("(self::node()[data(@Test1) = false()])[data(@Test2) = true()]", query.nodeNameToXPaths.get("dummyNode").get(0));
        assertExpression("(((/)/descendant::element(Q{}dummyNode))[data(@Test1) = false()])[data(@Test2) = true()]", query.nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0));
    }

    @Test
    public void ruleChainVisitsCustomFunctions() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[pmd-dummy:imageIs(@Image)]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(1, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        Assert.assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("self::node()[Q{http://pmd.sourceforge.net/pmd-dummy}imageIs(exactly-one(convertUntyped(data(@Image))))]", query.nodeNameToXPaths.get("dummyNode").get(0));
        assertExpression("((/)/descendant::element(Q{}dummyNode))[Q{http://pmd.sourceforge.net/pmd-dummy}imageIs(exactly-one(convertUntyped(data(@Image))))]", query.nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0));
    }

    /**
     * If a query contains another unbounded path expression other than the first one, it must be
     * excluded from rule chain execution. Saxon itself optimizes this quite good already.
     */
    @Test
    public void ruleChainVisitsUnboundedPathExpressions() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[//ClassOrInterfaceType]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(0, ruleChainVisits.size());
        Assert.assertEquals(1, query.nodeNameToXPaths.size());
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := (/)/descendant::element(Q{}ClassOrInterfaceType) return (((/)/descendant::element(Q{}dummyNode))[exists($Q{http://saxon.sf.net/generated-variable}v0)])", query.nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0));

        // second sample, more complex
        query = createQuery("//dummyNode[ancestor::ClassOrInterfaceDeclaration[//ClassOrInterfaceType]]");
        ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(0, ruleChainVisits.size());
        Assert.assertEquals(1, query.nodeNameToXPaths.size());
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := (/)/descendant::element(Q{}ClassOrInterfaceType) return (((/)/descendant::element(Q{}dummyNode))[exists(ancestor::element(Q{}ClassOrInterfaceDeclaration)[exists($Q{http://saxon.sf.net/generated-variable}v0)])])", query.nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0));

        // third example, with boolean expr
        query = createQuery("//dummyNode[//ClassOrInterfaceType or //OtherNode]");
        ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(0, ruleChainVisits.size());
        Assert.assertEquals(1, query.nodeNameToXPaths.size());
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := (exists((/)/descendant::element(Q{}ClassOrInterfaceType))) or (exists((/)/descendant::element(Q{}OtherNode))) return (((/)/descendant::element(Q{}dummyNode))[$Q{http://saxon.sf.net/generated-variable}v0])", query.nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0));
    }

    @Test
    public void ruleChainVisitsNested() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode/foo/*/bar[@Test = 'false']");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(1, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        Assert.assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("(((self::node()/child::element(Q{}foo))/child::element())/child::element(Q{}bar))[data(@Test) = \"false\"]", query.nodeNameToXPaths.get("dummyNode").get(0));
        assertExpression("docOrder(((docOrder((docOrder(((/)/descendant::element(Q{}dummyNode))/child::element(Q{}foo)))/child::element()))/child::element(Q{}bar))[data(@Test) = \"false\"])", query.nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0));
    }

    @Test
    public void ruleChainVisitsNested2() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode/foo[@Baz = 'a']/*/bar[@Test = 'false']");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(1, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        Assert.assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("((((self::node()/child::element(Q{}foo))[data(@Baz) = \"a\"])/child::element())/child::element(Q{}bar))[data(@Test) = \"false\"]", query.nodeNameToXPaths.get("dummyNode").get(0));
        assertExpression("docOrder(((docOrder((docOrder((((/)/descendant::element(Q{}dummyNode))/child::element(Q{}foo))[data(@Baz) = \"a\"]))/child::element()))/child::element(Q{}bar))[data(@Test) = \"false\"])", query.nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0));
    }

    @Test
    public void ruleChainVisitWithVariable() {
        PropertyDescriptor<String> testClassPattern = PropertyFactory.stringProperty("testClassPattern").desc("test").defaultValue("a").build();
        SaxonXPathRuleQuery query = createQuery("//dummyNode[matches(@SimpleName, $testClassPattern)]", testClassPattern);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(1, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        Assert.assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := (exactly-one(convertUntyped(data($testClassPattern)))) treat as xs:string return (self::node()[matches(convertUntyped(data(@SimpleName)), $Q{http://saxon.sf.net/generated-variable}v0, \"\")])", query.nodeNameToXPaths.get("dummyNode").get(0));
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := (exactly-one(convertUntyped(data($testClassPattern)))) treat as xs:string return (((/)/descendant::element(Q{}dummyNode))[matches(convertUntyped(data(@SimpleName)), $Q{http://saxon.sf.net/generated-variable}v0, \"\")])", query.nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0));
    }

    @Test
    public void ruleChainVisitWithVariable2() {
        PropertyDescriptor<String> testClassPattern = PropertyFactory.stringProperty("testClassPattern").desc("test").defaultValue("a").build();
        SaxonXPathRuleQuery query = createQuery("//dummyNode[matches(@SimpleName, $testClassPattern)]/foo", testClassPattern);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(1, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        Assert.assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := (exactly-one(convertUntyped(data($testClassPattern)))) treat as xs:string return ((self::node()[matches(convertUntyped(data(@SimpleName)), $Q{http://saxon.sf.net/generated-variable}v0, \"\")])/child::element(Q{}foo))", query.nodeNameToXPaths.get("dummyNode").get(0));
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := (exactly-one(convertUntyped(data($testClassPattern)))) treat as xs:string return (docOrder((((/)/descendant::element(Q{}dummyNode))[matches(convertUntyped(data(@SimpleName)), $Q{http://saxon.sf.net/generated-variable}v0, \"\")])/child::element(Q{}foo)))", query.nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0));
    }

    private static void assertExpression(String expected, Expression actual) {
        Assert.assertEquals(normalizeExprDump(expected),
                            normalizeExprDump(actual.toString()));
        //Assert.assertEquals(expected, actual);
    }

    private static String normalizeExprDump(String dump) {
        return dump.replaceAll("\\$qq:qq-?\\d+", "\\$qq:qq000")
                   .replaceAll("\\$zz:zz-?\\d+", "\\$zz:zz000");
    }

    private static void assertQuery(int resultSize, String xpath, Node node) {
        SaxonXPathRuleQuery query = createQuery(xpath);
        List<Node> result = query.evaluate(node, new RuleContext());
        Assert.assertEquals(resultSize, result.size());
    }

    private static SaxonXPathRuleQuery createQuery(String xpath, PropertyDescriptor<?>... descriptors) {
        Map<PropertyDescriptor<?>, Object> props = new HashMap<>();
        if (descriptors != null) {
            for (PropertyDescriptor<?> prop : descriptors) {
                props.put(prop, prop.defaultValue());
            }
        }

        return new SaxonXPathRuleQuery(
            xpath,
            XPathVersion.XPATH_2_0,
            props,
            XPathHandler.getHandlerForFunctionDefs(new AbstractXPathFunctionDef("imageIs", "dummy") {
                @Override
                public SequenceType[] getArgumentTypes() {
                    return new SequenceType[] {SequenceType.SINGLE_STRING};
                }

                @Override
                public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
                    return SequenceType.SINGLE_BOOLEAN;
                }

                @Override
                public ExtensionFunctionCall makeCallExpression() {
                    return new ExtensionFunctionCall() {
                        @Override
                        public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                            Node contextNode = ((AstElementNode) context.getContextItem()).getUnderlyingNode();
                            return BooleanValue.get(arguments[0].head().getStringValue().equals(contextNode.getImage()));
                        }
                    };
                }
            }),
            DeprecatedAttrLogger.noop()
        );
    }
}
