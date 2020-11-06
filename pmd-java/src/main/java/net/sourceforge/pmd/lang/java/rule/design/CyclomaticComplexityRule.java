/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric.CycloOption;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.metrics.ResultOption;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Cyclomatic complexity rule using metrics.
 *
 * @author Clément Fournier, based on work by Alan Hohn and Donald A. Leckie
 * @see CycloMetric
 * @version 6.0.0
 */
public class CyclomaticComplexityRule extends AbstractJavaMetricsRule {

    private static final Logger LOG = Logger.getLogger(CyclomaticComplexityRule.class.getName());

    // Deprecated, kept for backwards compatibility (6.0.0)
    @Deprecated
    private static final PropertyDescriptor<Integer> REPORT_LEVEL_DESCRIPTOR
        = PropertyFactory.intProperty("reportLevel")
                         .desc("Deprecated! Cyclomatic Complexity reporting threshold")
                         .require(positive()).defaultValue(10).build();


    private static final PropertyDescriptor<Integer> CLASS_LEVEL_DESCRIPTOR
        = PropertyFactory.intProperty("classReportLevel")
                         .desc("Total class complexity reporting threshold")
                         .require(positive()).defaultValue(80).build();

    private static final PropertyDescriptor<Integer> METHOD_LEVEL_DESCRIPTOR
        = PropertyFactory.intProperty("methodReportLevel")
                         .desc("Cyclomatic complexity reporting threshold")
                         .require(positive()).defaultValue(10).build();

    private static final Map<String, CycloOption> OPTION_MAP;

    static {
        OPTION_MAP = new HashMap<>();
        OPTION_MAP.put(CycloOption.IGNORE_BOOLEAN_PATHS.valueName(), CycloOption.IGNORE_BOOLEAN_PATHS);
        OPTION_MAP.put(CycloOption.CONSIDER_ASSERT.valueName(), CycloOption.CONSIDER_ASSERT);
    }

    private static final PropertyDescriptor<List<CycloOption>> CYCLO_OPTIONS_DESCRIPTOR
            = PropertyFactory.enumListProperty("cycloOptions", OPTION_MAP)
                             .desc("Choose options for the computation of Cyclo")
                             .emptyDefaultValue()
                             .build();

    private int methodReportLevel;
    private int classReportLevel;
    private MetricOptions cycloOptions;


    public CyclomaticComplexityRule() {
        definePropertyDescriptor(CLASS_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(METHOD_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(CYCLO_OPTIONS_DESCRIPTOR);
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
    }

    // Kept for backwards compatibility // TODO remove the property sometime
    private void assignReportLevelsCompat() {
        int methodLevel = getProperty(METHOD_LEVEL_DESCRIPTOR);
        int classLevel = getProperty(CLASS_LEVEL_DESCRIPTOR);
        int commonLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);

        if (methodLevel == METHOD_LEVEL_DESCRIPTOR.defaultValue()
            && classLevel == CLASS_LEVEL_DESCRIPTOR.defaultValue()
            && commonLevel != REPORT_LEVEL_DESCRIPTOR.defaultValue()) {
            LOG.warning("Rule CyclomaticComplexity uses deprecated property 'reportLevel'. "
                        + "Future versions of PMD will remove support for this property. "
                        + "Please use 'methodReportLevel' and 'classReportLevel' instead!");
            methodLevel = commonLevel;
            classLevel = commonLevel * 8;
        }

        methodReportLevel = methodLevel;
        classReportLevel = classLevel;
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        // methodReportLevel = getProperty(METHOD_LEVEL_DESCRIPTOR);
        // classReportLevel = getProperty(CLASS_LEVEL_DESCRIPTOR);
        assignReportLevelsCompat();

        cycloOptions = MetricOptions.ofOptions(getProperty(CYCLO_OPTIONS_DESCRIPTOR));


        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {

        super.visit(node, data);

        if (JavaClassMetricKey.WMC.supports(node)) {
            int classWmc = (int) MetricsUtil.computeMetric(JavaClassMetricKey.WMC, node, cycloOptions);

            if (classWmc >= classReportLevel) {
                int classHighest = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, node, cycloOptions, ResultOption.HIGHEST);

                String[] messageParams = {PrettyPrintingUtil.kindName(node),
                                          node.getSimpleName(),
                                          " total",
                                          classWmc + " (highest " + classHighest + ")", };

                addViolation(data, node, messageParams);
            }
        }
        return data;
    }


    @Override
    public final Object visit(MethodLikeNode node, Object data) {

        if (JavaOperationMetricKey.CYCLO.supports(node)) {
            int cyclo = (int) MetricsUtil.computeMetric(JavaOperationMetricKey.CYCLO, node, cycloOptions);
            if (cyclo >= methodReportLevel) {


                String opname = node instanceof ASTMethodOrConstructorDeclaration
                                ? PrettyPrintingUtil.displaySignature((ASTMethodOrConstructorDeclaration) node)
                                : "lambda";

                String kindname = node instanceof ASTMethodOrConstructorDeclaration
                                  ? node instanceof ASTConstructorDeclaration ? "constructor" : "method"
                                  : "lambda";


                addViolation(data, node, new String[] {kindname,
                                                       opname,
                                                       "",
                                                       "" + cyclo, });
            }
        }
        return data;
    }

}
