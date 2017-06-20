/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.AbstractMetric;
import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.api.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.ResultOption;

/**
 * Shares common logic across class metrics. Many class metrics actually sum the metric over the operations of
 * the class, so it makes sense to share that, and similar behaviour (TBD).
 *
 * @author Clément Fournier
 * @since June 2017
 */
public abstract class AbstractClassMetric extends AbstractMetric implements ClassMetric {

    /**
     * Gets the sum of the value of an operation metric over all operations in this class (excluding nested classes).
     * The computation is not forced (memoized results are used if they can be found).
     *
     * @param node          The class node.
     * @param key           The operation metric to use.
     * @param version       Version of the metric.
     * @param includeNested Adds the operations of nested classes to the sum.
     *
     * @return Returns the sum of a metric over all operations of a class.
     */
    protected double sumMetricOverOperations(ASTClassOrInterfaceDeclaration node, OperationMetricKey key,
                                             MetricVersion version, boolean includeNested) {

        List<ASTMethodOrConstructorDeclaration> operations = findOperations(node, includeNested);

        double sum = 0;
        for (ASTMethodOrConstructorDeclaration op : operations) {
            double val = Metrics.get(key, op, version);
            sum += val == Double.NaN ? 0 : val;
        }
        return sum;
    }

    /**
     * Gets the average of the value of an operation metric over all operations in this class (excluding nested
     * classes). The computation is not forced (memoized results are used if they can be found).
     *
     * @param node          The class node.
     * @param key           The operation metric to use.
     * @param version       Version of the metric.
     * @param includeNested Adds the operations of nested classes to the sum.
     *
     * @return Returns the average of a metric over all operations of a class.
     */
    protected double averageMetricOverOperations(ASTClassOrInterfaceDeclaration node, OperationMetricKey key,
                                                 MetricVersion version, boolean includeNested) {

        List<ASTMethodOrConstructorDeclaration> operations = findOperations(node, includeNested);

        double total = 0;
        for (ASTMethodOrConstructorDeclaration op : operations) {
            double val = Metrics.get(key, op, version);
            total += val == Double.NaN ? 1 : val;
        }
        return total / operations.size();
    }


    /**
     * Gets the highest value of an operation metric over all operations in this class (excluding nested classes).
     * The computation is not forced (memoized results are used if they can be found).
     *
     * @param node          The class node.
     * @param key           The operation metric to use.
     * @param version       Version of the metric.
     * @param includeNested Adds the operations of nested classes to the sum.
     *
     * @return Returns the highest value of a metric over all operations of a class.
     */
    protected double highestMetricOverOperations(ASTClassOrInterfaceDeclaration node, OperationMetricKey key,
                                                 MetricVersion version, boolean includeNested) {

        List<ASTMethodOrConstructorDeclaration> operations = findOperations(node, includeNested);

        double highest = Double.NEGATIVE_INFINITY;
        for (ASTMethodOrConstructorDeclaration op : operations) {
            double val = Metrics.get(key, op, version);

            if (val > highest) {
                highest = val;
            }
        }
        return highest;
    }


    // TODO:cf this one is computed everytime
    /**
     * Finds the declaration nodes of all methods or constructors that are declared inside a class.
     *
     * @param node          The class in which to look for.
     * @param includeNested Include operations found in nested classes?
     *
     * @return The list of all operations declared inside the specified class.
     */
    protected List<ASTMethodOrConstructorDeclaration> findOperations(ASTClassOrInterfaceDeclaration node,
                                                                     boolean includeNested) {

        if (includeNested) {
            return node.findDescendantsOfType(ASTMethodOrConstructorDeclaration.class);
        }

        ASTClassOrInterfaceBody body = (ASTClassOrInterfaceBody) node.jjtGetChild(0);
        List<ASTClassOrInterfaceBodyDeclaration> outerDecls
            = body.findChildrenOfType(ASTClassOrInterfaceBodyDeclaration.class);


        List<ASTMethodOrConstructorDeclaration> operations = new ArrayList<>();

        for (ASTClassOrInterfaceBodyDeclaration decl : outerDecls) {
            if (decl.jjtGetChild(0) instanceof ASTMethodOrConstructorDeclaration) {
                operations.add((ASTMethodOrConstructorDeclaration) decl.jjtGetChild(0));
            }
        }
        return operations;
    }
}
