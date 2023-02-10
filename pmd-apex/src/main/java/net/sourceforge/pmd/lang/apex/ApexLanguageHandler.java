/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.ViolationSuppressor;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.apex.ast.ApexParser;
import net.sourceforge.pmd.lang.apex.internal.ApexDesignerBindings;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;

class ApexLanguageHandler implements LanguageVersionHandler {

    private final ApexMetricsProvider myMetricsProvider = new ApexMetricsProvider();

    @Override
    public Parser getParser() {
        return new ApexParser();
    }

    @Override
    public List<ViolationSuppressor> getExtraViolationSuppressors() {
        return ApexViolationSuppressors.ALL_APEX_SUPPRESSORS;
    }

    @Override
    public LanguageMetricsProvider getLanguageMetricsProvider() {
        return myMetricsProvider;
    }

    @Override
    public DesignerBindings getDesignerBindings() {
        return ApexDesignerBindings.INSTANCE;
    }

    private static final class ApexMetricsProvider implements LanguageMetricsProvider {

        private final Set<Metric<?, ?>> metrics = setOf(
            ApexMetrics.COGNITIVE_COMPLEXITY,
            ApexMetrics.CYCLO,
            ApexMetrics.WEIGHED_METHOD_COUNT
        );

        @Override
        public Set<Metric<?, ?>> getMetrics() {
            return metrics;
        }
    }
}
