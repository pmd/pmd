/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.apex.ast.ApexParser;
import net.sourceforge.pmd.lang.apex.internal.ApexDesignerBindings;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileAnalysis;
import net.sourceforge.pmd.lang.apex.rule.internal.ApexRuleViolationFactory;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.processor.BatchLanguageProcessor;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;

public class ApexLanguageProcessor
    extends BatchLanguageProcessor<ApexLanguageProperties>
    implements LanguageVersionHandler {

    private final ApexMetricsProvider myMetricsProvider = new ApexMetricsProvider();
    private final ApexMultifileAnalysis multifileAnalysis;

    ApexLanguageProcessor(ApexLanguageProperties bundle) {
        super(bundle);
        this.multifileAnalysis = new ApexMultifileAnalysis(bundle);
    }

    @Override
    public @NonNull LanguageVersionHandler services() {
        return this;
    }

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return ApexRuleViolationFactory.INSTANCE;
    }

    @Override
    public Parser getParser() {
        return new ApexParser(this);
    }

    @Override
    public LanguageMetricsProvider getLanguageMetricsProvider() {
        return myMetricsProvider;
    }

    @Override
    public DesignerBindings getDesignerBindings() {
        return ApexDesignerBindings.INSTANCE;
    }

    public ApexMultifileAnalysis getMultiFileState() {
        return multifileAnalysis;
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
