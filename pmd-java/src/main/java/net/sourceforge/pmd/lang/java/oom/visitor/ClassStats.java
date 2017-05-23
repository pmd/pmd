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
     * {@see PackageStats#addClass}
     *
     * @param className Name of the nested class.
     *
     * @return The new ClassStats or the ones that were found.
     */
    public ClassStats addClass(String className) {
        if (!nestedClasses.containsKey(className)) {
            nestedClasses.put(className, new ClassStats());
        }
        return nestedClasses.get(className);
    }

    //TODO
}
