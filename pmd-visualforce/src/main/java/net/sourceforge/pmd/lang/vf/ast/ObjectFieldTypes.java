/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.lang.vf.DataType;

/**
 * Responsible for storing a mapping of Fields that can be referenced from Visualforce to the type of the field.
 */
class ObjectFieldTypes extends SalesforceFieldTypes {
    private static final Logger LOGGER = Logger.getLogger(ObjectFieldTypes.class.getName());

    public static final String CUSTOM_OBJECT_SUFFIX = "__c";
    private static final String FIELDS_DIRECTORY = "fields";
    private static final String MDAPI_OBJECT_FILE_SUFFIX = ".object";
    private static final String SFDX_FIELD_FILE_SUFFIX = ".field-meta.xml";

    private static final Map<String, DataType> STANDARD_FIELD_TYPES;

    static {
        STANDARD_FIELD_TYPES = new HashMap<>();
        STANDARD_FIELD_TYPES.put("createdbyid", DataType.Lookup);
        STANDARD_FIELD_TYPES.put("createddate", DataType.DateTime);
        STANDARD_FIELD_TYPES.put("id", DataType.Lookup);
        STANDARD_FIELD_TYPES.put("isdeleted", DataType.Checkbox);
        STANDARD_FIELD_TYPES.put("lastmodifiedbyid", DataType.Lookup);
        STANDARD_FIELD_TYPES.put("lastmodifieddate", DataType.DateTime);
        STANDARD_FIELD_TYPES.put("name", DataType.Text);
        STANDARD_FIELD_TYPES.put("systemmodstamp", DataType.DateTime);
    }

    /**
     * Keep track of which ".object" files have already been processed. All fields are processed at once. If an object
     * file has been processed
     */
    private final Set<String> objectFileProcessed;

    // XML Parsing objects
    private final DocumentBuilder documentBuilder;
    private final XPathExpression customObjectFieldsExpression;
    private final XPathExpression customFieldFullNameExpression;
    private final XPathExpression customFieldTypeExpression;
    private final XPathExpression sfdxCustomFieldFullNameExpression;
    private final XPathExpression sfdxCustomFieldTypeExpression;

    ObjectFieldTypes() {
        this.objectFileProcessed = new HashSet<>();

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(false);
            documentBuilderFactory.setValidating(false);
            documentBuilderFactory.setIgnoringComments(true);
            documentBuilderFactory.setIgnoringElementContentWhitespace(true);
            documentBuilderFactory.setExpandEntityReferences(false);
            documentBuilderFactory.setCoalescing(false);
            documentBuilderFactory.setXIncludeAware(false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            this.customObjectFieldsExpression = xPath.compile("/CustomObject/fields");
            this.customFieldFullNameExpression = xPath.compile("fullName/text()");
            this.customFieldTypeExpression = xPath.compile("type/text()");
            this.sfdxCustomFieldFullNameExpression = xPath.compile("/CustomField/fullName/text()");
            this.sfdxCustomFieldTypeExpression = xPath.compile("/CustomField/type/text()");
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Looks in {@code objectsDirectories} for a custom field identified by {@code expression}.
     */
    @Override
    protected void findDataType(String expression, List<Path> objectsDirectories) {
        // The expression should be in the form <objectName>.<fieldName>
        String[] parts = expression.split("\\.");
        if (parts.length == 1) {
            throw new RuntimeException("Malformed identifier: " + expression);
        } else if (parts.length == 2) {
            String objectName = parts[0];
            String fieldName = parts[1];

            addStandardFields(objectName);

            // Attempt to find a metadata file that contains the custom field. The information will be located in a
            // file located at <objectDirectory>/<objectName>.object or in an file located at
            // <objectDirectory>/<objectName>/fields/<fieldName>.field-meta.xml. The list of object directories
            // defaults to the [<vfFileName>/../objects] but can be overridden by the user.
            for (Path objectsDirectory : objectsDirectories) {
                Path sfdxCustomFieldPath = getSfdxCustomFieldPath(objectsDirectory, objectName, fieldName);
                if (sfdxCustomFieldPath != null) {
                    // SFDX Format
                    parseSfdxCustomField(objectName, sfdxCustomFieldPath);
                } else {
                    // MDAPI Format
                    String fileName = objectName + MDAPI_OBJECT_FILE_SUFFIX;
                    Path mdapiPath = objectsDirectory.resolve(fileName);
                    if (Files.exists(mdapiPath) && Files.isRegularFile(mdapiPath)) {
                        parseMdapiCustomObject(mdapiPath);
                    }
                }

                if (containsExpression(expression)) {
                    // Break out of the loop if a variable was found
                    break;
                }
            }
        } else {
            // TODO: Support cross object relationships, these are expressions that contain "__r"
            LOGGER.fine("Expression does not have two parts: " + expression);
        }
    }

    /**
     * Sfdx projects decompose custom fields into individual files. This method will return the individual file that
     * corresponds to &lt;objectName&gt;.&lt;fieldName&gt; if it exists.
     *
     * @return path to the metadata file for the Custom Field or null if not found
     */
    private Path getSfdxCustomFieldPath(Path objectsDirectory, String objectName, String fieldName) {
        Path fieldsDirectoryPath = Paths.get(objectsDirectory.toString(), objectName, FIELDS_DIRECTORY);
        if (Files.exists(fieldsDirectoryPath) && Files.isDirectory(fieldsDirectoryPath)) {
            Path sfdxFieldPath = Paths.get(fieldsDirectoryPath.toString(), fieldName + SFDX_FIELD_FILE_SUFFIX);
            if (Files.exists(sfdxFieldPath) && Files.isRegularFile(sfdxFieldPath)) {
                return sfdxFieldPath;
            }
        }
        return null;
    }

    /**
     * Determine the type of the custom field.
     */
    private void parseSfdxCustomField(String customObjectName, Path sfdxCustomFieldPath) {
        try {
            Document document = documentBuilder.parse(sfdxCustomFieldPath.toFile());
            Node fullNameNode = (Node) sfdxCustomFieldFullNameExpression.evaluate(document, XPathConstants.NODE);
            Node typeNode = (Node) sfdxCustomFieldTypeExpression.evaluate(document, XPathConstants.NODE);
            String type = typeNode.getNodeValue();
            DataType dataType = DataType.fromString(type);

            String key = customObjectName + "." + fullNameNode.getNodeValue();
            putDataType(key, dataType);
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new ContextedRuntimeException(e)
                    .addContextValue("customObjectName", customObjectName)
                    .addContextValue("sfdxCustomFieldPath", sfdxCustomFieldPath);
        }
    }

    /**
     * Parse the custom object path and determine the type of all of its custom fields.
     */
    private void parseMdapiCustomObject(Path mdapiObjectFile) {
        String fileName = mdapiObjectFile.getFileName().toString();

        String customObjectName = fileName.substring(0, fileName.lastIndexOf(MDAPI_OBJECT_FILE_SUFFIX));
        if (!objectFileProcessed.contains(customObjectName)) {
            try {
                Document document = documentBuilder.parse(mdapiObjectFile.toFile());
                NodeList fieldsNodes = (NodeList) customObjectFieldsExpression.evaluate(document, XPathConstants.NODESET);
                for (int i = 0; i < fieldsNodes.getLength(); i++) {
                    Node fieldsNode = fieldsNodes.item(i);
                    Node fullNameNode = (Node) customFieldFullNameExpression.evaluate(fieldsNode, XPathConstants.NODE);
                    if (fullNameNode == null) {
                        throw new RuntimeException("fullName evaluate failed for " + customObjectName + " " + fieldsNode.getTextContent());
                    }
                    String name = fullNameNode.getNodeValue();
                    if (endsWithIgnoreCase(name, CUSTOM_OBJECT_SUFFIX)) {
                        Node typeNode = (Node) customFieldTypeExpression.evaluate(fieldsNode, XPathConstants.NODE);
                        if (typeNode == null) {
                            throw new RuntimeException("type evaluate failed for object=" + customObjectName + ", field=" + name + " " + fieldsNode.getTextContent());
                        }
                        String type = typeNode.getNodeValue();
                        DataType dataType = DataType.fromString(type);
                        String key = customObjectName + "." + fullNameNode.getNodeValue();
                        putDataType(key, dataType);
                    }
                }
            } catch (IOException | SAXException | XPathExpressionException e) {
                throw new ContextedRuntimeException(e)
                        .addContextValue("customObjectName", customObjectName)
                        .addContextValue("mdapiObjectFile", mdapiObjectFile);
            }
            objectFileProcessed.add(customObjectName);
        }
    }

    /**
     * Add the set of standard fields which aren't present in the metadata file, but may be refernced from the
     * visualforce page.
     */
    private void addStandardFields(String customObjectName) {
        for (Map.Entry<String, DataType> entry : STANDARD_FIELD_TYPES.entrySet()) {
            putDataType(customObjectName + "." + entry.getKey(), entry.getValue());
        }
    }

    /**
     * Null safe endsWithIgnoreCase
     */
    private boolean endsWithIgnoreCase(String str, String suffix) {
        return str != null && str.toLowerCase(Locale.ROOT).endsWith(suffix.toLowerCase(Locale.ROOT));
    }

    @Override
    protected DataType putDataType(String name, DataType dataType) {
        DataType previousType = super.putDataType(name, dataType);
        if (previousType != null && !previousType.equals(dataType)) {
            // It should not be possible to have conflicting types for CustomFields
            throw new RuntimeException("Conflicting types for "
                    + name
                    + ". CurrentType="
                    + dataType
                    + ", PreviousType="
                    + previousType);
        }
        return previousType;
    }
}
