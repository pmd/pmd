/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;
import net.sourceforge.pmd.lang.java.metrics.signature.FieldSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.OperationSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.OperationSignature;
import net.sourceforge.pmd.lang.metrics.api.MetricKey;
import net.sourceforge.pmd.lang.metrics.api.MetricVersion;
import net.sourceforge.pmd.lang.metrics.api.ResultOption;


/**
 * Statistics about a package. This recursive data structure mirrors the package structure of the analysed project and
 * stores information about the classes and subpackages it contains.
 *
 * @author Clément Fournier
 * @see ClassStats
 */
public final class PackageStats {

    private final Map<String, PackageStats> subPackages = new HashMap<>();
    private final Map<String, ClassStats> classes = new HashMap<>();


    /**
     * Default constructor.
     */
    /* default */ PackageStats() {

    }


    /**
     * Resets the entire data structure.
     */
    /* default */ void reset() {
        subPackages.clear();
        classes.clear();
    }


    /**
     * Returns true if the signature of the operation designated by the qualified name is covered by the mask.
     *
     * @param qname   The operation to test
     * @param sigMask The signature mask to use
     *
     * @return True if the signature of the operation designated by the qualified name is covered by the mask
     */
    public boolean hasMatchingSig(JavaQualifiedName qname, OperationSigMask sigMask) {
        ClassStats clazz = getClassStats(qname, false);

        return clazz != null && clazz.hasMatchingSig(qname.getOperation(), sigMask);
    }


    /**
     * Gets the OperationStats corresponding to the qualified name.
     *
     * @param qname            The qualified name of the operation to fetch
     * @param sig              The signature of the operation, which must be non-null if createIfNotFound is set
     * @param createIfNotFound Create an OperationStats if missing
     *
     * @return The new OperationStat, or the one that was found. Can return null only if createIfNotFound is unset
     */
    OperationStats getOperationStats(JavaQualifiedName qname, OperationSignature sig, boolean createIfNotFound) {
        ClassStats container = getClassStats(qname, createIfNotFound);

        if (container == null || !qname.isOperation()) {
            return null;
        }

        OperationStats target = container.getOperationStats(qname.getOperation(), sig);

        if (target == null && createIfNotFound) {
            if (sig == null) {
                throw new IllegalArgumentException("Cannot add an operation with a null signature");
            }
            target = container.addOperation(qname.getOperation(), sig);
        }

        return target;
    }


    /**
     * Gets the ClassStats corresponding to the named resource. The class can be nested. If the createIfNotFound
     * parameter is set, the method also creates the hierarchy if it doesn't exist.
     *
     * @param qname            The qualified name of the class
     * @param createIfNotFound Create hierarchy if missing
     *
     * @return The new ClassStats, or the one that was found. Can return null only if createIfNotFound is unset
     */
    /* default */ ClassStats getClassStats(JavaQualifiedName qname, boolean createIfNotFound) {
        PackageStats container = getSubPackage(qname, createIfNotFound);

        if (container == null) {
            return null;
        }

        String topClassName = qname.getClasses()[0];
        if (createIfNotFound && classes.get(topClassName) == null) {
            classes.put(topClassName, new ClassStats());
        }

        ClassStats next = classes.get(topClassName);

        if (next == null) {
            return null;
        }

        String[] nameClasses = qname.getClasses();

        for (int i = 1; i < nameClasses.length && next != null; i++) {
            // Delegate search for nested classes to ClassStats
            next = next.getNestedClassStats(nameClasses[i], createIfNotFound);
        }

        return next;
    }


    /**
     * Returns the deepest PackageStats that contains the named resource. If the second parameter is set, creates the
     * missing PackageStats along the way.
     *
     * @param qname            The qualified name of the resource
     * @param createIfNotFound If set to true, the hierarch is created if missing
     *
     * @return The deepest package that contains this resource. Can only return null if createIfNotFound is unset
     */
    private PackageStats getSubPackage(JavaQualifiedName qname, boolean createIfNotFound) {
        if (qname.getPackages() == null) {
            return this; // the toplevel
        }

        String[] packagePath = qname.getPackages();
        PackageStats next = this;

        for (int i = 0; i < packagePath.length && next != null; i++) {
            if (createIfNotFound && next.subPackages.get(packagePath[i]) == null) {
                next.subPackages.put(packagePath[i], new PackageStats());
            }

            next = next.subPackages.get(packagePath[i]);
        }

        return next;
    }


    /**
     * Returns true if the signature of the field designated by its name and the qualified name of its class is covered
     * by the mask.
     *
     * @param qname     The class of the field
     * @param fieldName The name of the field
     * @param sigMask   The signature mask to use
     *
     * @return True if the signature of the field is covered by the mask
     */
    public boolean hasMatchingSig(JavaQualifiedName qname, String fieldName, FieldSigMask sigMask) {
        ClassStats clazz = getClassStats(qname, false);

        return clazz != null && clazz.hasMatchingSig(fieldName, sigMask);
    }


    /**
     * Computes the value of a metric on a class.
     *
     * @param key     The class metric to compute
     * @param node    The AST node of the class
     * @param force   Force the recomputation; if unset, we'll first check for a memoized result
     * @param version The version of the metric
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed
     */
    /* default */ double compute(MetricKey<ASTAnyTypeDeclaration> key, ASTAnyTypeDeclaration node, boolean force,
                                 MetricVersion version) {
        ClassStats container = getClassStats(node.getQualifiedName(), false);

        return container == null ? Double.NaN
                                 : MetricsComputer.INSTANCE.compute(key, node, force, version, container);
    }


    /**
     * Computes the value of a metric for an operation.
     *
     * @param key     The operation metric for which to find a memoized result
     * @param node    The AST node of the operation
     * @param force   Force the recomputation; if unset, we'll first check for a memoized result
     * @param version The version of the metric
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed
     */
    /* default */ double compute(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTMethodOrConstructorDeclaration node,
                                 boolean force, MetricVersion version) {
        JavaQualifiedName qname = node.getQualifiedName();
        ClassStats container = getClassStats(qname, false);
        OperationStats memoizer = container == null ? null : container.getOperationStats(qname.getOperation(),
                                                                                         OperationSignature.buildFor(node));

        return memoizer == null ? Double.NaN
                                : MetricsComputer.INSTANCE.compute(key, node, force, version, memoizer);
    }


    /**
     * Computes an aggregate result using a ResultOption.
     *
     * @param key     The class metric to compute
     * @param node    The AST node of the class
     * @param force   Force the recomputation; if unset, we'll first check for a memoized result
     * @param version The version of the metric
     * @param option  The type of result to compute
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed
     */
    /* default */ double computeWithResultOption(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTAnyTypeDeclaration node,
                                                 boolean force, MetricVersion version, ResultOption option) {
        ClassStats container = getClassStats(node.getQualifiedName(), false);

        return container == null ? Double.NaN
                                 : MetricsComputer.INSTANCE.computeWithResultOption(key, node, force, version,
                                                                                    option, container);
    }
}
