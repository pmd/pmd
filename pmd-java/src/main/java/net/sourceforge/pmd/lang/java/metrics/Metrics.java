/**
 *
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public class Metrics {

    /* Holds sufficient statistics gathered by the visitor */
    private static DataHolder m_holder;

    /* References all available metrics */
    public static enum Key {
        ATFD(new AtfdMetric()),
        // ...
        WMC(new WmcMetric());
        
        /* The object used to calculate the metric */
        private final Metric calculator;
        /* Semiprime number, its factors are the flags */
        private int          flags = 1;
        
        Key(Metric m) {
            calculator = m;
            
            if (m instanceof ClassMetric) {
                flags *= 2;
            }
            if (m instanceof MethodMetric) {
                flags *= 3;
            }
        }
        
        boolean isClassMetric() {
            return flags % 2 == 0;
        }
        
        boolean isMethodMetric() {
            return flags % 3 == 0;
        }

        Metric getCalculator() {
            return calculator;
        }

    }

    /**
     * Computes a metric identified by its code on the class AST node being
     * passed.
     */
    public static double get(Key key, ASTClassOrInterfaceDeclaration node) {
        if (!key.isClassMetric()) {
            throw new UnsupportedOperationException("That metric cannot be computed on a class");
        }

        return ((ClassMetric) key.getCalculator()).computeFor(node, m_holder);
    }

    /**
     * Computes a metric identified by its code on the method AST node being
     * passed.
     */
    public static double get(Key key, ASTMethodDeclaration node) {
        if (!key.isMethodMetric()) {
            throw new UnsupportedOperationException("That metric cannot be computed on a method");
        }

        return ((MethodMetric) key.getCalculator()).computeFor(node, m_holder);
    }

}
