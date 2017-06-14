/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.oom.Metric.Option;
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;


/**
 * Base class for metrics. Metric objects encapsulate the computational logic required to compute a metric from a
 * PackageStats and node.
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetric implements Metric {

    protected boolean isAbstractHandler = false;

    protected List<QualifiedName> findAllCalls(ASTMethodOrConstructorDeclaration node) {
        List<QualifiedName> result = new ArrayList<>();
        // TODO:cf
        // Needs TypeRes
        // Find the qualified names of all methods called in that method's block
        return result;
    }

    /**
     * Checks if the metric can be computed on that node.
     *
     * @param node The node to check for
     *
     * @return True if the metric can be computed, false otherwise.
     */
    // TODO:cf better wrap that around the metrics implementation
    protected boolean isSupported(ASTMethodOrConstructorDeclaration node) {
        return isAbstractHandler || !node.isAbstract();
    }

}
