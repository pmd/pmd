/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.treeexport.XmlTreeRenderer;

class ASTExpressionTest {

    /**
     * Slightly different scenarios which cause different AST, but should return the same results.
     */
    private static final String[] SNIPPET_TEMPLATES = new String[] {
        "{!%s}",
        "<apex:outputText value=\"{!%s}\" escape=\"false\"/>",
        "<script>function someFunc() {var foo = \"{!%s}\";}</script>"};

    @Test
    void testExpressionWithApexGetter() throws ASTExpression.DataNodeStateException {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyValue"));

            List<Node> nodes = getExpressions(compilationUnit);
            assertEquals(1, nodes.size(), template);

            ASTExpression expression = (ASTExpression) nodes.get(0);
            Map<VfTypedNode, String> identifiers = expression.getDataNodes();
            assertEquals(1, identifiers.size(), template);

            Map<String, Node> map = invertMap(identifiers);
            assertTrue(map.containsKey("MyValue"), template);
            assertTrue(map.get("MyValue") instanceof ASTIdentifier, template);
        }
    }

    @Test
    void testExpressionWithStandardController() throws ASTExpression.DataNodeStateException {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyObject__c.Text__c"));

            List<Node> nodes = getExpressions(compilationUnit);
            assertEquals(1, nodes.size(), template);

            ASTExpression expression = (ASTExpression) nodes.get(0);
            Map<VfTypedNode, String> identifiers = expression.getDataNodes();
            assertEquals(1, identifiers.size(), template);

            Map<String, Node> map = invertMap(identifiers);
            assertTrue(map.containsKey("MyObject__c.Text__c"), template);
            assertTrue(map.get("MyObject__c.Text__c") instanceof ASTIdentifier, template);
        }
    }

    @Test
    void testSelectOptions() throws ASTExpression.DataNodeStateException {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "userOptions.0"));

            List<Node> nodes = getExpressions(compilationUnit);
            assertEquals(1, nodes.size(), template);

            ASTExpression expression = (ASTExpression) nodes.get(0);
            Map<VfTypedNode, String> identifiers = expression.getDataNodes();
            assertEquals(1, identifiers.size(), template);

            Map<String, Node> map = invertMap(identifiers);
            assertTrue(map.containsKey("userOptions.0"), template);
            assertTrue(map.get("userOptions.0") instanceof ASTLiteral, template);
        }
    }

    @Test
    void testMultipleIdentifiers() throws ASTExpression.DataNodeStateException {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyObject__c.Text__c + ' this is a string' + MyObject__c.Text2__c"));

            List<Node> nodes = getExpressions(compilationUnit);
            assertEquals(1, nodes.size(), template);

            ASTExpression expression = (ASTExpression) nodes.get(0);
            Map<VfTypedNode, String> identifiers = expression.getDataNodes();
            assertEquals(2, identifiers.size(), template);

            Map<String, Node> map = invertMap(identifiers);
            assertEquals(2, map.size(), template);
            assertTrue(map.containsKey("MyObject__c.Text__c"), template);
            assertTrue(map.get("MyObject__c.Text__c") instanceof ASTIdentifier, template);
            assertTrue(map.containsKey("MyObject__c.Text2__c"), template);
            assertTrue(map.get("MyObject__c.Text2__c") instanceof ASTIdentifier, template);
        }
    }

    @Test
    void testIdentifierWithRelation() throws ASTExpression.DataNodeStateException {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyObject1__c.MyObject2__r.Text__c"));

            List<Node> nodes = getExpressions(compilationUnit);
            assertEquals(1, nodes.size(), template);

            ASTExpression expression = (ASTExpression) nodes.get(0);
            Map<VfTypedNode, String> identifiers = expression.getDataNodes();
            assertEquals(1, identifiers.size(), template);

            Map<String, Node> map = invertMap(identifiers);
            assertEquals(1, map.size(), template);
            assertTrue(map.containsKey("MyObject1__c.MyObject2__r.Text__c"), template);
            assertTrue(map.get("MyObject1__c.MyObject2__r.Text__c") instanceof ASTIdentifier, template);
        }
    }

    @Test
    void testMultipleIdentifiersWithRelation() throws ASTExpression.DataNodeStateException {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyObject1__c.MyObject2__r.Text__c + ' this is a string' + MyObject1__c.MyObject2__r.Text2__c"));

            List<Node> nodes = getExpressions(compilationUnit);
            assertEquals(1, nodes.size(), template);

            ASTExpression expression = (ASTExpression) nodes.get(0);
            Map<VfTypedNode, String> identifiers = expression.getDataNodes();
            assertEquals(2, identifiers.size(), template);

            Map<String, Node> map = invertMap(identifiers);
            assertEquals(2, map.size(), template);
            assertTrue(map.containsKey("MyObject1__c.MyObject2__r.Text__c"), template);
            assertTrue(map.get("MyObject1__c.MyObject2__r.Text__c") instanceof ASTIdentifier, template);
            assertTrue(map.containsKey("MyObject1__c.MyObject2__r.Text2__c"), template);
            assertTrue(map.get("MyObject1__c.MyObject2__r.Text2__c") instanceof ASTIdentifier, template);
        }
    }

    /**
     * The current implementation does not support expressing statements using array notation. This notation introduces
     * complexities that may be addressed in a future release.
     */
    @Test
    void testExpressionWithArrayIndexingNotSupported() {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyObject__c['Name']"));

            List<Node> nodes = getExpressions(compilationUnit);
            assertEquals(2, nodes.size(), template);

            ASTExpression expression = (ASTExpression) nodes.get(0);
            try {
                expression.getDataNodes();
                fail(template + " should have thrown");
            } catch (ASTExpression.DataNodeStateException expected) {
                // Intentionally left blank
            }
        }
    }

    @Test
    void testIdentifierWithRelationIndexedAsArrayNotSupported() {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyObject1__c['MyObject2__r'].Text__c"));

            List<Node> nodes = getExpressions(compilationUnit);
            assertEquals(2, nodes.size(), template);

            ASTExpression expression = (ASTExpression) nodes.get(0);
            try {
                expression.getDataNodes();
                fail(template + " should have thrown");
            } catch (ASTExpression.DataNodeStateException expected) {
                // Intentionally left blank
            }
        }
    }

    @Test
    void testIdentifierWithComplexIndexedArrayNotSupported() {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "theLineItems[item.Id].UnitPrice"));

            List<Node> nodes = getExpressions(compilationUnit);
            assertEquals(2, nodes.size(), template);

            ASTExpression expression = (ASTExpression) nodes.get(0);
            try {
                expression.getDataNodes();
                fail(template + " should have thrown");
            } catch (ASTExpression.DataNodeStateException expected) {
                // Intentionally left blank
            }
        }
    }

    private static List<Node> getExpressions(ASTCompilationUnit compilationUnit) {
        return compilationUnit.descendants(ASTExpression.class).toList(it -> it);
    }

    /**
     * Invert the map to make it easier to unit test.
     */
    private Map<String, Node> invertMap(Map<VfTypedNode, String> map) {
        Map<String, Node> result = map.entrySet().stream()
                                      .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        // Ensure no values have been lost
        assertEquals(map.size(), result.size());
        return result;
    }

    private ASTCompilationUnit compile(String snippet) {
        return compile(snippet, false);
    }

    private ASTCompilationUnit compile(String snippet, boolean renderAST) {
        ASTCompilationUnit node = VfParsingHelper.DEFAULT.parse(
            "<apex:page>"
                + snippet
                + "</apex:page>"
        );

        if (renderAST) {
            try {
                new XmlTreeRenderer().renderSubtree(node, System.out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return node;
    }
}
