/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric.NcssOption;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.metrics.ResultOption;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Simple rule for Ncss. Maybe to be enriched with type specific thresholds.
 *
 * @author Clément Fournier
 */
public final class NcssCountRule extends AbstractJavaMetricsRule {


    private static final PropertyDescriptor<Integer> METHOD_REPORT_LEVEL_DESCRIPTOR =
        PropertyFactory.intProperty("methodReportLevel")
                       .desc("NCSS reporting threshold for methods")
                       .require(positive())
                       .defaultValue(60)
                       .build();

    private static final PropertyDescriptor<Integer> CLASS_REPORT_LEVEL_DESCRIPTOR =
        PropertyFactory.intProperty("classReportLevel")
                       .desc("NCSS reporting threshold for classes")
                       .require(positive())
                       .defaultValue(1500)
                       .build();

    private static final PropertyDescriptor<List<NcssOption>> NCSS_OPTIONS_DESCRIPTOR;
    private int methodReportLevel;
    private int classReportLevel;
    private MetricOptions ncssOptions;


    static {
        Map<String, NcssOption> options = new HashMap<>();
        options.put(NcssOption.COUNT_IMPORTS.valueName(), NcssOption.COUNT_IMPORTS);

        NCSS_OPTIONS_DESCRIPTOR = PropertyFactory.enumListProperty("ncssOptions", options)
                                                 .desc("Choose options for the computation of Ncss")
                                                 .emptyDefaultValue()
                                                 .build();

    }


    public NcssCountRule() {
        definePropertyDescriptor(METHOD_REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(CLASS_REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(NCSS_OPTIONS_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        methodReportLevel = getProperty(METHOD_REPORT_LEVEL_DESCRIPTOR);
        classReportLevel = getProperty(CLASS_REPORT_LEVEL_DESCRIPTOR);
        ncssOptions = MetricOptions.ofOptions(getProperty(NCSS_OPTIONS_DESCRIPTOR));

        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {

        super.visit(node, data);

        if (JavaClassMetricKey.NCSS.supports(node)) {
            int classSize = (int) MetricsUtil.computeMetric(JavaClassMetricKey.NCSS, node, ncssOptions);
            int classHighest = (int) JavaMetrics.get(JavaOperationMetricKey.NCSS, node, ncssOptions, ResultOption.HIGHEST);

            if (classSize >= classReportLevel) {
                String[] messageParams = {node.getTypeKind().name().toLowerCase(Locale.ROOT),
                                          node.getImage(),
                                          classSize + " (Highest = " + classHighest + ")", };

                addViolation(data, node, messageParams);
            }
        }
        return data;
    }


    @Override
    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {

        if (JavaOperationMetricKey.NCSS.supports((MethodLikeNode) node)) {
            int methodSize = (int) MetricsUtil.computeMetric(JavaOperationMetricKey.NCSS, node, ncssOptions);
            if (methodSize >= methodReportLevel) {
                addViolation(data, node, new String[] {
                    node instanceof ASTMethodDeclaration ? "method" : "constructor",
                    PrettyPrintingUtil.displaySignature(node), "" + methodSize, });
            }
        }
        return data;
    }

}
