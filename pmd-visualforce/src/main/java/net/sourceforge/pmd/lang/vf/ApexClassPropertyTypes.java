/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.ast.Node;

import apex.jorje.semantic.symbol.type.BasicType;

/**
 * Responsible for storing a mapping of Apex Class properties that can be referenced from Visualforce to the type of the
 * property.
 */
class ApexClassPropertyTypes {
    private static final Logger LOGGER = Logger.getLogger(ApexClassPropertyTypes.class.getName());
    private static final String APEX_CLASS_FILE_SUFFIX = ".cls";

    private final ConcurrentHashMap<String, IdentifierType> variableNameToVariableType;
    private final Set<String> variableNameProcessed;

    ApexClassPropertyTypes() {
        this.variableNameToVariableType = new ConcurrentHashMap<>();
        this.variableNameProcessed = Collections.newSetFromMap(new ConcurrentHashMap());
    }

    /**
     * Looks in {@code apexDirectories} for an Apex property identified by {@code expression}.
     *
     * @return the IdentifierType for the property represented by {@code expression} or null if not found.
     */
    public IdentifierType getVariableType(String expression, String vfFileName, List<String> apexDirectories) {
        String lowerExpression = expression.toLowerCase(Locale.ROOT);
        if (variableNameToVariableType.containsKey(lowerExpression)) {
            // The expression has been previously retrieved
            return variableNameToVariableType.get(lowerExpression);
        } else if (variableNameProcessed.contains(lowerExpression)) {
            // The expression has been previously requested, but was not found
            return null;
        } else {
            String[] parts = expression.split("\\.");
            if (parts.length >= 2) {
                // Load the class and parse it
                String className = parts[0];

                Path vfFilePath = Paths.get(vfFileName);
                for (String apexDirectory : apexDirectories) {
                    Path candidateDirectory;
                    if (Paths.get(apexDirectory).isAbsolute()) {
                        candidateDirectory = Paths.get(apexDirectory);
                    } else {
                        candidateDirectory = vfFilePath.getParent().resolve(apexDirectory);
                    }

                    Path apexFilePath = candidateDirectory.resolve(className + APEX_CLASS_FILE_SUFFIX);
                    if (Files.exists(apexFilePath) && Files.isRegularFile(apexFilePath)) {
                        Parser parser = getApexParser();
                        try (BufferedReader reader = Files.newBufferedReader(apexFilePath, StandardCharsets.UTF_8)) {
                            Node node = parser.parse(apexFilePath.toString(), reader);
                            ApexClassPropertyTypesVisitor visitor = new ApexClassPropertyTypesVisitor();
                            visitor.visit((ApexNode<?>) node, null);
                            for (Pair<String, BasicType> variable : visitor.getVariables()) {
                                setVariableType(variable.getKey(), variable.getValue());
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        if (variableNameToVariableType.containsKey(lowerExpression)) {
                            // Break out of the loop if a variable was found
                            break;
                        }
                    }
                }
                variableNameProcessed.add(lowerExpression);
            } else {
                throw new RuntimeException("Malformed expression: " + expression);
            }
        }

        return variableNameToVariableType.get(lowerExpression);
    }

    private void setVariableType(String name, BasicType basicType) {
        IdentifierType identifierType = IdentifierType.fromBasicType(basicType);
        IdentifierType previousType = variableNameToVariableType.put(name.toLowerCase(Locale.ROOT), identifierType);
        if (previousType != null && !previousType.equals(identifierType)) {
            // It is possible to have a property and method with different types that appear the same to this code. An
            // example is an Apex class with a property "public String Foo {get; set;}" and a method of
            // "Integer getFoo() { return 1; }". In this case set the value as Unknown because we can't be sure which it
            // is. This code could be more complex in an attempt to determine if all the types are safe from escaping,
            // but we will allow a false positive in order to let the user know that the code could be refactored to be
            // more clear.
            variableNameToVariableType.put(name.toLowerCase(Locale.ROOT), IdentifierType.Unknown);
            LOGGER.warning("Conflicting types for "
                    + name
                    + ". CurrentType="
                    + identifierType
                    + ", PreviousType="
                    + previousType);
        }
    }

    private Parser getApexParser() {
        LanguageVersion languageVersion = LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion();
        ParserOptions parserOptions = languageVersion.getLanguageVersionHandler().getDefaultParserOptions();
        return languageVersion.getLanguageVersionHandler().getParser(parserOptions);
    }
}

