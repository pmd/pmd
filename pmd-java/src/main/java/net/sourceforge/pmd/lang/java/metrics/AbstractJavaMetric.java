/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.metrics.api.Metric;


/**
 * Base class for metrics. Metric objects encapsulate the computational logic required to compute a metric from a
 * JavaPackageStats and node. They're stateless.
 *
 * @param <N> Type of node the metric can be computed on
 *
 * @author Clément Fournier
 */
public abstract class AbstractJavaMetric<N extends Node> implements Metric<N> {

    protected List<QualifiedName> findAllCalls(ASTMethodOrConstructorDeclaration node) {
        List<QualifiedName> result = new ArrayList<>();
        // TODO:cf findAllCalls
        // Needs TypeRes
        // Find the qualified names of all methods called in that method's block
        return result;
    }


    /**
     * Gives access to the toplevel package stats to metrics without having to pass them as a parameter to metrics.
     *
     * @return The toplevel package stats (singleton contained within {@link JavaMetrics}).
     */
    protected static JavaPackageStats getTopLevelPackageStats() {
        return JavaMetrics.getTopLevelPackageStats();
    }

}
