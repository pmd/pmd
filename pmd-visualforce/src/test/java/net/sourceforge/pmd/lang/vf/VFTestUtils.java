/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.SaxonXPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public final class VFTestUtils {
    /**
     * Salesforce metadata is stored in two different formats, the newer sfdx form and the older mdapi format. Used to
     * locate metadata on the file system during unit tests.
     */
    public enum MetadataFormat {
        SFDX("sfdx"),
        MDAPI("mdapi");

        public final String directoryName;

        MetadataFormat(String directoryName) {
            this.directoryName = directoryName;
        }
    }

    /**
     * Represents the metadata types that are referenced from unit tests. Used to locate metadata on the file system
     * during unit tests.
     */
    public enum MetadataType {
        Apex("classes"),
        Objects("objects"),
        Vf("pages");

        public final String directoryName;

        MetadataType(String directoryName) {
            this.directoryName = directoryName;
        }
    }

    /**
     * @return the path of the directory that matches the given parameters. The directory path is constructed using the
     * following convention:
     * src/test/resources/_decomposed_test_package_name_/_test_class_name_minus_Test_/metadata/_metadata_format_/_metadata_type_
     */
    public static Path getMetadataPath(Object testClazz, MetadataFormat metadataFormat, MetadataType metadataType) {
        Path path = Paths.get("src", "test", "resources");
        // Decompose the test's package structure into directories
        for (String directory : testClazz.getClass().getPackage().getName().split("\\.")) {
            path = path.resolve(directory);
        }
        // Remove 'Test' from the class name
        path = path.resolve(testClazz.getClass().getSimpleName().replaceFirst("Test$", ""));
        // Append additional directories based on the MetadataFormat and MetadataType
        path = path.resolve("metadata").resolve(metadataFormat.directoryName);
        if (metadataType != null) {
            path = path.resolve(metadataType.directoryName);
        }

        return path.toAbsolutePath();
    }

    /**
     * @return all nodes that match the {@code xpath} version 2 query.
     */
    public static List<Node> findNodes(Node node, String xpath) {
        SaxonXPathRuleQuery query = createQuery(xpath);
        return query.evaluate(node, new RuleContext());
    }

    /**
     * Verify that return values of {@link SalesforceFieldTypes#getDataType(String, String, List)} using the keys of
     * {@code expectedDataTypes} matches the values of {@code expectedDataTypes}
     */
    public static void validateDataTypes(Map<String, DataType> expectedDataTypes, SalesforceFieldTypes fieldTypes,
                                         Path vfPagePath, List<String> paths) {
        String vfFileName = vfPagePath.toString();

        for (Map.Entry<String, DataType> entry : expectedDataTypes.entrySet()) {
            assertEquals(entry.getKey(), entry.getValue(),
                    fieldTypes.getDataType(entry.getKey(), vfFileName, paths));
        }
    }

    private static SaxonXPathRuleQuery createQuery(String xpath) {
        SaxonXPathRuleQuery query = new SaxonXPathRuleQuery();
        query.setVersion(XPathRuleQuery.XPATH_2_0);
        query.setProperties(Collections.<PropertyDescriptor<?>, Object>emptyMap());
        query.setXPath(xpath);
        return query;
    }

    private VFTestUtils() {
    }
}
