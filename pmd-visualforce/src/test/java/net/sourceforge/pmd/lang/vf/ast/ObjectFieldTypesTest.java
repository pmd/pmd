/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.vf.DataType;
import net.sourceforge.pmd.lang.vf.VFTestUtils;
import net.sourceforge.pmd.lang.vf.VfLanguageProperties;

class ObjectFieldTypesTest {
    private static final Map<String, DataType> EXPECTED_SFDX_DATA_TYPES;
    private static final Map<String, DataType> EXPECTED_MDAPI_DATA_TYPES;

    static {
        EXPECTED_SFDX_DATA_TYPES = new HashMap<>();
        EXPECTED_SFDX_DATA_TYPES.put("Account.Checkbox__c", DataType.Checkbox);
        EXPECTED_SFDX_DATA_TYPES.put("Account.DateTime__c", DataType.DateTime);
        EXPECTED_SFDX_DATA_TYPES.put("Account.LongTextArea__c", DataType.LongTextArea);
        EXPECTED_SFDX_DATA_TYPES.put("Account.Picklist__c", DataType.Picklist);
        EXPECTED_SFDX_DATA_TYPES.put("Account.Text__c", DataType.Text);
        EXPECTED_SFDX_DATA_TYPES.put("Account.TextArea__c", DataType.TextArea);
        // Edge Cases
        // Invalid property should return null
        EXPECTED_SFDX_DATA_TYPES.put("Account.DoesNotExist__c", null);

        EXPECTED_MDAPI_DATA_TYPES = new HashMap<>();
        EXPECTED_MDAPI_DATA_TYPES.put("Account.MDCheckbox__c", DataType.Checkbox);
        EXPECTED_MDAPI_DATA_TYPES.put("Account.MDDateTime__c", DataType.DateTime);
        EXPECTED_MDAPI_DATA_TYPES.put("Account.MDLongTextArea__c", DataType.LongTextArea);
        EXPECTED_MDAPI_DATA_TYPES.put("Account.MDPicklist__c", DataType.Picklist);
        EXPECTED_MDAPI_DATA_TYPES.put("Account.MDText__c", DataType.Text);
        EXPECTED_MDAPI_DATA_TYPES.put("Account.MDTextArea__c", DataType.TextArea);
        // Edge Cases
        // Invalid property should return null
        EXPECTED_MDAPI_DATA_TYPES.put("Account.DoesNotExist__c", null);
    }

    /**
     * Verify that CustomFields stored in sfdx project format are correctly parsed
     */
    @Test
    void testSfdxAccountIsProperlyParsed() {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Vf).resolve("SomePage.page");

        ObjectFieldTypes objectFieldTypes = new ObjectFieldTypes();
        validateSfdxAccount(objectFieldTypes, vfPagePath, VfLanguageProperties.OBJECTS_DIRECTORIES_DESCRIPTOR.defaultValue());
    }

    /**
     * Verify that CustomFields stored in mdapi format are correctly parsed
     */
    @Test
    void testMdapiAccountIsProperlyParsed() {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.MDAPI, VFTestUtils.MetadataType.Vf).resolve("SomePage.page");

        ObjectFieldTypes objectFieldTypes = new ObjectFieldTypes();
        validateMDAPIAccount(objectFieldTypes, vfPagePath, VfLanguageProperties.OBJECTS_DIRECTORIES_DESCRIPTOR.defaultValue());
    }

    /**
     * Verify that fields are found across multiple directories
     */
    @Test
    void testFieldsAreFoundInMultipleDirectories() {
        ObjectFieldTypes objectFieldTypes;
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Vf)
            .resolve("SomePage.page");

        List<String> paths = Arrays.asList(VfLanguageProperties.OBJECTS_DIRECTORIES_DESCRIPTOR.defaultValue().get(0),
                                           VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.MDAPI, VFTestUtils.MetadataType.Objects).toString());
        objectFieldTypes = new ObjectFieldTypes();
        validateSfdxAccount(objectFieldTypes, vfPagePath, paths);
        validateMDAPIAccount(objectFieldTypes, vfPagePath, paths);

        Collections.reverse(paths);
        objectFieldTypes = new ObjectFieldTypes();
        validateSfdxAccount(objectFieldTypes, vfPagePath, paths);
        validateMDAPIAccount(objectFieldTypes, vfPagePath, paths);
    }

    @Test
    void testInvalidDirectoryDoesNotCauseAnException() {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Vf).resolve("SomePage.page");
        String vfFileName = vfPagePath.toString();

        List<String> paths = Arrays.asList(Paths.get("..", "objects-does-not-exist").toString());
        ObjectFieldTypes objectFieldTypes = new ObjectFieldTypes();
        assertNull(objectFieldTypes.getDataType("Account.DoesNotExist__c", vfFileName, paths));
    }

    /**
     * Validate the expected results when the Account Fields are stored in decomposed sfdx format
     */
    private void validateSfdxAccount(ObjectFieldTypes objectFieldTypes, Path vfPagePath, List<String> paths) {
        validateDataTypes(EXPECTED_SFDX_DATA_TYPES, objectFieldTypes, vfPagePath, paths);
    }

    /**
     * Validate the expected results when the Account Fields are stored in a single file MDAPI format
     */
    private void validateMDAPIAccount(ObjectFieldTypes objectFieldTypes, Path vfPagePath, List<String> paths) {
        validateDataTypes(EXPECTED_MDAPI_DATA_TYPES, objectFieldTypes, vfPagePath, paths);
    }

    /**
     * Verify that return values of {@link SalesforceFieldTypes#getDataType(String, String, List)} using the keys of
     * {@code expectedDataTypes} matches the values of {@code expectedDataTypes}
     */
    static void validateDataTypes(Map<String, DataType> expectedDataTypes, SalesforceFieldTypes fieldTypes,
                                         Path vfPagePath, List<String> paths) {
        String vfFileName = vfPagePath.toString();

        for (Map.Entry<String, DataType> entry : expectedDataTypes.entrySet()) {
            assertEquals(entry.getValue(), fieldTypes.getDataType(entry.getKey(), vfFileName, paths), entry.getKey());
        }
    }
}
