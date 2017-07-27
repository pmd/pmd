/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;
import net.sourceforge.pmd.lang.java.metrics.signature.FieldSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.FieldSignature;
import net.sourceforge.pmd.lang.java.metrics.signature.OperationSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.OperationSignature;

/**
 * Statistics about a class, enum, interface, or annotation. Stores information about the contained members and their
 * signatures, and memoizes the results of the class metrics computed on the corresponding node.
 *
 * <p>This class does not provide methods to operate directly on its nested classes, but only on itself. To operate on a
 * nested class, retrieve the correct ClassStats with {@link PackageStats#getClassStats(JavaQualifiedName, boolean)}
 * then
 * use the methods of ClassStats. Note that at this level, entities of the data structure do not manipulate
 * QualifiedNames anymore, only Strings.
 *
 * @author Clément Fournier
 */
/* default */ class ClassStats extends Memoizer {

    private Map<OperationSignature, Map<String, OperationStats>> operations = new HashMap<>();
    private Map<FieldSignature, Set<String>> fields = new HashMap<>();
    private Map<String, ClassStats> nestedClasses = new HashMap<>();

    // References to the hierarchy
    // TODO:cf useful?
    // private String superclass;
    // private List<String> subclasses;


    /**
     * Finds a ClassStats in the direct children of this class. This can only be a directly nested class, for example in
     * the following snippet, A can get B and B can get C but A cannot get C without asking B.
     * <pre>
     * {@code
     * class MyClass { // ClassStats A
     *   class MyNested { // ClassStats B
     *     class MyDeeplyNested { // ClassStats C
     *     }
     *   }
     * }
     * }
     * </pre>
     *
     * @param className        Name of the nested class
     * @param createIfNotFound Create the requested ClassStats if missing
     *
     * @return The new ClassStats or the one that was found. Can return null if createIfNotFound is unset
     */
    /* default */ ClassStats getNestedClassStats(String className, boolean createIfNotFound) {
        if (createIfNotFound && !nestedClasses.containsKey(className)) {
            nestedClasses.put(className, new ClassStats());
        }
        return nestedClasses.get(className);
    }


    OperationStats getOperationStats(String operationName) {
        for (Map<String, OperationStats> map : operations.values()) {
            OperationStats stats = map.get(operationName);
            if (stats != null) {
                return stats;
            }
        }
        return null;
    }


    OperationStats getOperationStats(String operationName, OperationSignature sig) {
        if (sig == null) {
            return getOperationStats(operationName);
        }

        Map<String, OperationStats> sigMap = operations.get(sig);

        return sigMap == null ? null : sigMap.get(operationName);
    }


    /**
     * Adds an operation to the class.
     *
     * @param name The name of the operation
     * @param sig  The signature of the operation
     *
     * @return The newly created operation stats
     */
    /* default */ OperationStats addOperation(String name, OperationSignature sig) {
        if (!operations.containsKey(sig)) {
            operations.put(sig, new HashMap<String, OperationStats>());
        }
        OperationStats newOp = new OperationStats(name);
        operations.get(sig).put(name, newOp);
        return newOp;
    }


    /**
     * Adds a field to the class.
     *
     * @param name The name of the field
     * @param sig  The signature of the field
     */
    /* default */ void addField(String name, FieldSignature sig) {
        if (!fields.containsKey(sig)) {
            fields.put(sig, new HashSet<String>());
        }
        fields.get(sig).add(name);
    }


    /**
     * Checks whether the class declares an operation by the name given which is covered by the signature mask.
     *
     * @param name The name of the operation to look for
     * @param mask The mask covering accepted signatures
     *
     * @return True if the class declares an operation by the name given which is covered by the signature mask, false
     * otherwise
     */
    /* default */ boolean hasMatchingSig(String name, OperationSigMask mask) {
        // Indexing on signatures optimises this type of request
        for (OperationSignature sig : operations.keySet()) {
            if (mask.covers(sig)) {
                if (operations.get(sig).containsKey(name)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Checks whether the class declares a field by the name given which is covered by the signature mask.
     *
     * @param name The name of the field to look for
     * @param mask The mask covering accepted signatures
     *
     * @return True if the class declares a field by the name given which is covered by the signature mask, false
     * otherwise
     */
    /* default */ boolean hasMatchingSig(String name, FieldSigMask mask) {
        for (FieldSignature sig : fields.keySet()) {
            if (mask.covers(sig)) {
                if (fields.get(sig).contains(name)) {
                    return true;
                }
            }
        }

        return false;
    }

}
