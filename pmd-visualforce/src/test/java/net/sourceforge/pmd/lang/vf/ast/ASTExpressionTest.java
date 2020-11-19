/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.SimpleCharStream;
import net.sourceforge.pmd.lang.vf.VFTestUtils;
import net.sourceforge.pmd.util.treeexport.XmlTreeRenderer;

public class ASTExpressionTest {
    /**
     * Slightly different scenarios which cause different AST, but should return the same results.
     */
    private static final String[] SNIPPET_TEMPLATES = new String[] {
        "{!%s}",
        "<apex:outputText value=\"{!%s}\" escape=\"false\"/>",
        "<script>function someFunc() {var foo = \"{!%s}\";}</script>" };

    @Test
    public void testExpressionWithApexGetter() throws ASTExpression.DataNodeStateException {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyValue"));

            List<Node> nodes = VFTestUtils.findNodes(compilationUnit, "//Expression");
            assertEquals(template, 1, nodes.size());

            ASTExpression expression = (ASTExpression) nodes.get(0);
            Map<AbstractVFDataNode, String> identifiers = expression.getDataNodes();
            assertEquals(template, 1, identifiers.size());

            Map<String, Node> map = invertMap(identifiers);
            assertTrue(template, map.containsKey("MyValue"));
            assertTrue(template, map.get("MyValue") instanceof ASTIdentifier);
        }
    }

    @Test
    public void testExpressionWithStandardController() throws ASTExpression.DataNodeStateException {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyObject__c.Text__c"));

            List<Node> nodes = VFTestUtils.findNodes(compilationUnit, "//Expression");
            assertEquals(template, 1, nodes.size());

            ASTExpression expression = (ASTExpression) nodes.get(0);
            Map<AbstractVFDataNode, String> identifiers = expression.getDataNodes();
            assertEquals(template, 1, identifiers.size());

            Map<String, Node> map = invertMap(identifiers);
            assertTrue(template, map.containsKey("MyObject__c.Text__c"));
            assertTrue(template, map.get("MyObject__c.Text__c") instanceof ASTIdentifier);
        }
    }

    @Test
    public void testSelectOptions() throws ASTExpression.DataNodeStateException {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "userOptions.0"));

            List<Node> nodes = VFTestUtils.findNodes(compilationUnit, "//Expression");
            assertEquals(template, 1, nodes.size());

            ASTExpression expression = (ASTExpression) nodes.get(0);
            Map<AbstractVFDataNode, String> identifiers = expression.getDataNodes();
            assertEquals(template, 1, identifiers.size());

            Map<String, Node> map = invertMap(identifiers);
            assertTrue(template, map.containsKey("userOptions.0"));
            assertTrue(template, map.get("userOptions.0") instanceof ASTLiteral);
        }
    }

    @Test
    public void testMultipleIdentifiers() throws ASTExpression.DataNodeStateException {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyObject__c.Text__c + ' this is a string' + MyObject__c.Text2__c"));

            List<Node> nodes = VFTestUtils.findNodes(compilationUnit, "//Expression");
            assertEquals(template, 1, nodes.size());

            ASTExpression expression = (ASTExpression) nodes.get(0);
            Map<AbstractVFDataNode, String> identifiers = expression.getDataNodes();
            assertEquals(template, 2, identifiers.size());

            Map<String, Node> map = invertMap(identifiers);
            assertEquals(template, 2, map.size());
            assertTrue(template, map.containsKey("MyObject__c.Text__c"));
            assertTrue(template, map.get("MyObject__c.Text__c") instanceof ASTIdentifier);
            assertTrue(template, map.containsKey("MyObject__c.Text2__c"));
            assertTrue(template, map.get("MyObject__c.Text2__c") instanceof ASTIdentifier);
        }
    }

    @Test
    public void testIdentifierWithRelation() throws ASTExpression.DataNodeStateException {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyObject1__c.MyObject2__r.Text__c"));

            List<Node> nodes = VFTestUtils.findNodes(compilationUnit, "//Expression");
            assertEquals(template, 1, nodes.size());

            ASTExpression expression = (ASTExpression) nodes.get(0);
            Map<AbstractVFDataNode, String> identifiers = expression.getDataNodes();
            assertEquals(template, 1, identifiers.size());

            Map<String, Node> map = invertMap(identifiers);
            assertEquals(template, 1, map.size());
            assertTrue(template, map.containsKey("MyObject1__c.MyObject2__r.Text__c"));
            assertTrue(template, map.get("MyObject1__c.MyObject2__r.Text__c") instanceof ASTIdentifier);
        }
    }

    @Test
    public void testMultipleIdentifiersWithRelation() throws ASTExpression.DataNodeStateException {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyObject1__c.MyObject2__r.Text__c + ' this is a string' + MyObject1__c.MyObject2__r.Text2__c"));

            List<Node> nodes = VFTestUtils.findNodes(compilationUnit, "//Expression");
            assertEquals(template, 1, nodes.size());

            ASTExpression expression = (ASTExpression) nodes.get(0);
            Map<AbstractVFDataNode, String> identifiers = expression.getDataNodes();
            assertEquals(template, 2, identifiers.size());

            Map<String, Node> map = invertMap(identifiers);
            assertEquals(template, 2, map.size());
            assertTrue(template, map.containsKey("MyObject1__c.MyObject2__r.Text__c"));
            assertTrue(template, map.get("MyObject1__c.MyObject2__r.Text__c") instanceof ASTIdentifier);
            assertTrue(template, map.containsKey("MyObject1__c.MyObject2__r.Text2__c"));
            assertTrue(template, map.get("MyObject1__c.MyObject2__r.Text2__c") instanceof ASTIdentifier);
        }
    }

    /**
     * The current implementation does not support expressing statements using array notation. This notation introduces
     * complexities that may be addressed in a future release.
     */
    @Test
    public void testExpressionWithArrayIndexingNotSupported() {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyObject__c['Name']"));

            List<Node> nodes = VFTestUtils.findNodes(compilationUnit, "//Expression");
            assertEquals(template, 2, nodes.size());

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
    public void testIdentifierWithRelationIndexedAsArrayNotSupported() {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "MyObject1__c['MyObject2__r'].Text__c"));

            List<Node> nodes = VFTestUtils.findNodes(compilationUnit, "//Expression");
            assertEquals(template, 2, nodes.size());

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
    public void testIdentifierWithComplexIndexedArrayNotSupported() {
        for (String template : SNIPPET_TEMPLATES) {
            ASTCompilationUnit compilationUnit = compile(String.format(template, "theLineItems[item.Id].UnitPrice"));

            List<Node> nodes = VFTestUtils.findNodes(compilationUnit, "//Expression");
            assertEquals(template, 2, nodes.size());

            ASTExpression expression = (ASTExpression) nodes.get(0);
            try {
                expression.getDataNodes();
                fail(template + " should have thrown");
            } catch (ASTExpression.DataNodeStateException expected) {
                // Intentionally left blank
            }
        }
    }

    /**
     * Invert the map to make it easier to unit test.
     */
    private Map<String, Node> invertMap(Map<AbstractVFDataNode, String> map) {
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
        ASTCompilationUnit node = new net.sourceforge.pmd.lang.vf.ast.VfParser(
                new SimpleCharStream(new StringReader("<apex:page>"
                        + snippet
                        + "</apex:page>"))).CompilationUnit();

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
