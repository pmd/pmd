/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.vf.VFTestUtils;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextFile;

import apex.jorje.semantic.symbol.type.BasicType;

public class ApexClassPropertyTypesVisitorTest {

    @Test
    @SuppressWarnings("PMD.CloseResource")
    public void testApexClassIsProperlyParsed() throws IOException {
        LanguageVersion languageVersion = LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion();
        Parser parser = languageVersion.getLanguageVersionHandler().getParser();

        Path apexPath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Apex)
                                   .resolve("ApexController.cls");

        TextFile textFile = TextFile.forPath(apexPath, StandardCharsets.UTF_8, languageVersion);

        ParserTask task = new ParserTask(TextDocument.create(textFile),
                                         SemanticErrorReporter.noop());
        ApexClassPropertyTypesVisitor visitor = new ApexClassPropertyTypesVisitor();
        parser.parse(task).acceptVisitor(visitor, null);

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

        textFile.close();
    }
}
