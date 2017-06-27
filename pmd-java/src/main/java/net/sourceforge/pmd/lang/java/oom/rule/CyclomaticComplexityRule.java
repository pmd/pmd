/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.rule;

import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.Metric;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric;
import net.sourceforge.pmd.lang.java.rule.AbstractSimpleJavaMetricsRule;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;

/**
 * Refactored to use metrics.
 *
 * @author Clément Fournier
 */
public class CyclomaticComplexityRule extends AbstractSimpleJavaMetricsRule {

    // TODO:cf clean that up once the new property API is up
    private static final String[] VERSION_LABELS = {"standard", "ignoreBooleanPaths"};
    private static final MetricVersion[] CYCLO_VERSIONS = {Metric.Version.STANDARD, CycloMetric.Version.IGNORE_BOOLEAN_PATHS};

    private static final EnumeratedProperty<MetricVersion> CYCLO_VERSION_DESCRIPTOR = new EnumeratedProperty<>(
        "cycloVersion", "Choose a variant of Cyclo or the standard",
        VERSION_LABELS, CYCLO_VERSIONS, 0, 3.0f);


    @Override
    protected EnumeratedProperty<MetricVersion> versionDescriptor() {
        return CYCLO_VERSION_DESCRIPTOR;
    }


    @Override
    protected ClassMetricKey classMetricKey() {
        return ClassMetricKey.CYCLO;
    }


    @Override
    protected OperationMetricKey operationMetricKey() {
        return OperationMetricKey.CYCLO;
    }
}
