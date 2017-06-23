/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.oom.api.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.ResultOption;
import net.sourceforge.pmd.lang.java.oom.signature.FieldSigMask;
import net.sourceforge.pmd.lang.java.oom.signature.FieldSignature;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSigMask;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSignature;

/**
 * Statistics about a class. Gathers information about the contained members and their signatures,
 * subclasses and superclasses. This class does not provide methods to operate directly on its
 * nested classes, but only on itself. To operate on a nested class, retrieve the correct ClassStats
 * with {@link PackageStats#getClassStats(QualifiedName, boolean)} then use the methods of
 * ClassStats.
 *
 * <p>Note that at this level, entities of the DS do not manipulate QualifiedNames anymore, only
 * Strings.
 *
 * @author Clément Fournier
 */
/* default */ class ClassStats {

    private Map<OperationSignature, Map<String, OperationStats>> operations = new HashMap<>();
    private Map<FieldSignature, Set<String>> fields = new HashMap<>();
    private Map<String, ClassStats> nestedClasses = new HashMap<>();

    private Map<ParameterizedMetricKey, Double> memo = new HashMap<>();

    // References to the hierarchy
    // TODO:cf useful?
    // private String superclass;
    // private List<String> subclasses;

    private static double highest(List<Double> values) {
        double highest = Double.NEGATIVE_INFINITY;
        for (double val : values) {
            if (val > highest) {
                highest = val;
            }
        }
        return highest == Double.NEGATIVE_INFINITY ? 0 : highest;
    }

    private static double average(List<Double> values) {
        return sum(values) / values.size();
    }

    private static double sum(List<Double> values) {
        double sum = 0;
        for (double val : values) {
            sum += val;
        }
        return sum;
    }

    /**
     * Finds a ClassStats in the direct children of this class. This can only be a directly nested class, for example
     * in the following snippet, A can get B and B can get C but A cannot get C without asking B.
     * <pre>
     * <code>
     * class MyClass { // ClassStats A
     *   class MyNested { // ClassStats B
     *     class MyDeeplyNested { // ClassStats C
     *     }
     *   }
     * }
     * </code>
     * </pre>
     *
     * @param className        Name of the nested class.
     * @param createIfNotFound Create the requested ClassStats if missing.
     *
     * @return The new ClassStats or the one that was found. Can return null if createIfNotFound is unset.
     */
    /* default */ ClassStats getNestedClassStats(String className, boolean createIfNotFound) {
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
    /* default */ void addOperation(String name, OperationSignature sig) {
        if (!operations.containsKey(sig)) {
            operations.put(sig, new HashMap<String, OperationStats>());
        }
        operations.get(sig).put(name, new OperationStats(name));
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
     * Checks whether the class declares an operation by the name given which is covered by the
     * signature mask.
     *
     * @param name The name of the operation to look for
     * @param mask The mask covering accepted signatures
     *
     * @return True if the class declares an operation by the name given which is covered by the signature mask, false
     * otherwise.
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
     * Checks whether the class declares a field by the name given which is covered by the
     * signature mask.
     *
     * @param name The name of the field to look for
     * @param mask The mask covering accepted signatures
     *
     * @return True if the class declares a field by the name given which is covered by the signature mask, false
     * otherwise.
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

    /**
     * Computes the value of a metric for an operation.
     *
     * @param key   The operation metric for which to find a memoized result.
     * @param node  The AST node of the operation.
     * @param name  The name of the operation.
     * @param force Force the recomputation. If unset, we'll first check for a memoized result.
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed.
     */
    /* default */ double compute(OperationMetricKey key, ASTMethodOrConstructorDeclaration node, String name,
                                 boolean force, MetricVersion version) {

        // TODO:cf the operation signature will be built many times, we might as well store it in the node
        Map<String, OperationStats> sigMap = operations.get(OperationSignature.buildFor(node));

        if (sigMap == null) {
            return Double.NaN;
        }

        OperationStats stats = sigMap.get(name);
        return stats == null ? Double.NaN : stats.compute(key, node, force, version);
    }

    /**
     * Computes an aggregate result using a ResultOption.
     *
     * @param key     The class metric to compute.
     * @param node    The AST node of the class.
     * @param force   Force the recomputation. If unset, we'll first check for a memoized result.
     * @param version The version of the metric.
     * @param option  The type of result to compute
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed.
     */
    /* default */ double computeWithResultOption(OperationMetricKey key, ASTAnyTypeDeclaration node,
                                                 boolean force, MetricVersion version, ResultOption option) {

        List<ASTMethodOrConstructorDeclaration> ops = AbstractMetric.findOperations(node, false);

        List<Double> values = new ArrayList<>();
        for (ASTMethodOrConstructorDeclaration op : ops) {
            if (key.getCalculator().supports(op)) {
                double val = this.compute(key, op, op.getQualifiedName().getOperation(), force, version);
                if (val != Double.NaN) {
                    values.add(val);
                }
            }
        }

        // FUTURE use streams to do that when we upgrade the compiler to 1.8
        switch (option) {
        case SUM:
            return sum(values);
        case HIGHEST:
            return highest(values);
        case AVERAGE:
            return average(values);
        default:
            return Double.NaN;
        }
    }

    /**
     * Computes the value of a metric for a class.
     *
     * @param key   The class metric for which to find a memoized result.
     * @param node  The AST node of the class.
     * @param force Force the recomputation. If unset, we'll first check for a memoized result.
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed.
     */
    /* default */ double compute(ClassMetricKey key, ASTAnyTypeDeclaration node, boolean force, MetricVersion version) {

        ParameterizedMetricKey paramKey = ParameterizedMetricKey.build(key, version);
        // if memo.get(key) == null then the metric has never been computed. NaN is a valid value.
        Double prev = memo.get(paramKey);
        if (!force && prev != null) {
            return prev;
        }

        ClassMetric metric = key.getCalculator();
        double val = metric.computeFor(node, version);
        memo.put(paramKey, val);

        return val;
    }

}
