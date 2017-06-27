/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.Metric.Version;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.ResultOption;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * Reports values of a metric that cross a certain threshold.
 *
 * @author Clément Fournier
 */
public abstract class AbstractSimpleJavaMetricsRule extends AbstractJavaMetricsRule {

    private static final PropertyDescriptor<Boolean> SHOW_CLASSES_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
        "reportClasses", "Add class average violations to the report", true, 2.0f);

    private static final PropertyDescriptor<Boolean> SHOW_METHODS_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
        "reportMethods", "Add method average violations to the report", true, 3.0f);

    private static final IntegerProperty REPORT_LEVEL_DESCRIPTOR = new IntegerProperty(
        "reportLevel", "Metric reporting threshold", 1, 30, 10, 1.0f);


    private ClassMetricKey classMetricKey;
    private OperationMetricKey operationMetricKey;
    private boolean reportClasses = true;
    private boolean reportMethods = true;
    private MetricVersion metricVersion = Version.STANDARD;
    private int reportLevel;


    public AbstractSimpleJavaMetricsRule() {
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        definePropertyDescriptor(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
        if (versionDescriptor() != null) {
            definePropertyDescriptor(versionDescriptor());
        }
        classMetricKey = classMetricKey();
        operationMetricKey = operationMetricKey();
    }


    /**
     * Return the property descriptor which selects the version of the metric, if the metric has versions.
     *
     * @return The property descriptor which selects the version of the metric
     */
    protected EnumeratedProperty<MetricVersion> versionDescriptor() {
        return null;
    }


    /**
     * Returns the class metric key to use.
     *
     * @return The class metric key to use
     */
    protected abstract ClassMetricKey classMetricKey();


    /**
     * Returns the operation metric key to use.
     *
     * @return The operation metric key to use
     */
    protected abstract OperationMetricKey operationMetricKey();


    @Override
    public final Object visit(ASTCompilationUnit node, Object data) {
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        reportClasses = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        reportMethods = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
        if (versionDescriptor() != null) {
            Object version = getProperty(versionDescriptor());
            metricVersion = version instanceof MetricVersion ? (MetricVersion) version : Version.STANDARD;
        }
        super.visit(node, data);
        return data;
    }


    @Override
    public final Object visit(ASTAnyTypeDeclaration node, Object data) {
        super.visit(node, data);

        if (reportClasses && classMetricKey != null) {
            int classValue = (int) Metrics.get(classMetricKey, node, metricVersion);
            int classHighest = (int) Metrics.get(operationMetricKey, node, metricVersion, ResultOption.HIGHEST);

            String valueReport = String.valueOf(classValue);

            if (operationMetricKey != null) {
                int highest = (int) Metrics.get(operationMetricKey, node, metricVersion, ResultOption.HIGHEST);
                valueReport += " (Highest = " + highest + ")";
            }


            if (classValue >= reportLevel || classHighest >= reportLevel) {
                String[] messageParams = {node.getTypeKind().name().toLowerCase(),
                                          node.getImage(),
                                          valueReport, };

                addViolation(data, node, messageParams);
            }
        }
        return data;
    }


    @Override
    public final Object visit(ASTMethodOrConstructorDeclaration node, Object data) {

        if (reportMethods && operationMetricKey != null) {
            int cyclo = (int) Metrics.get(operationMetricKey, node, metricVersion);
            if (cyclo >= reportLevel) {
                addViolation(data, node, new String[] {node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                                       node.getQualifiedName().getOperation(), "" + cyclo, });
            }
        }
        return data;
    }

}
