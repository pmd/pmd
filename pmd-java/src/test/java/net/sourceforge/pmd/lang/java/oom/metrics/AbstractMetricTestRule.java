/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.Metric;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.ResultOption;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.DoubleProperty;

/**
 * Abstract test for a metric.
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetricTestRule extends AbstractJavaRule {

    public static final BooleanProperty REPORT_CLASSES_DESCRIPTOR = new BooleanProperty(
        "reportClasses", "Add class violations to the report", true, 2.0f);
    public static final BooleanProperty REPORT_METHODS_DESCRIPTOR = new BooleanProperty(
        "reportMethods", "Add method violations to the report", true, 3.0f);

    public static final DoubleProperty REPORT_LEVEL_DESCRIPTOR = new DoubleProperty(
        "reportLevel", "Minimum value required to report", -1., Double.POSITIVE_INFINITY, 0., 3.0f);


    protected boolean reportClasses = true;
    protected boolean reportMethods = true;
    protected double reportLevel = 0.;
    protected MetricVersion version = Metric.Version.STANDARD;
    private ClassMetricKey classKey;
    private OperationMetricKey opKey;

    public AbstractMetricTestRule() {
        classKey = getClassKey();
        opKey = getOpKey();

        definePropertyDescriptor(REPORT_CLASSES_DESCRIPTOR);
        definePropertyDescriptor(REPORT_METHODS_DESCRIPTOR);
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        reportClasses = getProperty(REPORT_CLASSES_DESCRIPTOR);
        reportMethods = getProperty(REPORT_METHODS_DESCRIPTOR);
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        return super.visit(node, data);
    }


    protected abstract ClassMetricKey getClassKey();

    protected abstract OperationMetricKey getOpKey();


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (classKey != null && reportClasses && classKey.supports(node)) {
            int classValue = (int) Metrics.get(classKey, node, version);

            String valueReport = String.valueOf(classValue);

            if (opKey != null) {
                int highest = (int) Metrics.get(opKey, node, version, ResultOption.HIGHEST);
                valueReport += " highest " + highest;
            }
            if (classValue >= reportLevel) {
                addViolation(data, node, new String[] {node.getQualifiedName().toString(), valueReport});
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        return processMethodOrConstructorDeclaration(node, data);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        return processMethodOrConstructorDeclaration(node, data);
    }

    protected Object processMethodOrConstructorDeclaration(ASTMethodOrConstructorDeclaration node, Object data) {
        if (opKey != null && reportMethods && opKey.supports(node)) {
            int methodValue = (int) Metrics.get(opKey, node, version);
            if (methodValue >= reportLevel) {
                addViolation(data, node, new String[] {node.getQualifiedName().toString(), "" + methodValue});
            }
        }
        return data;
    }

}
