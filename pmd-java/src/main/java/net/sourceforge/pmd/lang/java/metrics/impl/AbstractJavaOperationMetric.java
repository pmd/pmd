/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.ast.MethodLike;
import net.sourceforge.pmd.lang.java.metrics.AbstractJavaMetric;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetric;


/**
 * Base class for operation metrics. Can be applied on method and constructor declarations, and
 * lambda expressions.
 *
 * @author Clément Fournier
 */
public abstract class AbstractJavaOperationMetric extends AbstractJavaMetric<MethodLike>
        implements JavaOperationMetric {

    /**
     * Returns true if the metric can be computed on this operation. By default, abstract operations are filtered out.
     *
     * @param node The operation
     *
     * @return True if the metric can be computed on this operation
     */
    public boolean supports(MethodLike node) {
        return !node.isAbstract();
    }
}
