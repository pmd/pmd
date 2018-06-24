/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
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
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.ResultOption;
import net.sourceforge.pmd.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.properties.IntegerProperty;

/**
 * Simple rule for Ncss. Maybe to be enriched with type specific thresholds.
 *
 * @author Clément Fournier
 */
public final class NcssCountRule extends AbstractJavaMetricsRule {


    private static final IntegerProperty METHOD_REPORT_LEVEL_DESCRIPTOR =
            IntegerProperty.named("methodReportLevel")
                           .desc("NCSS reporting threshold for methods")
                           .range(1, 2000)
                           .defaultValue(60)
                           .build();

    private static final IntegerProperty CLASS_REPORT_LEVEL_DESCRIPTOR =
            IntegerProperty.named("classReportLevel")
                           .desc("NCSS reporting threshold for classes")
                           .range(1, 20000)
                           .defaultValue(1500)
                           .build();

    private static final Map<String, NcssOption> OPTION_MAP;


    static {
        OPTION_MAP = new HashMap<>();
        OPTION_MAP.put(NcssOption.COUNT_IMPORTS.valueName(), NcssOption.COUNT_IMPORTS);
    }


    private static final EnumeratedMultiProperty<NcssOption> NCSS_OPTIONS_DESCRIPTOR = new EnumeratedMultiProperty<>(
        "ncssOptions", "Choose options for the calculation of Ncss",
        OPTION_MAP, Collections.<NcssOption>emptyList(), NcssOption.class, 3.0f);


    private int methodReportLevel;
    private int classReportLevel;
    private MetricOptions ncssOptions;


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
            int classSize = (int) JavaMetrics.get(JavaClassMetricKey.NCSS, node, ncssOptions);
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

        int methodSize = (int) JavaMetrics.get(JavaOperationMetricKey.NCSS, node, ncssOptions);
        if (methodSize >= methodReportLevel) {
            addViolation(data, node, new String[] {node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                                   node.getQualifiedName().getOperation(), "" + methodSize, });
        }

        return data;
    }

}
