/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.QualifiedName;

/**
 * Statistics about a class. Gathers information about the contained members and their signatures,
 * subclasses and superclasses. This class does not provide methods to operate directly on its
 * nested classes, but only on itself. To operate on a nested class, retrieve the correct ClassStats
 * with {@link PackageStats#getClassStats(QualifiedName, boolean)} then use the methods of
 * ClassStats.
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
     * Finds a ClassStats in the direct children of this class. This cannot be a nested class, for
     * that see {@link PackageStats#getClassStats(QualifiedName, boolean)}.
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


    /**
     * Adds an operation to these stats
     *
     * @param qname The qualified name of the operation
     * @param sig   The signature of the operation
     */
    public void addOperation(QualifiedName qname, OperationSignature sig) {
        if (!operations.containsKey(sig)) {
            operations.put(sig, new HashSet<String>());
        }
        operations.get(sig).add(qname.getOperation());
    }

    /**
     * Adds an operation to these stats (not the nested stats!)
     *
     * @param name The qualified name of the operation
     * @param sig  The signature of the operation
     */
    public void addField(String name, FieldSignature sig) {
        if (!fields.containsKey(sig)) {
            fields.put(sig, new HashSet<String>());
        }
        fields.get(sig).add(name);
    }

    //TODO
}
