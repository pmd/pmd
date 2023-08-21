/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.vf.VFTestUtils;

class ApexClassPropertyTypesVisitorTest {

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void testApexClassIsProperlyParsed() {
        Path apexPath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Apex)
                                   .resolve("ApexController.cls")
                                   .toAbsolutePath();

        ApexClassPropertyTypesVisitor visitor = new ApexClassPropertyTypesVisitor();
        try (LanguageProcessorRegistry lpReg = VFTestUtils.fakeLpRegistry()) {
            new ApexClassPropertyTypes(lpReg).parseApex(apexPath).acceptVisitor(visitor, null);
        }

        List<Pair<String, String>> variables = visitor.getVariables();
        assertEquals(7, variables.size());
        Map<String, String> variableNameToVariableType = new Hashtable<>();
        for (Pair<String, String> variable : variables) {
            // Map the values and ensure there were no duplicates
            String previous = variableNameToVariableType.put(variable.getKey(), variable.getValue());
            assertNull(previous, variable.getKey());
        }

        assertTrue("ID".equalsIgnoreCase(variableNameToVariableType.get("ApexController.AccountIdProp")));
        assertTrue("ID".equalsIgnoreCase(variableNameToVariableType.get("ApexController.AccountId")));
        assertTrue("String".equalsIgnoreCase(variableNameToVariableType.get("ApexController.AccountName")));
        assertTrue("ApexController.InnerController".equalsIgnoreCase(variableNameToVariableType.get("ApexController.InnerController")));
        assertTrue("ID".equalsIgnoreCase(variableNameToVariableType.get("ApexController.InnerController.InnerAccountIdProp")));
        assertTrue("ID".equalsIgnoreCase(variableNameToVariableType.get("ApexController.InnerController.InnerAccountId")));
        assertTrue("String".equalsIgnoreCase(variableNameToVariableType.get("ApexController.InnerController.InnerAccountName")));
    }
}
