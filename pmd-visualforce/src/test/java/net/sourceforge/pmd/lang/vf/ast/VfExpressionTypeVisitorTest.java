/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.vf.DataType;
import net.sourceforge.pmd.lang.vf.VFTestUtils;
import net.sourceforge.pmd.util.treeexport.XmlTreeRenderer;

class VfExpressionTypeVisitorTest {
    private static final Map<String, DataType> EXPECTED_CUSTOM_FIELD_DATA_TYPES;
    private static final Map<String, DataType> EXPECTED_APEX_DATA_TYPES;

    static {
        EXPECTED_CUSTOM_FIELD_DATA_TYPES = new HashMap<>();
        EXPECTED_CUSTOM_FIELD_DATA_TYPES.put("CreatedDate", DataType.DateTime);
        EXPECTED_CUSTOM_FIELD_DATA_TYPES.put("DateTime__c", DataType.DateTime);
        EXPECTED_CUSTOM_FIELD_DATA_TYPES.put("Checkbox__c", DataType.Checkbox);
        EXPECTED_CUSTOM_FIELD_DATA_TYPES.put("Name", DataType.Text);
        EXPECTED_CUSTOM_FIELD_DATA_TYPES.put("Text__c", DataType.Text);
        EXPECTED_CUSTOM_FIELD_DATA_TYPES.put("TextArea__c", DataType.TextArea);
        EXPECTED_CUSTOM_FIELD_DATA_TYPES.put("LongTextArea__c", DataType.LongTextArea);
        EXPECTED_CUSTOM_FIELD_DATA_TYPES.put("Picklist__c", DataType.Picklist);

        EXPECTED_APEX_DATA_TYPES = new HashMap<>();
        EXPECTED_APEX_DATA_TYPES.put("AccountIdProp", DataType.Lookup);
        EXPECTED_APEX_DATA_TYPES.put("AccountId", DataType.Lookup);
        EXPECTED_APEX_DATA_TYPES.put("InnerAccountId", DataType.Lookup);
        EXPECTED_APEX_DATA_TYPES.put("InnerAccountIdProp", DataType.Lookup);
        EXPECTED_APEX_DATA_TYPES.put("AccountName", DataType.Text);
        EXPECTED_APEX_DATA_TYPES.put("InnerAccountName", DataType.Text);
        EXPECTED_APEX_DATA_TYPES.put("ConflictingProp", DataType.Unknown);
    }

    /**
     * Strings that use dot notation(Account.CreatedDate) result in ASTIdentifier nodes
     */
    @Test
    void testXpathQueryForCustomFieldIdentifiers() {
        Node rootNode = compile("StandardAccount.page");

        for (Map.Entry<String, DataType> entry : EXPECTED_CUSTOM_FIELD_DATA_TYPES.entrySet()) {
            List<ASTIdentifier> nodes = getIdentifiers(rootNode, entry);

            // Each string appears twice, it is set on a "value" attribute and inline
            assertEquals(2, nodes.size(), entry.getKey());
            for (Node node : nodes) {
                assertEquals(entry.getKey(), node.getImage());
                assertTrue(node instanceof ASTIdentifier, node.getClass().getSimpleName());
                ASTIdentifier identifier = (ASTIdentifier) node;
                assertEquals(entry.getValue(), identifier.getDataType(), entry.getKey());
            }
        }
    }

    /**
     * Strings that use array notation, Account['CreatedDate') don't have a DataType added. This type of notation
     * creates ambiguous situations with Apex methods that return Maps. This may be addressed in a future release.
     */
    @Test
    void testXpathQueryForCustomFieldLiteralsHaveNullDataType() {
        Node rootNode = compile("StandardAccount.page");

        for (Map.Entry<String, DataType> entry : EXPECTED_CUSTOM_FIELD_DATA_TYPES.entrySet()) {
            List<ASTLiteral> nodes = rootNode.descendants(ASTLiteral.class)
                                             // Literals are surrounded by apostrophes
                                             .filterMatching(ASTLiteral::getImage, "'" + entry.getKey() + "'")
                                             .filterMatching(ASTLiteral::getDataType, null)
                                             .toList();

            // Each string appears twice, it is set on a "value" attribute and inline
            assertEquals(2, nodes.size(), entry.getKey());
            for (Node node : nodes) {
                assertEquals(String.format("'%s'", entry.getKey()), node.getImage());
                assertTrue(node instanceof ASTLiteral, node.getClass().getSimpleName());
                ASTLiteral literal = (ASTLiteral) node;
                assertNull(literal.getDataType(), entry.getKey());
            }
        }
    }

    /**
     * Nodes where the DataType can't be determined should have a null DataType
     */
    @Test
    void testDataTypeForCustomFieldsNotFound() {
        Node rootNode = compile("StandardAccount.page");

        checkNodes(rootNode.descendants(ASTIdentifier.class)
                           .filterMatching(ASTIdentifier::getImage, "NotFoundField__c"));
        checkNodes(rootNode.descendants(ASTLiteral.class)
                           .filterMatching(ASTLiteral::getImage, "'NotFoundField__c'"));
    }

    private void checkNodes(NodeStream<? extends VfTypedNode> nodeStream) {
        // Each string appears twice, it is set on a "value" attribute and inline
        List<? extends VfTypedNode> nodes = nodeStream.toList();
        assertEquals(2, nodes.size());
        for (VfTypedNode node : nodes) {
            assertNull(node.getDataType());
        }
    }

    /**
     * Apex properties result in ASTIdentifier nodes
     */
    @Test
    void testXpathQueryForProperties() {
        Node rootNode = compile("ApexController.page");

        for (Map.Entry<String, DataType> entry : EXPECTED_APEX_DATA_TYPES.entrySet()) {
            List<ASTIdentifier> nodes = getIdentifiers(rootNode, entry);

            // Each string appears twice, it is set on a "value" attribute and inline
            assertEquals(2, nodes.size(), entry.getKey());
            for (Node node : nodes) {
                assertEquals(entry.getKey(), node.getImage());
                assertTrue(node instanceof ASTIdentifier, node.getClass().getSimpleName());
                ASTIdentifier identifier = (ASTIdentifier) node;
                assertEquals(entry.getValue(), identifier.getDataType(), entry.getKey());
            }
        }
    }

    private List<ASTIdentifier> getIdentifiers(Node rootNode, Entry<String, DataType> entry) {
        return rootNode.descendants(ASTIdentifier.class)
                       .filterMatching(ASTIdentifier::getImage, entry.getKey())
                       .filterMatching(ASTIdentifier::getDataType, entry.getValue())
                       .toList();
    }

    /**
     * Nodes where the DataType can't be determined should have a null DataType
     */
    @Test
    void testDataTypeForApexPropertiesNotFound() {
        Node rootNode = compile("ApexController.page");

        // Each string appears twice, it is set on a "value" attribute and inline
        checkNodes(rootNode.descendants(ASTIdentifier.class)
                           .filterMatching(ASTIdentifier::getImage, "NotFoundProp"));
    }

    private Node compile(String pageName) {
        return compile(pageName, false);
    }

    private Node compile(String pageName, boolean renderAST) {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Vf)
                .resolve(pageName);
        return compile(vfPagePath, renderAST);
    }

    private Node compile(Path vfPagePath, boolean renderAST) {
        Node node = VfParsingHelper.DEFAULT.parseFile(vfPagePath);
        assertNotNull(node);

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
