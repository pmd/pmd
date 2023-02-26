/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ApexLanguageProcessor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.vf.DataType;

/**
 * Responsible for storing a mapping of Apex Class properties that can be referenced from Visualforce to the type of the
 * property.
 */
class ApexClassPropertyTypes extends SalesforceFieldTypes {

    private static final Logger LOG = LoggerFactory.getLogger(ApexClassPropertyTypes.class);
    private static final String APEX_CLASS_FILE_SUFFIX = ".cls";
    private final ApexLanguageProcessor apexProcessor;
    private final LanguageProcessorRegistry lpReg;

    ApexClassPropertyTypes(LanguageProcessorRegistry lpReg) {
        this.apexProcessor = (ApexLanguageProcessor) lpReg.getProcessor(ApexLanguageModule.getInstance());
        this.lpReg = lpReg;
    }

    /**
     * Looks in {@code apexDirectories} for an Apex property identified by {@code expression}.
     */
    @Override
    public void findDataType(String expression, List<Path> apexDirectories) {
        String[] parts = expression.split("\\.");
        if (parts.length >= 2) {
            // Load the class and parse it
            String className = parts[0];

            for (Path apexDirectory : apexDirectories) {
                Path apexFilePath = apexDirectory.resolve(className + APEX_CLASS_FILE_SUFFIX);
                if (Files.exists(apexFilePath) && Files.isRegularFile(apexFilePath)) {
                    Node node = parseApex(expression, apexFilePath);
                    ApexClassPropertyTypesVisitor visitor = new ApexClassPropertyTypesVisitor();
                    node.acceptVisitor(visitor, null);

                    for (Pair<String, String> variable : visitor.getVariables()) {
                        putDataType(variable.getKey(), DataType.fromTypeName(variable.getValue()));
                    }

                    if (containsExpression(expression)) {
                        // Break out of the loop if a variable was found
                        break;
                    }
                }
            }
        }
    }

    Node parseApex(Path apexFilePath) {
        LanguageVersion languageVersion = apexProcessor.getLanguageVersion();
        try (TextFile file = TextFile.forPath(apexFilePath, StandardCharsets.UTF_8, languageVersion);
             TextDocument textDocument = TextDocument.create(file)) {

            Parser parser = apexProcessor.services().getParser();
            ParserTask task = new ParserTask(textDocument, SemanticErrorReporter.noop(), lpReg);

            return parser.parse(task);
        } catch (IOException e) {
            throw new ContextedRuntimeException(e).addContextValue("apexFilePath", apexFilePath);
        }
    }

    private Node parseApex(String contextExpr, Path apexFilePath) {
        try {
            return parseApex(apexFilePath);
        } catch (ContextedRuntimeException e) {
            throw e.addContextValue("expression", contextExpr);
        }
    }

    @Override
    protected DataType putDataType(String name, DataType dataType) {
        DataType previousType = super.putDataType(name, dataType);
        if (previousType != null && !previousType.equals(dataType)) {
            // It is possible to have a property and method with different types that appear the same to this code. An
            // example is an Apex class with a property "public String Foo {get; set;}" and a method of
            // "Integer getFoo() { return 1; }". In this case set the value as Unknown because we can't be sure which it
            // is. This code could be more complex in an attempt to determine if all the types are safe from escaping,
            // but we will allow a false positive in order to let the user know that the code could be refactored to be
            // more clear.
            super.putDataType(name, DataType.Unknown);
            LOG.warn("Conflicting types for {}. CurrentType={}, PreviousType={}",
                    name, dataType, previousType);
        }
        return previousType;
    }

}
