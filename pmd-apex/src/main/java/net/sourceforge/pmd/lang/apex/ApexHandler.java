/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.apex.ast.ApexParser;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.apex.rule.internal.ApexRuleViolationFactory;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.properties.PropertySource;

@InternalApi
public class ApexHandler extends AbstractPmdLanguageVersionHandler {

    private final ApexMetricsProvider myMetricsProvider = new ApexMetricsProvider();


    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return ApexRuleViolationFactory.INSTANCE;
    }

    @Override
    public Parser getParser() {
        return new ApexParser();
    }

    @Override
    public void declareParserTaskProperties(PropertySource source) {
        source.definePropertyDescriptor(ApexParser.MULTIFILE_DIRECTORY);
        overridePropertiesFromEnv(ApexLanguageModule.TERSE_NAME, source);
    }

    @Override
    public LanguageMetricsProvider getLanguageMetricsProvider() {
        return myMetricsProvider;
    }

    private static class ApexMetricsProvider implements LanguageMetricsProvider {

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
