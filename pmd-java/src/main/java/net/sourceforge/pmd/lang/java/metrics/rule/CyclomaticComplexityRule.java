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
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric.CycloOption;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.ResultOption;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * Cyclomatic complexity rule using metrics.
 *
 * @author Clément Fournier
 */
public class CyclomaticComplexityRule extends AbstractJavaMetricsRule {

    private static final IntegerProperty CLASS_LEVEL_DESCRIPTOR = new IntegerProperty(
        "classReportLevel", "Total class complexity reporting threshold", 1, 600, 80, 1.0f);

    private static final IntegerProperty METHOD_LEVEL_DESCRIPTOR = new IntegerProperty(
        "methodReportLevel", "Cyclomatic complexity reporting threshold", 1, 50, 10, 1.0f);

    private static final Map<String, CycloOption> VERSION_MAP;


    static {
        VERSION_MAP = new HashMap<>();
        VERSION_MAP.put("ignoreBooleanPaths", CycloOption.IGNORE_BOOLEAN_PATHS);
    }


    private static final EnumeratedMultiProperty<CycloOption> CYCLO_OPTIONS_DESCRIPTOR = new EnumeratedMultiProperty<>(
        "cycloOptions", "Choose options for the computation of Cyclo",
        VERSION_MAP, Collections.<CycloOption>emptyList(), CycloOption.class, 3.0f);

    private int methodReportLevel;
    private int classReportLevel;
    private MetricOptions cycloOptions;


    public CyclomaticComplexityRule() {
        definePropertyDescriptor(CLASS_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(METHOD_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(CYCLO_OPTIONS_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        methodReportLevel = getProperty(METHOD_LEVEL_DESCRIPTOR);
        classReportLevel = getProperty(CLASS_LEVEL_DESCRIPTOR);
        if (cycloOptions == null) {
            cycloOptions = MetricOptions.ofOptions(getProperty(CYCLO_OPTIONS_DESCRIPTOR));
        }

        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {

        super.visit(node, data);

        if (JavaClassMetricKey.WMC.supports(node)) {
            int classWmc = (int) JavaMetrics.get(JavaClassMetricKey.WMC, node, cycloOptions);

            if (classWmc >= classReportLevel) {
                int classHighest = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, node, cycloOptions, ResultOption.HIGHEST);

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

        int cyclo = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, node, cycloOptions);
        if (cyclo >= methodReportLevel) {
            addViolation(data, node, new String[] {node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                                   node.getQualifiedName().getOperation(),
                                                   "",
                                                   "" + cyclo, });
        }

        return data;
    }

}
