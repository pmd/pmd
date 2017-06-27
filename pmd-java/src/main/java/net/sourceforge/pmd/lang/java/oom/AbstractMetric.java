/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.oom.api.Metric;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;


/**
 * Base class for metrics. Metric objects encapsulate the computational logic required to compute a metric from a
 * PackageStats and node. They're stateless.
 *
 * TODO:cf all these methods mimic the behaviour of resultoptions, perhaps not great.
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetric implements Metric {

    protected List<QualifiedName> findAllCalls(ASTMethodOrConstructorDeclaration node) {
        List<QualifiedName> result = new ArrayList<>();
        // TODO:cf findAllCalls
        // Needs TypeRes
        // Find the qualified names of all methods called in that method's block
        return result;
    }


    /**
     * Calls more specific methods that can be overriden by subclasses.
     *
     * @param node The node to check
     *
     * @return True if the metric can be computed on this node
     */
    @Override
    public final boolean supports(AccessNode node) {
        return node instanceof ASTAnyTypeDeclaration && supports((ASTAnyTypeDeclaration) node)
            || node instanceof ASTMethodOrConstructorDeclaration && supports((ASTMethodOrConstructorDeclaration) node);
    }


    /**
     * Returns true if the metric can be computed on this type declaration. By default, annotation and interface
     * declarations are filtered out.
     *
     * @param node The type declaration
     *
     * @return True if the metric can be computed on this type declaration
     */
    protected boolean supports(ASTAnyTypeDeclaration node) {
        return node.getTypeKind() != TypeKind.ANNOTATION && node.getTypeKind() != TypeKind.INTERFACE;
    }


    /**
     * Returns true if the metric can be computed on this operation. By default, abstract operations are filtered out.
     *
     * @param node The operation
     *
     * @return True if the metric can be computed on this operation
     */
    protected boolean supports(ASTMethodOrConstructorDeclaration node) {
        return !node.isAbstract();
    }


    /**
     * Gives access to the toplevel package stats to metrics without having to pass them as a parameter to metrics.
     *
     * @return The toplevel package stats (singleton contained within {@link Metrics}.
     */
    protected static PackageStats getTopLevelPackageStats() {
        return Metrics.getTopLevelPackageStats();
    }


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
    protected static double sumMetricOverOperations(ASTAnyTypeDeclaration node, OperationMetricKey key,
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
     * Finds the declaration nodes of all methods or constructors that are declared inside a class.
     *
     * @param node          The class in which to look for.
     * @param includeNested Include operations found in nested classes?
     *
     * @return The list of all operations declared inside the specified class.
     *
     * TODO:cf this one is computed every time
     *
     * TODO:cf it might not be at the best place too (used by ClassStats)
     */
    public static List<ASTMethodOrConstructorDeclaration> findOperations(ASTAnyTypeDeclaration node,
                                                                         boolean includeNested) {

        if (includeNested) {
            return node.findDescendantsOfType(ASTMethodOrConstructorDeclaration.class);
        }

        List<ASTClassOrInterfaceBodyDeclaration> outerDecls
            = node.jjtGetChild(0).findChildrenOfType(ASTClassOrInterfaceBodyDeclaration.class);


        List<ASTMethodOrConstructorDeclaration> operations = new ArrayList<>();

        for (ASTClassOrInterfaceBodyDeclaration decl : outerDecls) {
            if (decl.jjtGetChild(0) instanceof ASTMethodOrConstructorDeclaration) {
                operations.add((ASTMethodOrConstructorDeclaration) decl.jjtGetChild(0));
            }
        }
        return operations;
    }


    /**
     * Gets the average of the value of an operation metric over all operations in this class (excluding nested
     * classes). The computation is not forced (memoized results are used if they can be found).
     *
     * @param node          The class node
     * @param key           The operation metric to use
     * @param version       Version of the metric
     * @param includeNested Adds the operations of nested classes to the sum.
     *
     * @return Returns the average of a metric over all operations of a class.
     */
    protected static double averageMetricOverOperations(ASTAnyTypeDeclaration node, OperationMetricKey key,
                                                        MetricVersion version, boolean includeNested) {

        List<ASTMethodOrConstructorDeclaration> operations = findOperations(node, includeNested);

        double total = 0;
        for (ASTMethodOrConstructorDeclaration op : operations) {
            double val = Metrics.get(key, op, version);
            total += val == Double.NaN ? 0 : val;
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
    protected static double highestMetricOverOperations(ASTAnyTypeDeclaration node, OperationMetricKey key,
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

}
