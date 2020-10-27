/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ApexClassPropertyTypesTest {
    @Test
    public void testApexClassIsProperlyParsed() {
        Path vfPagePath = VFTestContstants.SFDX_PATH.resolve(Paths.get("pages", "ApexController.page"))
                .toAbsolutePath();
        String vfFileName = vfPagePath.toString();

        // Intentionally use the wrong case for property names to ensure that they can be found. The Apex class name
        // must have the correct case since it is used to lookup the file. The Apex class name is guaranteed to be correct
        // in the Visualforce page, but the property names are not
        ApexClassPropertyTypes apexClassPropertyTypes = new ApexClassPropertyTypes();
        assertEquals(IdentifierType.Lookup,
                apexClassPropertyTypes.getVariableType("ApexController.accOuntIdProp", vfFileName,
                        VFTestContstants.RELATIVE_APEX_DIRECTORIES));
        assertEquals(IdentifierType.Lookup,
                apexClassPropertyTypes.getVariableType("ApexController.AcCountId", vfFileName,
                        VFTestContstants.RELATIVE_APEX_DIRECTORIES));
        assertEquals(IdentifierType.Text,
                apexClassPropertyTypes.getVariableType("ApexController.AcCountname", vfFileName,
                        VFTestContstants.RELATIVE_APEX_DIRECTORIES));

        // InnerController
        assertEquals("The class should be parsed to Unknown. It's not a valid expression on its own.",
                IdentifierType.Unknown,
                apexClassPropertyTypes.getVariableType("ApexController.innErController", vfFileName,
                        VFTestContstants.RELATIVE_APEX_DIRECTORIES));
        assertEquals(IdentifierType.Lookup,
                apexClassPropertyTypes.getVariableType("ApexController.innErController.innErAccountIdProp",
                        vfFileName, VFTestContstants.RELATIVE_APEX_DIRECTORIES));
        assertEquals(IdentifierType.Lookup,
                apexClassPropertyTypes.getVariableType("ApexController.innErController.innErAccountid",
                        vfFileName, VFTestContstants.RELATIVE_APEX_DIRECTORIES));
        assertEquals(IdentifierType.Text,
                apexClassPropertyTypes.getVariableType("ApexController.innErController.innErAccountnAme",
                        vfFileName, VFTestContstants.RELATIVE_APEX_DIRECTORIES));

        assertNull("Invalid class should return null",
                apexClassPropertyTypes.getVariableType("unknownclass.invalidProperty", vfFileName,
                        VFTestContstants.RELATIVE_APEX_DIRECTORIES));
        assertNull("Invalid class property should return null",
                apexClassPropertyTypes.getVariableType("ApexController.invalidProperty", vfFileName,
                        VFTestContstants.RELATIVE_APEX_DIRECTORIES));
    }

    /**
     * It is possible to have a property and method with different types that resolve to the same Visualforce
     * expression. An example is an Apex class with a property "public String Foo {get; set;}" and a method of
     * "Integer getFoo() { return 1; }". These properties should map to {@link IdentifierType#Unknown}.
     */
    @Test
    public void testConflictingPropertyTypesMapsToUnknown() {
        Path vfPagePath = VFTestContstants.SFDX_PATH.resolve(Paths.get("pages", "ApexController.page"))
                .toAbsolutePath();
        String vfFileName = vfPagePath.toString();
        ApexClassPropertyTypes apexClassPropertyTypes = new ApexClassPropertyTypes();
        assertEquals(IdentifierType.Unknown,
                apexClassPropertyTypes.getVariableType("ApexWithConflictingPropertyTypes.ConflictingProp",
                        vfFileName, VFTestContstants.RELATIVE_APEX_DIRECTORIES));
    }

    @Test
    public void testInvalidDirectoryDoesNotCauseAnException() {
        Path vfPagePath = VFTestContstants.SFDX_PATH.resolve(Paths.get("pages", "ApexController.page"))
                .toAbsolutePath();
        String vfFileName = vfPagePath.toString();

        List<String> paths = Arrays.asList(Paths.get("..", "classes-does-not-exist").toString());
        ApexClassPropertyTypes apexClassPropertyTypes = new ApexClassPropertyTypes();
        assertNull(apexClassPropertyTypes.getVariableType("ApexController.accOuntIdProp", vfFileName, paths));
    }
}
