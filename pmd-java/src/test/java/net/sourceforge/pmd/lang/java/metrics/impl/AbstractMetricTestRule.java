/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.metrics.ResultOption;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Abstract test rule for a metric. Tests of metrics use the standard framework for rule testing, using one dummy rule
 * per metric. Default parameters can be overriden by overriding the protected methods of this class.
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetricTestRule extends AbstractJavaMetricsRule {


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
    private JavaClassMetricKey classKey;
    private JavaOperationMetricKey opKey;


    public AbstractMetricTestRule() {
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
    protected abstract JavaClassMetricKey getClassKey();


    /**
     * Returns the class metric key to test, or null if we shouldn't test classes.
     *
     * @return The class metric key to test.
     */
    protected abstract JavaOperationMetricKey getOpKey();


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        reportClasses = getProperty(reportClassesDescriptor);
        reportMethods = getProperty(reportMethodsDescriptor);
        reportLevel = getProperty(reportLevelDescriptor);
        metricOptions = MetricOptions.ofOptions(getProperty(optionsDescriptor));

        return super.visit(node, data);
    }


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
     * Mappings of labels to versions for use in the options property.
     *
     * @return A map of labels to versions
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


    /** Gets a nice string representation of a double. */
    private String niceDoubleString(double val) {
        if (val == (int) val) {
            return String.valueOf((int) val);
        } else {
            return String.format(Locale.ROOT, "%.4f", val);
        }
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        if (classKey != null && reportClasses && classKey.supports(node)) {
            double classValue = MetricsUtil.computeMetric(classKey, node, metricOptions);

            String valueReport = niceDoubleString(classValue);

            if (opKey != null) {
                double highest = JavaMetrics.get(opKey, node, metricOptions, ResultOption.HIGHEST);
                valueReport += " highest " + niceDoubleString(highest);
            }
            if (classValue >= reportLevel) {
                addViolation(data, node, new String[] {node.getQualifiedName().toString(), valueReport, });
            }
        }
        return super.visit(node, data);
    }


    @Override
    public Object visit(MethodLikeNode node, Object data) {
        if (opKey != null && reportMethods && opKey.supports(node)) {
            double methodValue = MetricsUtil.computeMetric(opKey, node, metricOptions);
            if (methodValue >= reportLevel) {
                addViolation(data, node, new String[] {node.getQualifiedName().toString(),
                                                       "" + niceDoubleString(methodValue), });
            }
        }
        return super.visit(node, data);
    }
}
