/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Statistics about a class. Gathers information about the contained members and their signatures,
 * subclasses and superclasses.
 *
 * @author Clément Fournier
 */
public class ClassStats {
    private Map<OperationSignature, Set<String>> operations = new HashMap<>();
    private Map<FieldSignature, Set<String>> fields = new HashMap<>();
    private Map<String, ClassStats> nestedClasses = new HashMap<>();

    // References to the hierarchy
    // We store strings so that classes not analysed are ignored
    private String superclass;
    private List<String> subclasses;


    /**
     * Adds a ClassStats to the children of this class. This cannot be a nested class, for that see
     * {@see PackageStats#getNestedClassStats}
     *
     * @param className        Name of the nested class.
     * @param createIfNotFound Create ClassStats if missing.
     *
     * @return The new ClassStats or the one that was found. Can return null if createIfNotFound is
     * unset.
     */
    public ClassStats getNestedClassStats(String className, boolean createIfNotFound) {
        if (createIfNotFound && !nestedClasses.containsKey(className)) {
            nestedClasses.put(className, new ClassStats());
        }
        return nestedClasses.get(className);
    }

    //TODO
}
