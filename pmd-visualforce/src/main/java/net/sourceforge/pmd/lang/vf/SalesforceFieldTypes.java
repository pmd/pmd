/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for storing a mapping of Fields that can be referenced from Visualforce to the type of the field. The
 * fields are identified by in a case insensitive manner.
 */
abstract class SalesforceFieldTypes {
    /**
     * Cache of lowercase variable names to the variable type declared in the field's metadata file.
     */
    private final Map<String, DataType> variableNameToVariableType;

    /**
     * Keep track of which variables were already processed. Avoid processing if a page repeatedly asks for an entry
     * which we haven't previously found.
     */
    private final Set<String> variableNameProcessed;

    SalesforceFieldTypes() {
        this.variableNameToVariableType = new HashMap<>();
        this.variableNameProcessed = new HashSet<>();
    }

    /**
     *
     * @param expression expression literal as declared in the Visualforce page
     * @param vfFileName file name of the Visualforce page that contains expression. Used to resolve relative paths
     *                   included in {@code metadataDirectories}
     * @param metadataDirectories absolute or relative list of directories that may contain the metadata corresponding
     *                            to {@code expression}
     * @return the DataType if it can be determined, else null
     */
    public DataType getDataType(String expression, String vfFileName, List<String> metadataDirectories) {
        String lowerExpression = expression.toLowerCase(Locale.ROOT);
        if (variableNameToVariableType.containsKey(lowerExpression)) {
            // The expression has been previously retrieved
            return variableNameToVariableType.get(lowerExpression);
        } else if (variableNameProcessed.contains(lowerExpression)) {
            // The expression has been previously requested, but was not found
            return null;
        } else {
            Path vfFilePath = Paths.get(vfFileName);
            List<Path> resolvedPaths = new ArrayList<>();
            for (String metadataDirectory : metadataDirectories) {
                if (Paths.get(metadataDirectory).isAbsolute()) {
                    resolvedPaths.add(Paths.get(metadataDirectory));
                } else {
                    resolvedPaths.add(vfFilePath.getParent().resolve(metadataDirectory));
                }
            }

            findDataType(expression, resolvedPaths);
            variableNameProcessed.add(lowerExpression);
            return variableNameToVariableType.get(lowerExpression);
        }
    }

    /**
     * Stores {@link DataType} in a map using lower cased {@code expression} as the key.
     * @param expression expression literal as declared in the Visualforce page
     * @param dataType identifier determined for
     * @return the previous value associated with {@code key}, or {@code null} if there was no mapping for {@code key}.
     */
    protected DataType putDataType(String expression, DataType dataType) {
        return variableNameToVariableType.put(expression.toLowerCase(Locale.ROOT), dataType);
    }

    /**
     * @return true if the expression has previously been stored via {@link #putDataType(String, DataType)}
     */
    protected boolean containsExpression(String expression) {
        return variableNameToVariableType.containsKey(expression.toLowerCase(Locale.ROOT));
    }

    /**
     * Subclasses should attempt to find the {@code DataType} of {@code expression} within
     * {@code metadataDirectories}. The subclass should store the value by invoking
     * {@link #putDataType(String, DataType)}.
     *
     * @param expression expression as defined in the Visualforce page, case is preserved
     * @param metadataDirectories list of directories that may contain the metadata corresponding to {@code expression}
     */
    protected abstract void findDataType(String expression, List<Path> metadataDirectories);
}

