/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.Metric;
import net.sourceforge.pmd.lang.java.oom.api.Metric.Version;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.ResultOption;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.DoubleProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;

/**
 * Abstract test rule for a metric. Tests of metrics use the standard framework for rule testing, using one dummy rule
 * per metric. Default parameters can be overriden by overriding the protected methods of this class.
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetricTestRule extends AbstractJavaMetricsRule {

    private final EnumeratedProperty<MetricVersion> versionDescriptor = new EnumeratedProperty<>(
        "metricVersion", "Choose a variant of the metric or the standard",
        versionLabels(), versionValues(), 0, 3.0f);
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
    private ClassMetricKey classKey;
    private OperationMetricKey opKey;


    public AbstractMetricTestRule() {
        classKey = getClassKey();
        opKey = getOpKey();

        if (versionValues()[0] != Version.STANDARD) {
            throw new RuntimeException("The versions array must begin with the standard version");
        }

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
    protected abstract ClassMetricKey getClassKey();


    /**
     * Returns the class metric key to test, or null if we shouldn't test classes.
     *
     * @return The class metric key to test.
     */
    protected abstract OperationMetricKey getOpKey();


    /**
     * Values of the version.
     *
     * @return Array of the ordered values
     */
    protected MetricVersion[] versionValues() {
        return new MetricVersion[] {Version.STANDARD}; // TODO simplify that after #479
    }


    public Object visit(ASTCompilationUnit node, Object data) {
        reportClasses = getProperty(reportClassesDescriptor);
        reportMethods = getProperty(reportMethodsDescriptor);
        reportLevel = getProperty(reportLevelDescriptor);
        Object version = getProperty(versionDescriptor);
        metricVersion = version instanceof MetricVersion ? (MetricVersion) version : Metric.Version.STANDARD;

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
     * Labels of the versions.
     *
     * @return Array of the ordered labels
     */
    protected String[] versionLabels() {
        return new String[] {"standard"};
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
    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        if (classKey != null && reportClasses && classKey.supports(node)) {
            int classValue = (int) Metrics.get(classKey, node, metricVersion);

            String valueReport = String.valueOf(classValue);

            if (opKey != null) {
                int highest = (int) Metrics.get(opKey, node, metricVersion, ResultOption.HIGHEST);
                valueReport += " highest " + highest;
            }
            if (classValue >= reportLevel) {
                addViolation(data, node, new String[] {node.getQualifiedName().toString(), valueReport});
            }
        }
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        if (opKey != null && reportMethods && opKey.supports(node)) {
            int methodValue = (int) Metrics.get(opKey, node, metricVersion);
            if (methodValue >= reportLevel) {
                addViolation(data, node, new String[] {node.getQualifiedName().toString(), "" + methodValue});
            }
        }
        return data;
    }

}
