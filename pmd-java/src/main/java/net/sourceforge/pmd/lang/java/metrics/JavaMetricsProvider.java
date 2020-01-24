/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.internal.AbstractLanguageMetricsProvider;

@InternalApi
public class JavaMetricsProvider extends AbstractLanguageMetricsProvider<ASTAnyTypeDeclaration, MethodLikeNode> {

    public JavaMetricsProvider() {
        super(ASTAnyTypeDeclaration.class, MethodLikeNode.class, JavaMetricsComputer.getInstance());
    }

    @Override
    public void initialize() {
        JavaMetrics.reset();
    }

    @Override
    public List<? extends MetricKey<ASTAnyTypeDeclaration>> getAvailableTypeMetrics() {
        return Arrays.asList(JavaClassMetricKey.values());
    }


    @Override
    public List<? extends MetricKey<MethodLikeNode>> getAvailableOperationMetrics() {
        return Arrays.asList(JavaOperationMetricKey.values());
    }
}
