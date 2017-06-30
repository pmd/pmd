/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.oom.api.Metric;


/**
 * Base class for metrics. Metric objects encapsulate the computational logic required to compute a metric from a
 * PackageStats and node. They're stateless.
 *
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

}
