/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.rule;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.Metric;
import net.sourceforge.pmd.lang.java.oom.api.Metric.Version;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.ResultOption;
import net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric.CycloVersion;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * Cyclomatic complexity rule using metrics.
 *
 * @author Clément Fournier
 */
public class CyclomaticComplexityRule extends AbstractJavaMetricsRule {

    private static final IntegerProperty REPORT_LEVEL_DESCRIPTOR = new IntegerProperty(
        "reportLevel", "Cyclomatic Complexity reporting threshold", 1, 30, 10, 1.0f);

    private static final BooleanProperty REPORT_CLASSES_DESCRIPTOR = new BooleanProperty(
        "reportClasses", "Add class average violations to the report", true, 2.0f);

    private static final BooleanProperty REPORT_METHODS_DESCRIPTOR = new BooleanProperty(
        "reportMethods", "Add method average violations to the report", true, 3.0f);


    private static final String[] VERSION_LABELS = {"standard", "ignoreBooleanPaths"};

    private static final MetricVersion[] CYCLO_VERSIONS = {Metric.Version.STANDARD, CycloVersion.IGNORE_BOOLEAN_PATHS};

    private static final EnumeratedProperty<MetricVersion> CYCLO_VERSION_DESCRIPTOR = new EnumeratedProperty<>(
        "cycloVersion", "Choose a variant of Cyclo or the standard",
        VERSION_LABELS, CYCLO_VERSIONS, 0, 3.0f);

    private int reportLevel;
    private boolean reportClasses = true;
    private boolean reportMethods = true;
    private MetricVersion cycloVersion = Version.STANDARD;


    public CyclomaticComplexityRule() {
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(REPORT_CLASSES_DESCRIPTOR);
        definePropertyDescriptor(REPORT_METHODS_DESCRIPTOR);
        definePropertyDescriptor(CYCLO_VERSION_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        reportClasses = getProperty(REPORT_CLASSES_DESCRIPTOR);
        reportMethods = getProperty(REPORT_METHODS_DESCRIPTOR);
        Object version = getProperty(CYCLO_VERSION_DESCRIPTOR);
        cycloVersion = version instanceof MetricVersion ? (MetricVersion) version : Version.STANDARD;

        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {

        super.visit(node, data);

        if (reportClasses && ClassMetricKey.CYCLO.supports(node)) {
            int classCyclo = (int) Metrics.get(ClassMetricKey.CYCLO, node, cycloVersion);
            int classHighest = (int) Metrics.get(OperationMetricKey.CYCLO, node, cycloVersion, ResultOption.HIGHEST);

            if (classCyclo >= reportLevel || classHighest >= reportLevel) {
                String[] messageParams = {node.getTypeKind().name().toLowerCase(),
                                          node.getImage(),
                                          classCyclo + " (Highest = " + classHighest + ")", };

                addViolation(data, node, messageParams);
            }
        }
        return data;
    }


    @Override
    public final Object visit(ASTMethodOrConstructorDeclaration node, Object data) {

        if (reportMethods) {
            int cyclo = (int) Metrics.get(OperationMetricKey.CYCLO, node, cycloVersion);
            if (cyclo >= reportLevel) {
                addViolation(data, node, new String[] {node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                                       node.getQualifiedName().getOperation(), "" + cyclo, });
            }
        }
        return data;
    }

}
