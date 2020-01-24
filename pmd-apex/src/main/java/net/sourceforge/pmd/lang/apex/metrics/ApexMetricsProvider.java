/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexClassMetricKey;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexOperationMetricKey;
import net.sourceforge.pmd.lang.metrics.internal.AbstractLanguageMetricsProvider;

@InternalApi
public class ApexMetricsProvider extends AbstractLanguageMetricsProvider<ASTUserClassOrInterface<?>, ASTMethod> {

    @SuppressWarnings("unchecked")
    public ApexMetricsProvider() {
        // a wild double cast
        super((Class<ASTUserClassOrInterface<?>>) (Object) ASTUserClassOrInterface.class, ASTMethod.class, ApexMetricsComputer.getInstance());
    }

    @Override
    public void initialize() {
        ApexMetrics.reset();
    }

    @Override
    public List<ApexClassMetricKey> getAvailableTypeMetrics() {
        return Arrays.asList(ApexClassMetricKey.values());
    }


    @Override
    public List<ApexOperationMetricKey> getAvailableOperationMetrics() {
        return Arrays.asList(ApexOperationMetricKey.values());
    }
}
