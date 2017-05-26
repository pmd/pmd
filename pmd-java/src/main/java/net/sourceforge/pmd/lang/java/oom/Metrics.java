/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import static net.sourceforge.pmd.lang.java.oom.visitor.MetricsVisitorFacade.TOP_LEVEL_PACKAGE;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.QualifiableNode.QualifiedName;
import net.sourceforge.pmd.lang.java.oom.metrics.AtfdMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.WmcMetric;


/**
 * User bound façade of the Metrics Framework. Provides a uniform interface for the calculation of
 * metrics.
 *
 * @author Clément Fournier
 */
public class Metrics {

    private Metrics() { // Cannot be instantiated

    }

    /**
     * Computes a metric identified by its code on the class AST node being
     * passed.
     */
    public static double get(ClassMetricKey key, ASTClassOrInterfaceDeclaration node) {
        QualifiedName qname = node.getQualifiedName();
        double memoized = TOP_LEVEL_PACKAGE.getMemo(key, qname); // TODO

        return memoized == Double.NaN ? key.getCalculator().computeFor(node, TOP_LEVEL_PACKAGE) : memoized;
    }

    /**
     * Computes a metric identified by its code on the operation AST node being
     * passed.
     */
    public static double get(OperationMetricKey key, ASTMethodOrConstructorDeclaration node) {
        QualifiedName qname = node.getQualifiedName();
        double memoized = TOP_LEVEL_PACKAGE.getMemo(key, qname); // TODO

        return memoized == Double.NaN ? key.getCalculator().computeFor(node, TOP_LEVEL_PACKAGE) : memoized;
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
