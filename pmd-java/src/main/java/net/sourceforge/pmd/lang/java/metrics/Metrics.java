/**
 *
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public class Metrics {

    /* Holds sufficient statistics gathered by the visitor */
    private static PackageStats m_holder;

    /* References all available class metrics */
    public static enum ClassMetricKey {
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
    
    /* References all available method metrics */
    public static enum OperationMetricKey {
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

    /**
     * Computes a metric identified by its code on the class AST node being
     * passed.
     */
    public static double get(ClassMetricKey key, ASTClassOrInterfaceDeclaration node) {
        return key.getCalculator().computeFor(node, m_holder);
    }

    /**
     * Computes a metric identified by its code on the operation AST node being
     * passed.
     */
    public static double get(OperationMetricKey key, ASTMethodOrConstructorDeclaration node) {
        return key.getCalculator().computeFor(node, m_holder);
    }

}
