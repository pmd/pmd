/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.AbstractMetric;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetric;

/**
 * Base class for operation metrics.
 *
 * @author Clément Fournier
 */
public abstract class AbstractOperationMetric extends AbstractMetric<ASTMethodOrConstructorDeclaration>
    implements OperationMetric {

    /**
     * Returns true if the metric can be computed on this operation. By default, abstract operations are filtered out.
     *
     * @param node The operation
     *
     * @return True if the metric can be computed on this operation
     */
    public boolean supports(ASTMethodOrConstructorDeclaration node) {
        return !node.isAbstract();
    }
}
