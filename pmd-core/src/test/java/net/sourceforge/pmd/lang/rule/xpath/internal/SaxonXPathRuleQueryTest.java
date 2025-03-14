/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.followPath;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.node;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.nodeB;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.root;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.tree;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import net.sourceforge.pmd.DummyParsingHelper;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.DummyNodeWithListAndEnum;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.xpath.PmdXPathException;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionDefinition;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

import net.sf.saxon.expr.Expression;

class SaxonXPathRuleQueryTest {

    @RegisterExtension
    private final DummyParsingHelper helper = new DummyParsingHelper();

    //    Unsupported: https://github.com/pmd/pmd/issues/2451
    //    @Test
    //    void testListAttribute() {
    //        RootNode dummy = new DummyNodeWithListAndEnum();
    //
    //        assertQuery(1, "//dummyNode[@List = \"A\"]", dummy);
    //        assertQuery(1, "//dummyNode[@List = \"B\"]", dummy);
    //        assertQuery(0, "//dummyNode[@List = \"C\"]", dummy);
    //        assertQuery(1, "//dummyNode[@Enum = \"FOO\"]", dummy);
    //        assertQuery(0, "//dummyNode[@Enum = \"BAR\"]", dummy);
    //        assertQuery(1, "//dummyNode[@EnumList = \"FOO\"]", dummy);
    //        assertQuery(1, "//dummyNode[@EnumList = \"BAR\"]", dummy);
    //        assertQuery(1, "//dummyNode[@EnumList = (\"FOO\", \"BAR\")]", dummy);
    //        assertQuery(0, "//dummyNode[@EmptyList = (\"A\")]", dummy);
    //    }

    @Test
    void testHigherOrderFuns() { // XPath 3.1
        DummyRootNode tree = helper.parse("(oha)");

        assertQuery(1, "//dummyRootNode["
            + "(@Image => substring-after('[') => substring-before(']')) "
            //            --------------------    ---------------------
            //                Those are higher order functions,
            //                the arrow operator applies it to the left expression
            + "! . = '']", tree);
        //     ^ This is the mapping operator, it applies a function on
        //     the right to every element of the sequence on the left

        // Together this says,

        // for r in dummyRootNode:
        //    tmp = atomize(r/@Image)
        //    tmp = substring-after('[', tmp)
        //    tmp = substring-before(']', tmp)
        //    if tmp == '':
        //      yield r

    }

    @Test
    void testListProperty() {
        RootNode dummy = new DummyNodeWithListAndEnum();

        PropertyDescriptor<List<String>> prop = PropertyFactory.stringListProperty("prop")
                                                               .defaultValues("FOO", "BAR")
                                                               .desc("description").build();


        assertQuery(1, "//dummyRootNode[@Enum = $prop]", dummy, prop);
    }

    @Test
    void testInvalidReturn() {
        DummyNodeWithListAndEnum dummy = new DummyNodeWithListAndEnum();

        PmdXPathException exception = assertThrows(PmdXPathException.class, () -> {
            createQuery("1+2").evaluate(dummy);
        });
        assertThat(exception.getMessage(), CoreMatchers.containsString("XPath rule expression returned a non-node"));
        assertThat(exception.getMessage(), CoreMatchers.containsString("Int64Value"));
    }

    @Test
    void testRootExpression() {
        DummyRootNode dummy = helper.parse("(oha)");

        List<Node> result = assertQuery(1, "/", dummy);
        assertEquals(dummy, result.get(0));
    }

    @Test
    void testRootExpressionIsADocumentNode() {
        DummyRootNode dummy = helper.parse("(oha)");

        List<Node> result = assertQuery(1, "(/)[self::document-node()]", dummy);
        assertEquals(dummy, result.get(0));
    }

    @Test
    void testRootExpressionWithName() {
        DummyRootNode dummy = helper.parse("(oha)");
        String xpathName = dummy.getXPathNodeName();

        List<Node> result = assertQuery(1, "(/)[self::document-node(element(" + xpathName + "))]", dummy);
        assertEquals(dummy, result.get(0));

        assertQuery(0, "(/)[self::document-node(element(DummyNodeX))]", dummy);
    }

    @Test
    void testListAttributes() {
        DummyRootNode dummy = helper.parse("(a(b))");
        List<Node> result = assertQuery(1,
                "//dummyNode[count(distinct-values(@Lines)) > 0 and not(empty(index-of(@Lines, 'a')))]", dummy);

        assertEquals(dummy.getChild(0), result.get(0));
    }

    @Test
    void ruleChainVisits() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[@Image='baz']/foo | //bar[@Public = 'true'] | //dummyNode[@Public = false()] | //dummyNode");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(2, ruleChainVisits.size());
        assertTrue(ruleChainVisits.contains("dummyNode"));
        assertTrue(ruleChainVisits.contains("bar"));

        assertEquals(3, query.nodeNameToXPaths.size());
        assertExpression("(self::node()[(data(attribute::attribute(Image))) = baz])/child::element(foo)", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("self::node()[(data(attribute::attribute(Public))) = false]", query.getExpressionsForLocalNameOrDefault("dummyNode").get(1));
        assertExpression("self::node()", query.getExpressionsForLocalNameOrDefault("dummyNode").get(2));
        assertExpression("self::node()[(data(attribute::attribute(Public))) = true]", query.getExpressionsForLocalNameOrDefault("bar").get(0));
        assertExpression("(((docOrder((((/)/descendant::element(dummyNode))[(data(attribute::attribute(Image))) = baz])/child::element(foo))) | (((/)/descendant::element(bar))[(data(attribute::attribute(Public))) = true])) | (((/)/descendant::element(dummyNode))[(data(attribute::attribute(Public))) = false])) | ((/)/descendant::element(dummyNode))", query.getFallbackExpr());
    }

    @Test
    void ruleChainVisitsMultipleFilters() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[@Test1 = false()][@Test2 = true()]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("(self::node()[(data(attribute::attribute(Test1))) = false])[(data(attribute::attribute(Test2))) = true]", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("(((/)/descendant::element(dummyNode))[(data(attribute::attribute(Test1))) = false])[(data(attribute::attribute(Test2))) = true]", query.getFallbackExpr());
    }

    @Test
    void ruleChainVisitsCustomFunctions() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[pmd-dummy:imageIs(@Image)]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("self::node()[Q{http://pmd.sourceforge.net/pmd-dummy}imageIs(exactly-one(convertTo_xs:string(data(attribute::attribute(Image)))))]", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("((/)/descendant::element(Q{}dummyNode))[Q{http://pmd.sourceforge.net/pmd-dummy}imageIs(exactly-one(convertTo_xs:string(data(attribute::attribute(Image)))))]", query.getFallbackExpr());
    }

    /**
     * If a query contains another unbounded path expression other than the first one, it must be
     * excluded from rule chain execution. Saxon itself optimizes this quite good already.
     */
    @Test
    void ruleChainVisitsUnboundedPathExpressions() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[//ClassOrInterfaceType]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(0, ruleChainVisits.size());
        assertEquals(1, query.nodeNameToXPaths.size());
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := exists((/)/descendant::element(Q{}ClassOrInterfaceType)) return (((/)/descendant::element(Q{}dummyNode))[$Q{http://saxon.sf.net/generated-variable}v0])", query.getFallbackExpr());

        // second sample, more complex
        query = createQuery("//dummyNode[ancestor::ClassOrInterfaceDeclaration[//ClassOrInterfaceType]]");
        ruleChainVisits = query.getRuleChainVisits();
        assertEquals(0, ruleChainVisits.size());
        assertEquals(1, query.nodeNameToXPaths.size());
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := exists((/)/descendant::element(Q{}ClassOrInterfaceType)) return (((/)/descendant::element(Q{}dummyNode))[exists(ancestor::element(Q{}ClassOrInterfaceDeclaration)[$Q{http://saxon.sf.net/generated-variable}v0])])", query.getFallbackExpr());

        // third example, with boolean expr
        query = createQuery("//dummyNode[//ClassOrInterfaceType or //OtherNode]");
        ruleChainVisits = query.getRuleChainVisits();
        assertEquals(0, ruleChainVisits.size());
        assertEquals(1, query.nodeNameToXPaths.size());
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := (exists((/)/descendant::element(Q{}ClassOrInterfaceType))) or (exists((/)/descendant::element(Q{}OtherNode))) return (((/)/descendant::element(Q{}dummyNode))[$Q{http://saxon.sf.net/generated-variable}v0])", query.getFallbackExpr());
    }

    @Test
    void ruleChainVisitsNested() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode/foo/*/bar[@Test = 'false']");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("(((self::node()/child::element(foo))/child::element())/child::element(bar))[(data(attribute::attribute(Test))) = false]", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("docOrder(((docOrder((((/)/descendant::element(dummyNode))/child::element(foo))/child::element()))/child::element(bar))[(data(attribute::attribute(Test))) = false])", query.getFallbackExpr());
    }

    @Test
    void ruleChainVisitsNested2() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode/foo[@Baz = 'a']/*/bar[@Test = 'false']");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("((((self::node()/child::element(foo))[(data(attribute::attribute(Baz))) = a])/child::element())/child::element(bar))[(data(attribute::attribute(Test))) = false]", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("docOrder(((docOrder(((((/)/descendant::element(dummyNode))/child::element(foo))[(data(attribute::attribute(Baz))) = a])/child::element()))/child::element(bar))[(data(attribute::attribute(Test))) = false])", query.getFallbackExpr());
    }

    @Test
    void unionBeforeSlash() {
        SaxonXPathRuleQuery query = createQuery("(//dummyNode | //dummyNodeB)/dummyNode[@Image = '10']");

        DummyRootNode tree = tree(() -> root(
            node(
                node()
            ),
            nodeB(
                node()
            )
        ));

        tree.descendantsOrSelf().forEach(n -> {
            List<Node> results = query.evaluate(n);
            assertEquals(1, results.size());
            assertEquals(followPath(tree, "10"), results.get(0));
        });

        assertExpression("docOrder((((/)/descendant::(element(dummyNode) | element(dummyNodeB)))/child::element(dummyNode))[(data(attribute::attribute(Image))) = 10])", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
    }

    @Test
    void unionBeforeSlashWithFilter() {
        SaxonXPathRuleQuery query = createQuery("(//dummyNode[@Image='0'] | //dummyNodeB[@Image='1'])/dummyNode[@Image = '10']");

        DummyRootNode tree = tree(() -> root(
            node(
                node()
            ),
            nodeB(
                node()
            )
        ));

        assertEquals(0, query.getRuleChainVisits().size());
        assertExpression("docOrder((((((/)/descendant::element(dummyNode))[(data(attribute::attribute(Image))) = 0]) | (((/)/descendant::element(dummyNodeB))[(data(attribute::attribute(Image))) = 1]))/child::element(dummyNode))[(data(attribute::attribute(Image))) = 10])", query.getFallbackExpr());

        tree.descendantsOrSelf().forEach(n -> {
            List<Node> results = query.evaluate(n);
            assertEquals(1, results.size());
            assertEquals(followPath(tree, "10"), results.get(0));
        });
    }

    @Test
    void unionBeforeSlashDeeper() {
        SaxonXPathRuleQuery query = createQuery("(//dummyNode | //dummyNodeB)/dummyNode/dummyNode");

        DummyRootNode tree = tree(() -> root(
            node(
                node(
                    node()
                )
            ),
            nodeB(
                node()
            )
        ));

        assertEquals(0, query.getRuleChainVisits().size());
        assertExpression("docOrder((((/)/descendant::(element(dummyNode) | element(dummyNodeB)))/child::element(dummyNode))/child::element(dummyNode))", query.getFallbackExpr());

        tree.descendantsOrSelf().forEach(n -> {
            List<Node> results = query.evaluate(n);
            assertEquals(1, results.size());
            assertEquals(followPath(tree, "000"), results.get(0));
        });
    }

    @Test
    void ruleChainVisitWithVariable() {
        PropertyDescriptor<String> testClassPattern = PropertyFactory.stringProperty("testClassPattern").desc("test").defaultValue("a").build();
        SaxonXPathRuleQuery query = createQuery("//dummyNode[matches(@SimpleName, $testClassPattern)]", testClassPattern);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("self::node()[matches(zero-or-one(convertTo_xs:string(data(attribute::attribute(SimpleName)))), a, )]", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("((/)/descendant::element(Q{}dummyNode))[matches(zero-or-one(convertTo_xs:string(data(attribute::attribute(SimpleName)))), a, )]", query.getFallbackExpr());
    }

    @Test
    void ruleChainVisitWithVariable2() {
        PropertyDescriptor<String> testClassPattern = PropertyFactory.stringProperty("testClassPattern").desc("test").defaultValue("a").build();
        SaxonXPathRuleQuery query = createQuery("//dummyNode[matches(@SimpleName, $testClassPattern)]/foo", testClassPattern);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("(self::node()[matches(zero-or-one(convertTo_xs:string(data(attribute::attribute(SimpleName)))), a, )])/child::element(Q{}foo)", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("docOrder((((/)/descendant::element(Q{}dummyNode))[matches(zero-or-one(convertTo_xs:string(data(attribute::attribute(SimpleName)))), a, )])/child::element(Q{}foo))", query.getFallbackExpr());
    }

    @Test
    void ruleChainVisitWithTwoFunctions() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[ends-with(@Image, 'foo')][pmd-dummy:imageIs('bar')]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("let $v0 := imageIs(bar) return ((self::node()[ends-with(zero-or-one(convertTo_xs:string(data(attribute::attribute(Image)))), foo)])[$v0])", query.nodeNameToXPaths.get("dummyNode").get(0));
    }

    @Test
    void ruleChainWithUnions() {
        SaxonXPathRuleQuery query = createQuery("(//ForStatement | //WhileStatement | //DoStatement)//AssignmentOperator");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(0, ruleChainVisits.size());
    }

    @Test
    void ruleChainWithUnionsAndFilter() {
        SaxonXPathRuleQuery query = createQuery("(//ForStatement | //WhileStatement | //DoStatement)//AssignmentOperator[@Image='foo']");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(0, ruleChainVisits.size());
    }

    @Test
    void ruleChainWithUnionsCustomFunctionsVariant1() {
        SaxonXPathRuleQuery query = createQuery("(//ForStatement | //WhileStatement | //DoStatement)//dummyNode[pmd-dummy:imageIs(@Image)]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(0, ruleChainVisits.size());
    }

    @Test
    void ruleChainWithUnionsCustomFunctionsVariant2() {
        SaxonXPathRuleQuery query = createQuery("//(ForStatement | WhileStatement | DoStatement)//dummyNode[pmd-dummy:imageIs(@Image)]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(0, ruleChainVisits.size());
    }

    @Test
    void ruleChainWithUnionsCustomFunctionsVariant3() {
        SaxonXPathRuleQuery query = createQuery("//ForStatement//dummyNode[pmd-dummy:imageIs(@Image)]"
                                                    + " | //WhileStatement//dummyNode[pmd-dummy:imageIs(@Image)]"
                                                    + " | //DoStatement//dummyNode[pmd-dummy:imageIs(@Image)]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(3, ruleChainVisits.size());
        assertTrue(ruleChainVisits.contains("ForStatement"));
        assertTrue(ruleChainVisits.contains("WhileStatement"));
        assertTrue(ruleChainVisits.contains("DoStatement"));

        final String expectedSubexpression = "(self::node()/descendant::element(dummyNode))[imageIs(exactly-one(convertTo_xs:string(data(attribute::attribute(Image)))))]";
        assertExpression(expectedSubexpression, query.nodeNameToXPaths.get("ForStatement").get(0));
        assertExpression(expectedSubexpression, query.nodeNameToXPaths.get("WhileStatement").get(0));
        assertExpression(expectedSubexpression, query.nodeNameToXPaths.get("DoStatement").get(0));
    }

    @Test
    void ruleChainVisitsWithUnionsAndLets() {
        PropertyDescriptor<Boolean> boolProperty = PropertyFactory.booleanProperty("checkAll").desc("test").defaultValue(true).build();
        SaxonXPathRuleQuery query = createQuery("//dummyNode[$checkAll and ClassOrInterfaceType] | //ForStatement[not($checkAll)]", boolProperty);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(2, ruleChainVisits.size());
        assertTrue(ruleChainVisits.contains("dummyNode"));
        assertTrue(ruleChainVisits.contains("ForStatement"));
    }

    private static void assertExpression(String expected, Expression actual) {
        assertEquals(normalizeExprDump(expected),
                     normalizeExprDump(actual.toString()));
    }

    private static String normalizeExprDump(String dump) {
        return dump.replaceAll("Q\\{[^}]*+}", "") // remove namespaces
                   // generated variable ids
                   .replaceAll("\\$qq:qq-?\\d+", "\\$qq:qq000")
                   .replaceAll("\\$zz:zz-?\\d+", "\\$zz:zz000");
    }

    private static List<Node> assertQuery(int resultSize, String xpath, Node node, PropertyDescriptor<?>... descriptors) {
        SaxonXPathRuleQuery query = createQuery(xpath, descriptors);
        List<Node> result = query.evaluate(node);
        assertEquals(resultSize, result.size(), "Wrong number of matched nodes");
        return result;
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
            XPathVersion.DEFAULT,
            props,
            XPathHandler.getHandlerForFunctionDefs(imageIsFunction()),
            DeprecatedAttrLogger.noop()
        );
    }

    @NonNull
    private static XPathFunctionDefinition imageIsFunction() {
        return new XPathFunctionDefinition("imageIs", DummyLanguageModule.getInstance()) {
            @Override
            public Type[] getArgumentTypes() {
                return new Type[] {Type.SINGLE_STRING};
            }

            @Override
            public Type getResultType() {
                return Type.SINGLE_BOOLEAN;
            }

            @Override
            public FunctionCall makeCallExpression() {
                return (contextNode, arguments) -> StringUtils.equals(arguments[0].toString(), contextNode.getImage());
            }
        };
    }
}
