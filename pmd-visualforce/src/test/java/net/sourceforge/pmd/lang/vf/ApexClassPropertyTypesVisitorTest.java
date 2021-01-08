/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.ast.Node;

import apex.jorje.semantic.symbol.type.BasicType;

public class ApexClassPropertyTypesVisitorTest {
    @Test
    public void testApexClassIsProperlyParsed() throws IOException {
        LanguageVersion languageVersion = LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion();
        ParserOptions parserOptions = languageVersion.getLanguageVersionHandler().getDefaultParserOptions();
        Parser parser = languageVersion.getLanguageVersionHandler().getParser(parserOptions);

        Path apexPath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Apex)
                .resolve("ApexController.cls").toAbsolutePath();
        ApexClassPropertyTypesVisitor visitor = new ApexClassPropertyTypesVisitor();
        try (BufferedReader reader = Files.newBufferedReader(apexPath, StandardCharsets.UTF_8)) {
            Node node = parser.parse(apexPath.toString(), reader);
            assertNotNull(node);
            visitor.visit((ApexNode<?>) node, null);
        }

        List<Pair<String, BasicType>> variables = visitor.getVariables();
        assertEquals(7, variables.size());
        Map<String, BasicType> variableNameToVariableType = new Hashtable<>();
        for (Pair<String, BasicType> variable : variables) {
            // Map the values and ensure there were no duplicates
            BasicType previous = variableNameToVariableType.put(variable.getKey(), variable.getValue());
            assertNull(variable.getKey(), previous);
        }

        assertEquals(BasicType.ID, variableNameToVariableType.get("ApexController.AccountIdProp"));
        assertEquals(BasicType.ID, variableNameToVariableType.get("ApexController.AccountId"));
        assertEquals(BasicType.STRING, variableNameToVariableType.get("ApexController.AccountName"));
        assertEquals(BasicType.APEX_OBJECT, variableNameToVariableType.get("ApexController.InnerController"));
        assertEquals(BasicType.ID, variableNameToVariableType.get("ApexController.InnerController.InnerAccountIdProp"));
        assertEquals(BasicType.ID, variableNameToVariableType.get("ApexController.InnerController.InnerAccountId"));
        assertEquals(BasicType.STRING, variableNameToVariableType.get("ApexController.InnerController.InnerAccountName"));
    }
}
