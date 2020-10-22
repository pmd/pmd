/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Responsible for storing a mapping of Fields that can be referenced from Visualforce to the type of the field.
 */
class ObjectFieldTypes {
    private static final Logger LOGGER = Logger.getLogger(ObjectFieldTypes.class.getName());

    public static final String CUSTOM_OBJECT_SUFFIX = "__c";
    private static final String FIELDS_DIRECTORY = "fields";
    private static final String MDAPI_OBJECT_FILE_SUFFIX = ".object";
    private static final String SFDX_FIELD_FILE_SUFFIX = ".field-meta.xml";

    private static final ImmutableMap<String, ExpressionType> STANDARD_FIELD_TYPES =
            ImmutableMap.<String, ExpressionType>builder()
                    .put("createdbyid", ExpressionType.Lookup)
                    .put("createddate", ExpressionType.DateTime)
                    .put("id", ExpressionType.Lookup)
                    .put("isdeleted", ExpressionType.Checkbox)
                    .put("lastmodifiedbyid", ExpressionType.Lookup)
                    .put("lastmodifieddate", ExpressionType.DateTime)
                    .put("systemmodstamp", ExpressionType.DateTime)
                    .build();

    /**
     * Cache of lowercase variable names to the variable type declared in the field's metadata file.
     */
    private final ConcurrentHashMap<String, ExpressionType> variableNameToVariableType;

    /**
     * Keep track of which variables were already processed. Avoid processing if a page repeatedly asks for an entry
     * which we haven't previously found.
     */
    private final Set<String> variableNameProcessed;

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
        this.variableNameToVariableType = new ConcurrentHashMap<>();
        this.variableNameProcessed = Sets.newConcurrentHashSet();
        this.objectFileProcessed = Sets.newConcurrentHashSet();

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
     *
     * @return the ExpressionType for the field represented by {@code expression} or null the custom field isn't found.
     */
    public ExpressionType getVariableType(String expression, String vfFileName, List<String> objectsDirectories) {
        String lowerExpression = expression.toLowerCase(Locale.ROOT);

        if (variableNameToVariableType.containsKey(lowerExpression)) {
            // The expression has been previously retrieved
            return variableNameToVariableType.get(lowerExpression);
        } else if (variableNameProcessed.contains(lowerExpression)) {
            // The expression has been previously requested, but was not found
            return null;
        } else {
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
                Path vfFilePath = Paths.get(vfFileName);
                for (String objectsDirectory : objectsDirectories) {
                    Path candidateDirectory;
                    if (Paths.get(objectsDirectory).isAbsolute()) {
                        candidateDirectory = Paths.get(objectsDirectory);
                    } else {
                        candidateDirectory = vfFilePath.getParent().resolve(objectsDirectory);
                    }

                    Path sfdxCustomFieldPath = getSfdxCustomFieldPath(candidateDirectory, objectName, fieldName);
                    if (sfdxCustomFieldPath != null) {
                        // SFDX Format
                        parseSfdxCustomField(objectName, sfdxCustomFieldPath);
                    } else {
                        // MDAPI Format
                        String fileName = objectName + MDAPI_OBJECT_FILE_SUFFIX;
                        Path mdapiPath = candidateDirectory.resolve(fileName);
                        if (Files.exists(mdapiPath) && Files.isRegularFile(mdapiPath)) {
                            parseMdapiCustomObject(mdapiPath);
                        }
                    }

                    if (variableNameToVariableType.containsKey(lowerExpression)) {
                        // Break out of the loop if a variable was found
                        break;
                    }
                }
                variableNameProcessed.add(lowerExpression);
            } else {
                // TODO: Support cross object relationships, these are expressions that contain "__r"
                LOGGER.fine("Expression does not have two parts: " + expression);
            }
        }

        return variableNameToVariableType.get(lowerExpression);
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
            ExpressionType expressionType = ExpressionType.fromString(type);

            String key = customObjectName + "." + fullNameNode.getNodeValue();
            setVariableType(key, expressionType);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                        ExpressionType expressionType = ExpressionType.fromString(type);
                        String key = customObjectName + "." + fullNameNode.getNodeValue();
                        setVariableType(key, expressionType);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            objectFileProcessed.add(customObjectName);
        }
    }

    /**
     * Add the set of standard fields which aren't present in the metadata file, but may be refernced from the
     * visualforce page.
     */
    private void addStandardFields(String customObjectName) {
        for (Map.Entry<String, ExpressionType> entry : STANDARD_FIELD_TYPES.entrySet()) {
            setVariableType(customObjectName + "." + entry.getKey(), entry.getValue());
        }
    }

    /**
     * Null safe endsWithIgnoreCase
     */
    private boolean endsWithIgnoreCase(String str, String suffix) {
        return str != null && str.toLowerCase(Locale.ROOT).endsWith(suffix.toLowerCase(Locale.ROOT));
    }

    private void setVariableType(String name, ExpressionType expressionType) {
        name = name.toLowerCase(Locale.ROOT);
        ExpressionType previousType = variableNameToVariableType.put(name, expressionType);
        if (previousType != null && !previousType.equals(expressionType)) {
            // It should not be possible ot have conflicting types for CustomFields
            throw new RuntimeException("Conflicting types for "
                    + name
                    + ". CurrentType="
                    + expressionType
                    + ", PreviousType="
                    + previousType);
        }
    }
}
