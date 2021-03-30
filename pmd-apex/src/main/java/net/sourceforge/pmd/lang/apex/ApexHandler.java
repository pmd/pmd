/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexParser;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexClassMetricKey;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexOperationMetricKey;
import net.sourceforge.pmd.lang.apex.rule.internal.ApexRuleViolationFactory;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.internal.AbstractLanguageMetricsProvider;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

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
    public LanguageMetricsProvider<ASTUserClassOrInterface<?>, ASTMethod> getLanguageMetricsProvider() {
        return myMetricsProvider;
    }

    private static class ApexMetricsProvider extends AbstractLanguageMetricsProvider<ASTUserClassOrInterface<?>, ASTMethod> {

        @SuppressWarnings("unchecked")
        ApexMetricsProvider() {
            // a wild double cast
            super((Class<ASTUserClassOrInterface<?>>) (Object) ASTUserClassOrInterface.class, ASTMethod.class);
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
}
