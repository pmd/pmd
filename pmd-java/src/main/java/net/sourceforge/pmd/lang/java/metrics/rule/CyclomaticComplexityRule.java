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
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * Cyclomatic complexity rule using metrics.
 *
 * @author Clément Fournier
 */
public class CyclomaticComplexityRule extends AbstractJavaMetricsRule {

    private static final IntegerProperty CLASS_LEVEL_DESCRIPTOR = new IntegerProperty(
        "classReportLevel", "Total class complexity reporting threshold", 1, 200, 40, 1.0f);

    private static final IntegerProperty METHOD_LEVEL_DESCRIPTOR = new IntegerProperty(
        "methodReportLevel", "Cyclomatic complexity reporting threshold", 1, 30, 10, 1.0f);

    private static final Map<String, MetricVersion> VERSION_MAP;


    static {
        VERSION_MAP = new HashMap<>();
        VERSION_MAP.put("standard", Version.STANDARD);
        VERSION_MAP.put("ignoreBooleanPaths", CycloVersion.IGNORE_BOOLEAN_PATHS);
    }


    private static final EnumeratedProperty<MetricVersion> CYCLO_VERSION_DESCRIPTOR = new EnumeratedProperty<>(
        "cycloVersion", "Choose a variant of Cyclo or the standard",
        VERSION_MAP, Version.STANDARD, MetricVersion.class, 3.0f);

    private int methodReportLevel;
    private int classReportLevel;
    private MetricVersion cycloVersion = Version.STANDARD;


    public CyclomaticComplexityRule() {
        definePropertyDescriptor(CLASS_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(METHOD_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(CYCLO_VERSION_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        methodReportLevel = getProperty(METHOD_LEVEL_DESCRIPTOR);
        classReportLevel = getProperty(CLASS_LEVEL_DESCRIPTOR);
        cycloVersion = getProperty(CYCLO_VERSION_DESCRIPTOR);

        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {

        super.visit(node, data);

        if (JavaClassMetricKey.WMC.supports(node)) {
            int classWmc = (int) JavaMetrics.get(JavaClassMetricKey.WMC, node, cycloVersion);

            if (classWmc >= classReportLevel) {
                int classHighest = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, node, cycloVersion, ResultOption.HIGHEST);

                String[] messageParams = {node.getTypeKind().name().toLowerCase(),
                                          node.getImage(),
                                          " total",
                                          classWmc + " (highest " + classHighest + ")", };

                addViolation(data, node, messageParams);
            }
        }
        return data;
    }


    @Override
    public final Object visit(ASTMethodOrConstructorDeclaration node, Object data) {

        int cyclo = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, node, cycloVersion);
        if (cyclo >= methodReportLevel) {
            addViolation(data, node, new String[] {node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                                   node.getQualifiedName().getOperation(),
                                                   "",
                                                   "" + cyclo, });
        }

        return data;
    }

}
