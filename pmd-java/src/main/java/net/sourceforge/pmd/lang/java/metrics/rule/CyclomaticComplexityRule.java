/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.rule;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric.CycloVersion;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;
import net.sourceforge.pmd.lang.metrics.Metric.Version;
import net.sourceforge.pmd.lang.metrics.MetricVersion;
import net.sourceforge.pmd.lang.metrics.ResultOption;
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


    private static final Map<String, MetricVersion> VERSION_MAP;


    static {
        VERSION_MAP = new HashMap<>();
        VERSION_MAP.put("standard", Version.STANDARD);
        VERSION_MAP.put("ignoreBooleanPaths", CycloVersion.IGNORE_BOOLEAN_PATHS);
    }


    private static final EnumeratedProperty<MetricVersion> CYCLO_VERSION_DESCRIPTOR = new EnumeratedProperty<>(
        "cycloVersion", "Choose a variant of Cyclo or the standard",
        VERSION_MAP, Version.STANDARD, MetricVersion.class, 3.0f);

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
        cycloVersion = getProperty(CYCLO_VERSION_DESCRIPTOR);
        reportClasses = getProperty(REPORT_CLASSES_DESCRIPTOR);
        reportMethods = getProperty(REPORT_METHODS_DESCRIPTOR);

        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {

        super.visit(node, data);

        if (reportClasses && JavaClassMetricKey.CYCLO.supports(node)) {
            int classCyclo = (int) JavaMetrics.get(JavaClassMetricKey.CYCLO, node, cycloVersion);
            int classHighest = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, node, cycloVersion, ResultOption.HIGHEST);

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
            int cyclo = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, node, cycloVersion);
            if (cyclo >= reportLevel) {
                addViolation(data, node, new String[] {node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                                       node.getQualifiedName().getOperation(), "" + cyclo, });
            }
        }
        return data;
    }

}
