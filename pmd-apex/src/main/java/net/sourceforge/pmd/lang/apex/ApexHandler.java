/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.apex.ast.ApexParser;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.apex.rule.internal.ApexRuleViolationFactory;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

@InternalApi
public class ApexHandler extends AbstractPmdLanguageVersionHandler {

    private final ApexMetricsProvider myMetricsProvider = new ApexMetricsProvider();


    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return ApexRuleViolationFactory.INSTANCE;
    }

    @Override
    public ParserOptions getDefaultParserOptions() {
        return new ApexParserOptions();
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new ApexParser(parserOptions);
    }


    @Override
    public LanguageMetricsProvider getLanguageMetricsProvider() {
        return myMetricsProvider;
    }

    private static class ApexMetricsProvider implements LanguageMetricsProvider {


        @Override
        public Set<Metric<?, ?>> getMetrics() {
            return setOf(
                ApexMetrics.COGNITIVE_COMPLEXITY,
                ApexMetrics.CYCLO,
                ApexMetrics.WEIGHED_METHOD_COUNT
            );
        }
    }
}
