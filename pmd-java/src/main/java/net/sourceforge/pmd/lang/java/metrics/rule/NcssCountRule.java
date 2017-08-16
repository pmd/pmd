/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.rule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric.NcssOption;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.ResultOption;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * Simple rule for Ncss. Maybe to be enriched with type specific thresholds.
 *
 * @author Clément Fournier
 */
public final class NcssCountRule extends AbstractJavaMetricsRule {


    private static final IntegerProperty METHOD_REPORT_LEVEL_DESCRIPTOR = new IntegerProperty(
        "methodReportLevel", "Metric reporting threshold for methods", 1, 60, 12, 1.0f);

    private static final IntegerProperty CLASS_REPORT_LEVEL_DESCRIPTOR = new IntegerProperty(
        "classReportLevel", "Metric reporting threshold for classes", 1, 1000, 250, 1.0f);

    private static final Map<String, MetricOption> OPTION_MAP;


    static {
        OPTION_MAP = new HashMap<>();
        OPTION_MAP.put("javaNcss", NcssOption.COUNT_IMPORTS);
    }


    private static final EnumeratedMultiProperty<MetricOption> NCSS_VERSION_DESCRIPTOR = new EnumeratedMultiProperty<>(
        "ncssOptions", "Choose options for the calculation of Ncss",
        OPTION_MAP, Collections.<MetricOption>emptyList(), MetricOption.class, 3.0f);


    private int methodReportLevel;
    private int classReportLevel;
    private MetricOption[] ncssOptions;


    public NcssCountRule() {
        definePropertyDescriptor(METHOD_REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(CLASS_REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(NCSS_VERSION_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        methodReportLevel = getProperty(METHOD_REPORT_LEVEL_DESCRIPTOR);
        classReportLevel = getProperty(CLASS_REPORT_LEVEL_DESCRIPTOR);
        ncssOptions = getProperty(NCSS_VERSION_DESCRIPTOR).toArray(new MetricOption[0]);

        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {

        super.visit(node, data);

        if (JavaClassMetricKey.NCSS.supports(node)) {
            int classSize = (int) JavaMetrics.get(JavaClassMetricKey.NCSS, node, ncssOptions);
            int classHighest = (int) JavaMetrics.get(JavaOperationMetricKey.NCSS, node, ResultOption.HIGHEST, ncssOptions);

            if (classSize >= classReportLevel) {
                String[] messageParams = {node.getTypeKind().name().toLowerCase(),
                                          node.getImage(),
                                          classSize + " (Highest = " + classHighest + ")", };

                addViolation(data, node, messageParams);
            }
        }
        return data;
    }


    @Override
    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {

        int methodSize = (int) JavaMetrics.get(JavaOperationMetricKey.NCSS, node, ncssOptions);
        if (methodSize >= methodReportLevel) {
            addViolation(data, node, new String[] {node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                                   node.getQualifiedName().getOperation(), "" + methodSize, });
        }

        return data;
    }

}
