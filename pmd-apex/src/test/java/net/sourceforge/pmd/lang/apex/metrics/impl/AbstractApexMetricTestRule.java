/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexClassMetricKey;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexOperationMetricKey;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.metrics.ResultOption;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Abstract test rule for a metric. Tests of metrics use the standard framework for rule testing, using one dummy rule
 * per metric. Default parameters can be overridden by overriding the protected methods of this class.
 *
 * @author Clément Fournier
 */
public abstract class AbstractApexMetricTestRule extends AbstractApexRule {

    private final PropertyDescriptor<List<MetricOption>> optionsDescriptor =
            PropertyFactory.enumListProperty("metricOptions", optionMappings())
                           .desc("Choose a variant of the metric or the standard")
                           .emptyDefaultValue().build();

    private final PropertyDescriptor<Boolean> reportClassesDescriptor =
            PropertyFactory.booleanProperty("reportClasses")
                           .desc("Add class violations to the report")
                           .defaultValue(isReportClasses()).build();

    private final PropertyDescriptor<Boolean> reportMethodsDescriptor =
            PropertyFactory.booleanProperty("reportMethods")
                           .desc("Add method violations to the report")
                           .defaultValue(isReportMethods()).build();

    private final PropertyDescriptor<Double> reportLevelDescriptor =
            PropertyFactory.doubleProperty("reportLevel")
                           .desc("Minimum value required to report")
                           .defaultValue(defaultReportLevel()).build();

    private MetricOptions metricOptions;
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
        definePropertyDescriptor(optionsDescriptor);
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
     * Mappings of labels to options for use in the options property.
     *
     * @return A map of labels to options
     */
    protected Map<String, MetricOption> optionMappings() {
        return new HashMap<>();
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
        if (metricOptions == null) {
            metricOptions = MetricOptions.ofOptions(getProperty(optionsDescriptor));
        }

        if (classKey != null && reportClasses && classKey.supports(node)) {
            int classValue = (int) MetricsUtil.computeMetric(classKey, node, metricOptions);

            String valueReport = String.valueOf(classValue);

            if (opKey != null) {
                int highest = (int) ApexMetrics.get(opKey, node, metricOptions, ResultOption.HIGHEST);
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
            int methodValue = (int) MetricsUtil.computeMetric(opKey, node, metricOptions);
            if (methodValue >= reportLevel) {
                addViolation(data, node, new String[] {node.getQualifiedName().toString(), "" + methodValue});
            }
        }
        return data;
    }
}
