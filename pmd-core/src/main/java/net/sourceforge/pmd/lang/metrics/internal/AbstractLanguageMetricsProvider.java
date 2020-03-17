/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics.internal;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;


/**
 * Base implementation for {@link LanguageMetricsProvider}.
 *
 * @author Clément Fournier
 * @since 6.11.0
 */
public abstract class AbstractLanguageMetricsProvider<T extends Node, O extends Node> implements LanguageMetricsProvider<T, O> {

    private final Class<T> tClass;
    private final Class<O> oClass;


    protected AbstractLanguageMetricsProvider(Class<T> tClass,
                                              Class<O> oClass) {
        this.tClass = tClass;
        this.oClass = oClass;
    }


    @Override
    public T asTypeNode(Node anyNode) {
        return tClass.isInstance(anyNode) ? tClass.cast(anyNode) : null;
    }


    @Override
    public O asOperationNode(Node anyNode) {
        return oClass.isInstance(anyNode) ? oClass.cast(anyNode) : null;
    }


    @Override
    public Map<MetricKey<?>, Double> computeAllMetricsFor(Node node) {
        Map<MetricKey<?>, Double> results = new HashMap<>();
        T t = asTypeNode(node);
        if (t != null) {
            for (MetricKey<T> tkey : getAvailableTypeMetrics()) {
                results.put(tkey, MetricsUtil.computeMetric(tkey, t));
            }
        }
        O o = asOperationNode(node);
        if (o != null) {
            for (MetricKey<O> okey : getAvailableOperationMetrics()) {
                results.put(okey, MetricsUtil.computeMetric(okey, o));
            }
        }

        return results;
    }

}
