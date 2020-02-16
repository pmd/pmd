/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.metrics.AbstractMetricsComputer;
import net.sourceforge.pmd.lang.metrics.MetricsComputer;

/**
 * Computes metrics for the Apex framework.
 *
 * @author Clément Fournier
 * @deprecated See {@link MetricsComputer}
 */
@Deprecated
public class ApexMetricsComputer extends AbstractMetricsComputer<ASTUserClassOrInterface<?>, ASTMethod> {

    private static final ApexMetricsComputer INSTANCE = new ApexMetricsComputer();

    @Override
    protected List<ASTMethod> findOperations(ASTUserClassOrInterface<?> node) {
        return ApexMetrics.findOps(node);
    }

    @InternalApi
    public static ApexMetricsComputer getInstance() {
        return INSTANCE;
    }

}
