/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.QualifiableNode;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.metrics.ResultOption;


/**
 * Base implementation for {@link LanguageMetricsProvider}.
 *
 * @author Clément Fournier
 * @since 6.11.0
 */
public abstract class AbstractLanguageMetricsProvider<T extends QualifiableNode, O extends QualifiableNode> implements LanguageMetricsProvider<T, O> {

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
    public double computeForType(MetricKey<T> key, T node, MetricOptions options) {
        return MetricsUtil.computeMetric(key, node, options, true);
    }


    @Override
    public double computeForOperation(MetricKey<O> key, O node, MetricOptions options) {
        return MetricsUtil.computeMetric(key, node, options, true);
    }


    @Override
    public double computeWithResultOption(MetricKey<O> key, T node, MetricOptions options, ResultOption resultOption) {
        return MetricsUtil.computeAggregate(key, findOps(node), options, resultOption);
    }

    protected abstract List<O> findOps(T t);


    @Override
    public Map<MetricKey<?>, Double> computeAllMetricsFor(Node node) {
        Map<MetricKey<?>, Double> results = new HashMap<>();
        T t = asTypeNode(node);
        if (t != null) {
            for (MetricKey<T> tkey : getAvailableTypeMetrics()) {
                results.put(tkey, computeForType(tkey, t, MetricOptions.emptyOptions()));
            }
        }
        O o = asOperationNode(node);
        if (o != null) {
            for (MetricKey<O> okey : getAvailableOperationMetrics()) {
                results.put(okey, computeForOperation(okey, o, MetricOptions.emptyOptions()));
            }
        }

        return results;
    }

}
