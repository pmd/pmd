/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexClassMetricKey;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexOperationMetricKey;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.metrics.Metric.Version;
import net.sourceforge.pmd.lang.metrics.MetricVersion;
import net.sourceforge.pmd.lang.metrics.ResultOption;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.DoubleProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;

/**
 * Abstract test rule for a metric. Tests of metrics use the standard framework for rule testing, using one dummy rule
 * per metric. Default parameters can be overriden by overriding the protected methods of this class.
 *
 * @author Clément Fournier
 */
public abstract class AbstractApexMetricTestRule extends AbstractApexRule {

    private final EnumeratedProperty<MetricVersion> versionDescriptor = new EnumeratedProperty<>(
        "metricVersion", "Choose a variant of the metric or the standard",
        versionMappings(), Version.STANDARD, MetricVersion.class, 3.0f);
    private final BooleanProperty reportClassesDescriptor = new BooleanProperty(
        "reportClasses", "Add class violations to the report", isReportClasses(), 2.0f);
    private final BooleanProperty reportMethodsDescriptor = new BooleanProperty(
        "reportMethods", "Add method violations to the report", isReportMethods(), 3.0f);
    private final DoubleProperty reportLevelDescriptor = new DoubleProperty(
        "reportLevel", "Minimum value required to report", -1., Double.POSITIVE_INFINITY, defaultReportLevel(), 3.0f);

    private MetricVersion metricVersion;
    private boolean reportClasses;
    private boolean reportMethods;
    private double reportLevel;
    private ApexClassMetricKey classKey;
    private ApexOperationMetricKey opKey;


    public AbstractApexMetricTestRule() {
        classKey = getClassKey();
        opKey = getOpKey();

        definePropertyDescriptor(reportClassesDescriptor);
        definePropertyDescriptor(reportMethodsDescriptor);
        definePropertyDescriptor(reportLevelDescriptor);
        definePropertyDescriptor(versionDescriptor);
    }


    /**
     * Returns the class metric key to test, or null if we shouldn't test classes.
     *
     * @return The class metric key to test.
     */
    protected abstract ApexClassMetricKey getClassKey();


    /**
     * Returns the class metric key to test, or null if we shouldn't test classes.
     *
     * @return The class metric key to test.
     */
    protected abstract ApexOperationMetricKey getOpKey();


    /**
     * Sets the default for reportClasses descriptor.
     *
     * @return The default for reportClasses descriptor
     */
    protected boolean isReportClasses() {
        return true;
    }


    /**
     * Sets the default for reportMethods descriptor.
     *
     * @return The default for reportMethods descriptor
     */
    protected boolean isReportMethods() {
        return true;
    }


    /**
     * Mappings of labels to versions for use in the version property.
     *
     * @return A map of labels to versions
     */
    protected Map<String, MetricVersion> versionMappings() {
        Map<String, MetricVersion> mappings = new HashMap<>();
        mappings.put("standard", Version.STANDARD);
        return mappings;
    }


    /**
     * Default report level, which is 0.
     *
     * @return The default report level.
     */
    protected double defaultReportLevel() {
        return 0.;
    }


    @Override
    public Object visit(ASTUserClass node, Object data) {
        reportClasses = getProperty(reportClassesDescriptor);
        reportMethods = getProperty(reportMethodsDescriptor);
        reportLevel = getProperty(reportLevelDescriptor);
        metricVersion = getProperty(versionDescriptor);


        if (classKey != null && reportClasses && classKey.supports(node)) {
            int classValue = (int) ApexMetrics.get(classKey, node, metricVersion);

            String valueReport = String.valueOf(classValue);

            if (opKey != null) {
                int highest = (int) ApexMetrics.get(opKey, node, metricVersion, ResultOption.HIGHEST);
                valueReport += " highest " + highest;
            }
            if (classValue >= reportLevel) {
                addViolation(data, node, new String[] {node.getQualifiedName().toString(), valueReport});
            }
        }
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTMethod node, Object data) {
        if (opKey != null && reportMethods && opKey.supports(node)) {
            int methodValue = (int) ApexMetrics.get(opKey, node, metricVersion);
            if (methodValue >= reportLevel) {
                addViolation(data, node, new String[] {node.getQualifiedName().toString(), "" + methodValue});
            }
        }
        return data;
    }
}
