/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce.ast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.lang.visualforce.DataType;

import com.google.common.reflect.ClassPath;

/**
 * Responsible for storing a mapping of Fields that can be referenced from Visualforce to the type of the field.
 *
 * <p>SFDX and MDAPI project formats are supported.
 * @see <a href="https://developer.salesforce.com/docs/atlas.en-us.sfdx_dev.meta/sfdx_dev/sfdx_dev_source_file_format.htm">Salesforce DX Project Structure and Source Format</a>
 */
class ObjectFieldTypes extends SalesforceFieldTypes {
    private static final Logger LOG = LoggerFactory.getLogger(ObjectFieldTypes.class);

    public static final String CUSTOM_OBJECT_SUFFIX = "__c";
    private static final String FIELDS_DIRECTORY = "fields";
    private static final String MDAPI_OBJECT_FILE_SUFFIX = ".object";
    private static final String SFDX_FIELD_FILE_SUFFIX = ".field-meta.xml";

    private static final Map<String, DataType> SYSTEM_FIELDS;
    private static final Map<String, ClassPath.ClassInfo> SOBJECTS;

    static {
        // see https://developer.salesforce.com/docs/atlas.en-us.object_reference.meta/object_reference/system_fields.htm
        SYSTEM_FIELDS = new HashMap<>();
        SYSTEM_FIELDS.put("id", DataType.Lookup);
        SYSTEM_FIELDS.put("isdeleted", DataType.Checkbox);
        SYSTEM_FIELDS.put("createdbyid", DataType.Lookup);
        SYSTEM_FIELDS.put("createddate", DataType.DateTime);
        SYSTEM_FIELDS.put("lastmodifiedbyid", DataType.Lookup);
        SYSTEM_FIELDS.put("lastmodifieddate", DataType.DateTime);
        SYSTEM_FIELDS.put("systemmodstamp", DataType.DateTime);
        // name is not defined as systemfield, but might occur frequently
        SYSTEM_FIELDS.put("name", DataType.Text);

        try {
            // see https://developer.salesforce.com/docs/atlas.en-us.object_reference.meta/object_reference/sforce_api_objects_list.htm
            // and https://github.com/apex-dev-tools/sobject-types
            SOBJECTS = Collections.unmodifiableMap(
                    ClassPath.from(ClassLoader.getSystemClassLoader())
                            .getTopLevelClasses(com.nawforce.runforce.SObjects.Account.class.getPackage().getName())
                            .stream()
                            .collect(Collectors.toMap(c -> c.getSimpleName().toLowerCase(Locale.ROOT),
                                    Function.identity()))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

            addSystemFields(objectName);

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
            LOG.debug("Expression does not have two parts: {}", expression);
        }
    }

    /**
     * Sfdx projects decompose custom fields into individual files. This method will return the individual file that
     * corresponds to &lt;objectName&gt;.&lt;fieldName&gt; if it exists.
     *
     * <p>Note: these metadata files are created not only for custom fields, but also for standard fields that
     * are used within the project. The metadata of these standard fields (fields of standard objects) provide
     * less explicit information. E.g. the type is not available the metadata file for these standard fields.
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

            DataType dataType = null;

            if (typeNode != null) {
                // custom field with a defined type
                String type = typeNode.getNodeValue();
                dataType = DataType.fromString(type);
            } else {
                // maybe a field from a standard object - the type is then not explicitly in field-meta.xml provided
                ClassPath.ClassInfo classInfo = SOBJECTS.get(customObjectName.toLowerCase(Locale.ROOT));
                if (classInfo != null) {
                    Field[] fields = classInfo.load().getFields();
                    for (Field f : fields) {
                        if (f.getName().equalsIgnoreCase(fullNameNode.getNodeValue())) {
                            dataType = DataType.fromTypeName(f.getType().getSimpleName());
                            break;
                        }
                    }
                    if (dataType == null) {
                        LOG.warn("Couldn't determine data type of customObjectName={} from {}", customObjectName, sfdxCustomFieldPath);
                        dataType = DataType.Unknown;
                    }
                } else {
                    LOG.warn("Couldn't determine data type of customObjectName={} - no sobject definition found", customObjectName);
                    dataType = DataType.Unknown;
                }
            }

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
     * Add the set of system fields which aren't present in the metadata file, but may be referenced from the
     * visualforce page.
     *
     * @see <a href="https://developer.salesforce.com/docs/atlas.en-us.object_reference.meta/object_reference/system_fields.htm">Overview of Salesforce Objects and Fields / System Fields</a>
     */
    private void addSystemFields(String customObjectName) {
        for (Map.Entry<String, DataType> entry : SYSTEM_FIELDS.entrySet()) {
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
