/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;


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


    /**
     * Sums the results of the computation of a metric on all operation nodes of this class. Methods belonging to a
     * nested class are counted as well. The computation is not forced (memoized results are used if they can be found).
     *
     * @param key  The metric to compute.
     * @param root The class node.
     *
     * @return The sum of a metric computed on the operations of a class.
     */
    protected double sumMetricOnOperations(OperationMetricKey key, ASTClassOrInterfaceDeclaration root) {
        PackageStats toplevel = Metrics.getTopLevelPackageStats();

        List<ASTMethodOrConstructorDeclaration> ops
            = root.findDescendantsOfType(ASTMethodOrConstructorDeclaration.class);

        double sum = 0;
        for (ASTMethodOrConstructorDeclaration op : ops) {
            double val = toplevel.compute(key, op, false);
            if (val != Double.NaN) {
                sum += val;
            }
        }

        return sum;
    }
}
