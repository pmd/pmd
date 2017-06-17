/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.keys.ClassMetric;

/**
 * @author Clément Fournier
 */
public abstract class AbstractClassMetric extends AbstractMetric implements ClassMetric {


    /**
     * Gets the sum of the value of an operation metric over all operations in this class. The computation is not forced
     * (memoized results are used if they can be found).
     *
     * @param node             The class node.
     * @param holder           The toplevel package stats.
     * @param key              The operation metric to use.
     * @param useNestedClasses Adds the operations of nested classes to the sum.
     *
     * @return Returns the sum of a metric over all operations of a class.
     */
    protected double sumMetricOverOperations(ASTClassOrInterfaceDeclaration node,
                                             PackageStats holder, OperationMetricKey key, boolean useNestedClasses) {

        List<ASTMethodOrConstructorDeclaration> operations
            = node.findDescendantsOfType(ASTMethodOrConstructorDeclaration.class);

        double sum = 0;
        for (ASTMethodOrConstructorDeclaration op : operations) {

            if (!useNestedClasses && op.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class) != node) {
                continue;
            }

            double val = Metrics.get(key, op);
            sum += val == Double.NaN ? 0 : val;
        }
        return sum;
    }


}
