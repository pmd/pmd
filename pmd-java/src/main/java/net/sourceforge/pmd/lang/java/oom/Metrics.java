/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;


import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.oom.metrics.AtfdMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.WmcMetric;
import net.sourceforge.pmd.lang.java.oom.visitor.PackageStats;


/**
 * User bound façade of the Metrics Framework. Provides a uniform interface for the calculation of
 * metrics.
 * <p>
 * TODO split up get(ASTMethodOrConstructorDeclaration) and use default methods in OperationMetric
 *
 * @author Clément Fournier
 */
public class Metrics {

    private static final PackageStats TOP_LEVEL_PACKAGE = new PackageStats();

    private Metrics() { // Cannot be instantiated

    }

    /**
     * Returns the top level package stats.
     *
     * @return The top level package stats.
     */
    public static PackageStats getTopLevelPackageStats() {
        return TOP_LEVEL_PACKAGE;
    }

    /**
     * Computes a metric identified by its code a the class AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed.
     */
    public static double get(ClassMetricKey key, ASTClassOrInterfaceDeclaration node) {
        QualifiedName qname = node.getQualifiedName();
        double memoized = TOP_LEVEL_PACKAGE.getMemo(key, qname); // TODO

        return memoized == Double.NaN ? key.getCalculator().computeFor(node, TOP_LEVEL_PACKAGE)
                                      : memoized;
    }

    /**
     * Computes a metric identified by its code a the operation AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed.
     */
    public static double get(OperationMetricKey key, ASTMethodOrConstructorDeclaration node) {
        QualifiedName qname = node.getQualifiedName();
        double memoized = TOP_LEVEL_PACKAGE.getMemo(key, qname); // TODO

        return memoized == Double.NaN ? key.getCalculator().computeFor(node, TOP_LEVEL_PACKAGE)
                                      : memoized;
    }

    /**
     * Keys identifying class metrics.
     */
    public enum ClassMetricKey {
        /** Access to Foreign Data. */
        ATFD(new AtfdMetric()),
        // ...
        /** Weighed Method Count. */
        WMC(new WmcMetric());

        /* The object used to calculate the metric */
        private final ClassMetric calculator;

        ClassMetricKey(ClassMetric m) {
            calculator = m;
        }

        ClassMetric getCalculator() {
            return calculator;
        }
    }

    /**
     * Keys identifying operation metrics.
     */
    public enum OperationMetricKey {

        /** Access to Foreign Data. */ // TODO add short description here
        ATFD(new AtfdMetric());

        /* The object used to calculate the metric */
        private final OperationMetric calculator;

        OperationMetricKey(OperationMetric m) {
            calculator = m;
        }

        OperationMetric getCalculator() {
            return calculator;
        }
    }
}
