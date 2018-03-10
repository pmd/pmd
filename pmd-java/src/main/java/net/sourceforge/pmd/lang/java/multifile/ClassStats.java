/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSignature;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature;

/**
 * Statistics about a class, enum, interface, or annotation. Stores information about the contained members and their
 * signatures, and memoizes the results of the class metrics computed on the corresponding node.
 *
 * <p>This class does not provide methods to operate directly on its nested classes, but only on itself. To operate on a
 * nested class, retrieve the correct ClassStats with {@link PackageStats#getClassStats(JavaQualifiedName, boolean)}
 * then use the methods of ClassStats. Note that at this level, entities of the data structure do not manipulate
 * QualifiedNames anymore, only Strings.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
final class ClassStats implements ClassMirror {

    private Map<JavaOperationSignature, Set<String>> operations = new HashMap<>();
    private Map<JavaFieldSignature, Set<String>> fields = new HashMap<>();
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
    void addOperation(String name, JavaOperationSignature sig) {
        if (!operations.containsKey(sig)) {
            operations.put(sig, new HashSet<String>());
        }
        operations.get(sig).add(name);
    }


    /**
     * Adds a field to the class.
     *
     * @param name The name of the field
     * @param sig  The signature of the field
     */
    void addField(String name, JavaFieldSignature sig) {
        if (!fields.containsKey(sig)) {
            fields.put(sig, new HashSet<String>());
        }
        fields.get(sig).add(name);
    }


    @Override
    public boolean hasMatchingOpSig(String name, JavaOperationSigMask mask) {
        // Indexing on signatures optimises this type of request
        for (Entry<JavaOperationSignature, Set<String>> entry : operations.entrySet()) {
            if (mask.covers(entry.getKey())) {
                if (entry.getValue().contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public boolean hasMatchingFieldSig(String name, JavaFieldSigMask mask) {
        for (Entry<JavaFieldSignature, Set<String>> entry : fields.entrySet()) {
            if (mask.covers(entry.getKey())) {
                if (entry.getValue().contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public int countMatchingOpSigs(JavaOperationSigMask sigMask) {
        int sum = 0;

        for (Entry<JavaOperationSignature, Set<String>> e : operations.entrySet()) {
            if (sigMask.covers(e.getKey())) {
                sum += e.getValue().size();
            }
        }

        return sum;
    }


    @Override
    public int countMatchingFieldSigs(JavaFieldSigMask sigMask) {
        int sum = 0;

        for (Entry<JavaFieldSignature, Set<String>> e : fields.entrySet()) {
            if (sigMask.covers(e.getKey())) {
                sum += e.getValue().size();
            }
        }

        return sum;
    }

}
