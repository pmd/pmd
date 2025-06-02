/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import java.util.Optional;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;


/**
 * Implements the {@code metric()} XPath function. Takes the
 * string name of a metric and the context node and returns
 * the result if the metric can be computed, otherwise returns
 * {@link Double#NaN}.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public final class MetricFunction extends BaseJavaXPathFunction {

    public static final MetricFunction INSTANCE = new MetricFunction();


    private MetricFunction() {
        super("metric");
    }

    @Override
    public Type[] getArgumentTypes() {
        return new Type[] {Type.SINGLE_STRING};
    }


    @Override
    public Type getResultType() {
        return Type.OPTIONAL_DECIMAL;
    }


    @Override
    public boolean dependsOnContext() {
        return true;
    }


    @Override
    public FunctionCall makeCallExpression() {
        return (contextNode, arguments) -> {
            String metricKey = arguments[0].toString();
            return getMetric(contextNode, metricKey);
        };
    }


    static String badMetricKeyMessage(String constantName) {
        return String.format("'%s' is not the name of a metric", constantName);
    }


    private static Optional<Double> getMetric(Node n, String metricKeyName) throws XPathFunctionException {
        LanguageMetricsProvider provider =
            n.getAstInfo().getLanguageProcessor().services().getLanguageMetricsProvider();
        Metric<?, ?> metric = provider.getMetricWithName(metricKeyName);
        if (metric == null) {
            throw new XPathFunctionException(badMetricKeyMessage(metricKeyName));
        }

        Number computed = Metric.compute(metric, n, MetricOptions.emptyOptions());
        return computed == null ? Optional.empty() : Optional.of(computed.doubleValue());
    }

}
