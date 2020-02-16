/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.metrics.AbstractMetricsComputer;
import net.sourceforge.pmd.lang.metrics.MetricsComputer;

/**
 * Computes a metric.
 *
 * @author Clément Fournier
 * @deprecated See {@link MetricsComputer}
 */
@Deprecated
public final class JavaMetricsComputer extends AbstractMetricsComputer<ASTAnyTypeDeclaration, MethodLikeNode> {

    private static final JavaMetricsComputer INSTANCE = new JavaMetricsComputer();


    private JavaMetricsComputer() {
    }

    // TODO: doesn't consider lambdas
    @Override
    protected List<MethodLikeNode> findOperations(ASTAnyTypeDeclaration node) {
        return JavaMetrics.findOps(node);
    }

    @InternalApi
    public static JavaMetricsComputer getInstance() {
        return INSTANCE;
    }

}
