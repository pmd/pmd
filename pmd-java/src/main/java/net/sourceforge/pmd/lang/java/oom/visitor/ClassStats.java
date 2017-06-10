/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;

/**
 * Statistics about a class. Gathers information about the contained members and their signatures,
 * subclasses and superclasses. This class does not provide methods to operate directly on its
 * nested classes, but only on itself. To operate on a nested class, retrieve the correct ClassStats
 * with {@link PackageStats#getClassStats(QualifiedName, boolean)} then use the methods of
 * ClassStats.
 *
 * <p>Note that at this level, entities of the DS do not manipulate QualifiedNames anymore, only Strings.
 *
 * @author Clément Fournier
 */
public class ClassStats {

    private Map<OperationSignature, Set<OperationStats>> operations = new HashMap<>();
    private Map<FieldSignature, Set<String>> fields = new HashMap<>();
    private Map<String, ClassStats> nestedClasses = new HashMap<>();

    private Map<OperationMetricKey, Double> memo = new HashMap<>();

    // References to the hierarchy
    // We store strings so that classes not analysed are ignored
    // TODO useful?
    // private String superclass;
    // private List<String> subclasses;


    /**
     * Finds a ClassStats in the direct children of this class. This cannot be a nested class, for
     * that see {@link PackageStats#getClassStats(QualifiedName, boolean)}.
     *
     * @param className        Name of the nested class.
     * @param createIfNotFound Create ClassStats if missing.
     *
     * @return The new ClassStats or the one that was found. Can return null if createIfNotFound is unset.
     */
    ClassStats getNestedClassStats(String className, boolean createIfNotFound) {
        if (createIfNotFound && !nestedClasses.containsKey(className)) {
            nestedClasses.put(className, new ClassStats());
        }
        return nestedClasses.get(className);
    }

    /**
     * Adds an operation to the class.
     *
     * @param name The name of the operation
     * @param sig  The signature of the operation
     */
    void addOperation(String name, OperationSignature sig) {
        if (!operations.containsKey(sig)) {
            operations.put(sig, new HashSet<OperationStats>());
        }
        operations.get(sig).add(new OperationStats(name));
    }

    /**
     * Adds a field to the class.
     *
     * @param name The qualified name of the field
     * @param sig  The signature of the field
     */
    void addField(String name, FieldSignature sig) {
        if (!fields.containsKey(sig)) {
            fields.put(sig, new HashSet<String>());
        }
        fields.get(sig).add(name);
    }

    /**
     * Checks whether the class declares an operation by the name given which is covered by the
     * signature mask.
     *
     * @param name The name of the operation to look for
     * @param mask The mask covering accepted signatures
     *
     * @return True if the class declares an operation by the name given which is covered by the signature mask, false
     * otherwise.
     */
    public boolean hasMatchingSig(String name, OperationSigMask mask) {
        // Indexing on signatures optimises this type of request
        for (OperationSignature sig : operations.keySet()) {
            if (mask.covers(sig)) {
                if (operations.get(sig).contains(new OperationStats(name))) { // TODO eliminate "new" here
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the class declares a field by the name given which is covered by the
     * signature mask.
     *
     * @param name The name of the operation to look for
     * @param mask The mask covering accepted signatures
     *
     * @return True if the class declares a field by the name given which is covered by the signature mask, false
     * otherwise.
     */
    public boolean hasMatchingSig(String name, FieldSigMask mask) {
        for (FieldSignature sig : fields.keySet()) {
            if (mask.covers(sig)) {
                if (fields.get(sig).contains(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Finds a memoized result for a specific metric and operation.
     *
     * @param key  The operation metric for which to find a memoized result.
     * @param name The name of the operation;
     *
     * @return The memoized result if it was found, or {@code Double.NaN}.
     */
    double getMemo(OperationMetricKey key, String name) {
        // TODO maybe optimise this
        for (OperationSignature sig : operations.keySet()) {
            for (OperationStats stats : operations.get(sig)) {
                if (stats.equals(name)) {
                    return stats.getMemo(key);
                }
            }
        }
        return Double.NaN;
    }

    /**
     * Finds a memoized result for this class.
     *
     * @param key  The class metric for which to find a memoized result.
     *
     * @return The memoized result if it was found, or {@code Double.NaN}.
     */
    public double getMemo(Metrics.ClassMetricKey key) {
        return memo.get(key) == null ? Double.NaN : memo.get(key);
    }
}
