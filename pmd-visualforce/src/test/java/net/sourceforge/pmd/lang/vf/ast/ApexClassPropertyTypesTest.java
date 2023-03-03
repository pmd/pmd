/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.vf.DataType;
import net.sourceforge.pmd.lang.vf.VFTestUtils;
import net.sourceforge.pmd.lang.vf.VfLanguageProperties;

class ApexClassPropertyTypesTest {
    private static final Map<String, DataType> EXPECTED_DATA_TYPES;

    static {
        // Intentionally use the wrong case for property names to ensure that they can be found. The Apex class name
        // must have the correct case since it is used to lookup the file. The Apex class name is guaranteed to be correct
        // in the Visualforce page, but the property names are not
        EXPECTED_DATA_TYPES = new HashMap<>();
        EXPECTED_DATA_TYPES.put("ApexController.accOuntIdProp", DataType.Lookup);
        EXPECTED_DATA_TYPES.put("ApexController.AcCountId", DataType.Lookup);
        EXPECTED_DATA_TYPES.put("ApexController.AcCountname", DataType.Text);

        // InnerController
        // The class should be parsed to Unknown. It's not a valid expression on its own.
        EXPECTED_DATA_TYPES.put("ApexController.innErController", DataType.Unknown);
        EXPECTED_DATA_TYPES.put("ApexController.innErController.innErAccountIdProp", DataType.Lookup);
        EXPECTED_DATA_TYPES.put("ApexController.innErController.innErAccountid", DataType.Lookup);
        EXPECTED_DATA_TYPES.put("ApexController.innErController.innErAccountnAme", DataType.Text);

        // Edge cases
        // Invalid class should return null
        EXPECTED_DATA_TYPES.put("unknownclass.invalidProperty", null);
        // Invalid class property should return null
        EXPECTED_DATA_TYPES.put("ApexController.invalidProperty", null);
        /*
         * It is possible to have a property and method with different types that resolve to the same Visualforce
         * expression. An example is an Apex class with a property "public String Foo {get; set;}" and a method of
         * "Integer getFoo() { return 1; }". These properties should map to {@link DataType#Unknown}.
         */
        EXPECTED_DATA_TYPES.put("ApexController.ConflictingProp", DataType.Unknown);
    }

    @Test
    void testApexClassIsProperlyParsed() {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Vf)
                                     .resolve("SomePage.page");
        try (LanguageProcessorRegistry lpReg = VFTestUtils.fakeLpRegistry()) {
            ApexClassPropertyTypes apexClassPropertyTypes = new ApexClassPropertyTypes(lpReg);
            ObjectFieldTypesTest.validateDataTypes(EXPECTED_DATA_TYPES, apexClassPropertyTypes, vfPagePath,
                                                   VfLanguageProperties.APEX_DIRECTORIES_DESCRIPTOR.defaultValue());
        }

    }

    @Test
    void testInvalidDirectoryDoesNotCauseAnException() {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Vf)
                .resolve("SomePage.page");
        String vfFileName = vfPagePath.toString();

        List<String> paths = Arrays.asList(Paths.get("..", "classes-does-not-exist").toString());
        try (LanguageProcessorRegistry lpReg = VFTestUtils.fakeLpRegistry()) {
            ApexClassPropertyTypes apexClassPropertyTypes = new ApexClassPropertyTypes(lpReg);
            assertNull(apexClassPropertyTypes.getDataType("ApexController.accOuntIdProp", vfFileName, paths));
        }
    }
}
