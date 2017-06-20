/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.oom.api.Metric;


/**
 * Base class for metrics. Metric objects encapsulate the computational logic required to compute a metric from a
 * PackageStats and node. They're stateless.
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetric implements Metric {

    /**
     * Gives access to the toplevel package stats to metrics without having to pass them as a parameter to metrics.
     *
     * @return The toplevel package stats (singleton contained within {@link Metrics}.
     */
    protected PackageStats getTopLevelPackageStats() {
        return Metrics.getTopLevelPackageStats();
    }


    protected List<QualifiedName> findAllCalls(ASTMethodOrConstructorDeclaration node) {
        List<QualifiedName> result = new ArrayList<>();
        // TODO:cf
        // Needs TypeRes
        // Find the qualified names of all methods called in that method's block
        return result;
    }

    /**
     * Default implementation of the supports method, which filters out abstract nodes. Metrics that support abstract
     * nodes should override this method.
     *
     * @param node The node to check.
     *
     * @return True if the metric can be computed on this node.
     */
    @Override
    public boolean supports(AccessNode node) {
        return !node.isAbstract();
    }
}
