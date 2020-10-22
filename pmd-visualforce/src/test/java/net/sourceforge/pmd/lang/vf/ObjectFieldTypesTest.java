/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class ObjectFieldTypesTest {

    /**
     * Verify that CustomFields stored in sfdx project format are correctly parsed
     */
    @Test
    public void testSfdxAccountIsProperlyParsed() {
        Path vfPagePath = VFTestContstants.SFDX_PATH.resolve(Paths.get("pages", "StandarAccountPage.page"))
                .toAbsolutePath();

        ObjectFieldTypes objectFieldTypes = new ObjectFieldTypes();
        validateSfdxAccount(objectFieldTypes, vfPagePath, VFTestContstants.RELATIVE_OBJECTS_DIRECTORIES);
    }

    /**
     * Verify that CustomFields stored in mdapi format are correctly parsed
     */
    @Test
    public void testMdapiAccountIsProperlyParsed() {
        Path vfPagePath = VFTestContstants.MDAPI_PATH.resolve(Paths.get("pages", "StandarAccountPage.page"))
                .toAbsolutePath();

        ObjectFieldTypes objectFieldTypes = new ObjectFieldTypes();
        validateMDAPIAccount(objectFieldTypes, vfPagePath, VFTestContstants.RELATIVE_OBJECTS_DIRECTORIES);
    }

    /**
     * Verify that fields are found across multiple directories
     */
    @Test
    public void testFieldsAreFoundInMultipleDirectories() {
        ObjectFieldTypes objectFieldTypes;
        Path vfPagePath = VFTestContstants.SFDX_PATH.resolve(Paths.get("pages", "StandarAccountPage.page"))
                .toAbsolutePath();

        List<String> paths = Arrays.asList(VFTestContstants.RELATIVE_OBJECTS_DIRECTORIES.get(0),
                VFTestContstants.ABSOLUTE_MDAPI_OBJECTS_DIRECTORIES.get(0));
        objectFieldTypes = new ObjectFieldTypes();
        validateSfdxAccount(objectFieldTypes, vfPagePath, paths);
        validateMDAPIAccount(objectFieldTypes, vfPagePath, paths);

        Collections.reverse(paths);
        objectFieldTypes = new ObjectFieldTypes();
        validateSfdxAccount(objectFieldTypes, vfPagePath, paths);
        validateMDAPIAccount(objectFieldTypes, vfPagePath, paths);
    }

    @Test
    public void testInvalidDirectoryDoesNotCauseAnException() {
        Path vfPagePath = VFTestContstants.SFDX_PATH.resolve(Paths.get("pages", "StandarAccountPage.page"))
                .toAbsolutePath();
        String vfFileName = vfPagePath.toString();

        List<String> paths = Arrays.asList(Paths.get("..", "objects-does-not-exist").toString());
        ObjectFieldTypes objectFieldTypes = new ObjectFieldTypes();
        assertNull(objectFieldTypes.getVariableType("Account.DoesNotExist__c", vfFileName, paths));
    }

    /**
     * Validate the expected results when the Account Fields are stored in decomposed sfdx format
     */
    private void validateSfdxAccount(ObjectFieldTypes objectFieldTypes, Path vfPagePath, List<String> paths) {
        String vfFileName = vfPagePath.toString();

        assertEquals(ExpressionType.Checkbox,
                objectFieldTypes.getVariableType("Account.Checkbox__c", vfFileName, paths));
        assertEquals(ExpressionType.DateTime,
                objectFieldTypes.getVariableType("Account.DateTime__c", vfFileName, paths));
        assertEquals(ExpressionType.LongTextArea,
                objectFieldTypes.getVariableType("Account.LongTextArea__c", vfFileName, paths));
        assertEquals(ExpressionType.Picklist,
                objectFieldTypes.getVariableType("Account.Picklist__c", vfFileName, paths));
        assertEquals(ExpressionType.Text,
                objectFieldTypes.getVariableType("Account.Text__c", vfFileName, paths));
        assertEquals(ExpressionType.TextArea,
                objectFieldTypes.getVariableType("Account.TextArea__c", vfFileName, paths));
        assertNull(objectFieldTypes.getVariableType("Account.DoesNotExist__c", vfFileName, paths));
    }

    /**
     * Validate the expected results when the Account Fields are stored in a single file MDAPI format
     */
    private void validateMDAPIAccount(ObjectFieldTypes objectFieldTypes, Path vfPagePath, List<String> paths) {
        String vfFileName = vfPagePath.toString();

        assertEquals(ExpressionType.Checkbox,
                objectFieldTypes.getVariableType("Account.MDCheckbox__c", vfFileName, paths));
        assertEquals(ExpressionType.DateTime,
                objectFieldTypes.getVariableType("Account.MDDateTime__c", vfFileName, paths));
        assertEquals(ExpressionType.LongTextArea,
                objectFieldTypes.getVariableType("Account.MDLongTextArea__c", vfFileName, paths));
        assertEquals(ExpressionType.Picklist,
                objectFieldTypes.getVariableType("Account.MDPicklist__c", vfFileName, paths));
        assertEquals(ExpressionType.Text,
                objectFieldTypes.getVariableType("Account.MDText__c", vfFileName, paths));
        assertEquals(ExpressionType.TextArea,
                objectFieldTypes.getVariableType("Account.MDTextArea__c", vfFileName, paths));
        assertNull(objectFieldTypes.getVariableType("Account.DoesNotExist__c", vfFileName, paths));
    }
}
