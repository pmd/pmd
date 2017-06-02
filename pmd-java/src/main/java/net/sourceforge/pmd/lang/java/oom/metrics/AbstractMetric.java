/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;


/**
 * Base class for metrics. Metric objects encapsulate the computational logic required to compute a metric from a PackageStats.
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetric {

    protected boolean isAbstractHandler = false;

    protected List<QualifiedName> findAllCalls(ASTMethodOrConstructorDeclaration node) {
        List<QualifiedName> result = new ArrayList<>();
        // TODO
        // Needs TypeRes!!
        // Find the qualified names of all methods called in that method's block
        return result;
    }

    /**
     * Checks if the metric can be computed on that node.
     *
     * @param node The node to check for
     * @return True if the metric can be computed, false otherwise.
     */
    // TODO better wrap that around the metrics implementation
    protected boolean isSupported(ASTMethodOrConstructorDeclaration node) {
        if (isAbstractHandler) {
            return true;
        }

        if (node instanceof ASTMethodDeclaration) {
            return !((ASTMethodDeclaration) node).isAbstract();
        } else {
            return !((ASTConstructorDeclaration) node).isAbstract();
        }
    }
}
