/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.tuple.Pair;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.vf.DataType;

import apex.jorje.semantic.symbol.type.BasicType;

/**
 * Responsible for storing a mapping of Apex Class properties that can be referenced from Visualforce to the type of the
 * property.
 */
class ApexClassPropertyTypes extends SalesforceFieldTypes {
    private static final Logger LOGGER = Logger.getLogger(ApexClassPropertyTypes.class.getName());
    private static final String APEX_CLASS_FILE_SUFFIX = ".cls";

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
                    Parser parser = getApexParser();
                    try (BufferedReader reader = Files.newBufferedReader(apexFilePath, StandardCharsets.UTF_8)) {
                        Node node = parser.parse(apexFilePath.toString(), reader);
                        ApexClassPropertyTypesVisitor visitor = new ApexClassPropertyTypesVisitor();
                        visitor.visit((ApexNode<?>) node, null);
                        for (Pair<String, BasicType> variable : visitor.getVariables()) {
                            putDataType(variable.getKey(), DataType.fromBasicType(variable.getValue()));
                        }
                    } catch (IOException e) {
                        throw new ContextedRuntimeException(e)
                                .addContextValue("expression", expression)
                                .addContextValue("apexFilePath", apexFilePath);
                    }

                    if (containsExpression(expression)) {
                        // Break out of the loop if a variable was found
                        break;
                    }
                }
            }
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
            LOGGER.warning("Conflicting types for "
                    + name
                    + ". CurrentType="
                    + dataType
                    + ", PreviousType="
                    + previousType);
        }
        return previousType;
    }

    private Parser getApexParser() {
        LanguageVersion languageVersion = LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion();
        ParserOptions parserOptions = languageVersion.getLanguageVersionHandler().getDefaultParserOptions();
        return languageVersion.getLanguageVersionHandler().getParser(parserOptions);
    }
}
