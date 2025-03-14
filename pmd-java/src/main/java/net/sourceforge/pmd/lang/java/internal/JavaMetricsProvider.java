/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.Metric;

/**
 * @author Clément Fournier
 */
class JavaMetricsProvider implements LanguageMetricsProvider {

    private final Set<Metric<?, ?>> metrics = setOf(
        JavaMetrics.ACCESS_TO_FOREIGN_DATA,
        JavaMetrics.CYCLO,
        JavaMetrics.NPATH,
        JavaMetrics.NCSS,
        JavaMetrics.LINES_OF_CODE,
        JavaMetrics.FAN_OUT,
        JavaMetrics.WEIGHED_METHOD_COUNT,
        JavaMetrics.WEIGHT_OF_CLASS,
        JavaMetrics.NUMBER_OF_ACCESSORS,
        JavaMetrics.NUMBER_OF_PUBLIC_FIELDS,
        JavaMetrics.TIGHT_CLASS_COHESION
    );

    @Override
    public Set<Metric<?, ?>> getMetrics() {
        return metrics;
    }
}
