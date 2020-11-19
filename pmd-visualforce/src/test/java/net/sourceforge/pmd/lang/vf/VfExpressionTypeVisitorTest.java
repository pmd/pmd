/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.vf.ast.ASTIdentifier;
import net.sourceforge.pmd.lang.vf.ast.ASTLiteral;
import net.sourceforge.pmd.lang.vf.ast.AbstractVFDataNode;
import net.sourceforge.pmd.util.treeexport.XmlTreeRenderer;

public class VfExpressionTypeVisitorTest {
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
    public void testXpathQueryForCustomFieldIdentifiers() throws FileNotFoundException {
        Node rootNode = compile("StandardAccount.page");

        for (Map.Entry<String, DataType> entry : EXPECTED_CUSTOM_FIELD_DATA_TYPES.entrySet()) {
            String xpath = String.format("//Identifier[@Image='%s' and @DataType='%s']", entry.getKey(), entry.getValue().name());
            List<Node> nodes = VFTestUtils.findNodes(rootNode, xpath);
            // Each string appears twice, it is set on a "value" attribute and inline
            assertEquals(entry.getKey(), 2, nodes.size());
            for (Node node : nodes) {
                assertEquals(entry.getKey(), node.getImage());
                assertTrue(node.getClass().getSimpleName(), node instanceof ASTIdentifier);
                ASTIdentifier identifier = (ASTIdentifier) node;
                assertEquals(entry.getKey(), entry.getValue(), identifier.getDataType());
            }
        }
    }

    /**
     * Strings that use array notation, Account['CreatedDate') don't have a DataType added. This type of notation
     * creates ambiguous situations with Apex methods that return Maps. This may be addressed in a future release.
     */
    @Test
    public void testXpathQueryForCustomFieldLiteralsHaveNullDataType() throws FileNotFoundException {
        Node rootNode = compile("StandardAccount.page");

        for (Map.Entry<String, DataType> entry : EXPECTED_CUSTOM_FIELD_DATA_TYPES.entrySet()) {
            // Literals are surrounded by apostrophes
            String xpath = String.format("//Literal[@Image=\"'%s'\" and @DataType='']", entry.getKey());
            List<Node> nodes = VFTestUtils.findNodes(rootNode, xpath);
            // Each string appears twice, it is set on a "value" attribute and inline
            assertEquals(entry.getKey(), 2, nodes.size());
            for (Node node : nodes) {
                assertEquals(String.format("'%s'", entry.getKey()), node.getImage());
                assertTrue(node.getClass().getSimpleName(), node instanceof ASTLiteral);
                ASTLiteral literal = (ASTLiteral) node;
                assertEquals(entry.getKey(), null, literal.getDataType());
            }
        }
    }

    /**
     * Nodes where the DataType can't be determined should have a null DataType
     */
    @Test
    public void testDataTypeForCustomFieldsNotFound() throws FileNotFoundException {
        Node rootNode = compile("StandardAccount.page");

        for (String xpath : new String[] { "//Identifier[@Image='NotFoundField__c']", "//Literal[@Image=\"'NotFoundField__c'\"]" }) {
            List<Node> nodes = VFTestUtils.findNodes(rootNode, xpath);
            // Each string appears twice, it is set on a "value" attribute and inline
            assertEquals(2, nodes.size());
            for (Node node : nodes) {
                assertTrue(node.getClass().getSimpleName(), node instanceof AbstractVFDataNode);
                AbstractVFDataNode dataNode = (AbstractVFDataNode) node;
                assertNull(dataNode.getDataType());
            }
        }
    }

    /**
     * Apex properties result in ASTIdentifier nodes
     */
    @Test
    public void testXpathQueryForProperties() throws FileNotFoundException {
        Node rootNode = compile("ApexController.page");

        for (Map.Entry<String, DataType> entry : EXPECTED_APEX_DATA_TYPES.entrySet()) {
            String xpath = String.format("//Identifier[@Image='%s' and @DataType='%s']", entry.getKey(), entry.getValue().name());
            List<Node> nodes = VFTestUtils.findNodes(rootNode, xpath);
            // Each string appears twice, it is set on a "value" attribute and inline
            assertEquals(entry.getKey(), 2, nodes.size());
            for (Node node : nodes) {
                assertEquals(entry.getKey(), node.getImage());
                assertTrue(node.getClass().getSimpleName(), node instanceof ASTIdentifier);
                ASTIdentifier identifier = (ASTIdentifier) node;
                assertEquals(entry.getKey(), entry.getValue(), identifier.getDataType());
            }
        }
    }

    /**
     * Nodes where the DataType can't be determined should have a null DataType
     */
    @Test
    public void testDataTypeForApexPropertiesNotFound() throws FileNotFoundException {
        Node rootNode = compile("ApexController.page");

        String xpath = "//Identifier[@Image='NotFoundProp']";
        List<Node> nodes = VFTestUtils.findNodes(rootNode, xpath);
        // Each string appears twice, it is set on a "value" attribute and inline
        assertEquals(2, nodes.size());
        for (Node node : nodes) {
            assertTrue(node.getClass().getSimpleName(), node instanceof AbstractVFDataNode);
            AbstractVFDataNode dataNode = (AbstractVFDataNode) node;
            assertNull(dataNode.getDataType());
        }
    }

    private Node compile(String pageName) throws FileNotFoundException {
        return compile(pageName, false);
    }

    private Node compile(String pageName, boolean renderAST) throws FileNotFoundException {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Vf)
                .resolve(pageName);
        return compile(vfPagePath, renderAST);
    }

    private Node compile(Path vfPagePath, boolean renderAST) throws FileNotFoundException {
        LanguageVersion languageVersion = LanguageRegistry.getLanguage(VfLanguageModule.NAME).getDefaultVersion();
        ParserOptions parserOptions = languageVersion.getLanguageVersionHandler().getDefaultParserOptions();
        Parser parser = languageVersion.getLanguageVersionHandler().getParser(parserOptions);

        Node node = parser.parse(vfPagePath.toString(), new FileReader(vfPagePath.toFile()));
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
