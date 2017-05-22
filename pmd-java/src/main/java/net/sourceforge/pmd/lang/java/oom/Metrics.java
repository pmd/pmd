/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.metrics.AtfdMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.WmcMetric;
import net.sourceforge.pmd.lang.java.oom.visitor.PackageStats;

/**
 * Façade of the Metrics Framework.
 *
 * @author Clément Fournier
 */
public class Metrics {

    /**
     * Holds sufficient statistics and memoises results
     */
    private static PackageStats topLevelPackage;

    private Metrics() { // Cannot be instantiated

    }

    /**
     * Computes a metric identified by its code on the class AST node being
     * passed.
     */
    public static double get(ClassMetricKey key, ASTClassOrInterfaceDeclaration node) {
        String qname = ""; // node.getQualifiedName(); //TODO
        double memoized = topLevelPackage.getMemo(key, qname);

        return memoized == Double.NaN ? key.getCalculator().computeFor(node, topLevelPackage) : memoized;
    }

    /**
     * Computes a metric identified by its code on the operation AST node being
     * passed.
     */
    public static double get(OperationMetricKey key, ASTMethodOrConstructorDeclaration node) {
        String qname = ""; // node.getQualifiedName(); //TODO
        double memoized = topLevelPackage.getMemo(key, qname);

        return memoized == Double.NaN ? key.getCalculator().computeFor(node, topLevelPackage) : memoized;
    }

    /**
     * Keys for class metrics
     */
    public enum ClassMetricKey {
        ATFD(new AtfdMetric()),
        // ...
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
     * Keys for operation metrics
     */
    public enum OperationMetricKey {

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
