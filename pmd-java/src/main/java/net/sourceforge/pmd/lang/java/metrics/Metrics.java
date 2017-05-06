/**
 *
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;

/**
 * Façade of the Metrics Framework.
 *
 * @author Clément Fournier
 */
public class Metrics {

    /**
     * Holds sufficient statistics gathered by the visitor
     */
    private static PackageStats topPackageStats; // TODO

    private Metrics() {

    }

    /**
     * Computes a metric identified by its code on the class AST node being
     * passed.
     */
    public static double get(ClassMetricKey key, ASTClassOrInterfaceDeclaration node) {
        return key.getCalculator().computeFor(node, topPackageStats);
    }

    /**
     * Computes a metric identified by its code on the operation AST node being
     * passed.
     */
    public static double get(OperationMetricKey key, ASTMethodOrConstructorDeclaration node) {
        return key.getCalculator().computeFor(node, topPackageStats);
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

        ATFD(new AtfdMetric()),
        // ...
        ;

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
