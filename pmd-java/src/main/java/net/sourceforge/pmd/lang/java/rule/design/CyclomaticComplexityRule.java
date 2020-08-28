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
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.metrics.api.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaMetrics.CycloOption;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Cyclomatic complexity rule using metrics.
 *
 * @author Clément Fournier, based on work by Alan Hohn and Donald A. Leckie
 * @version 6.0.0
 */
public class CyclomaticComplexityRule extends AbstractJavaRule {

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
    public Object visitJavaNode(JavaNode node, Object param) {
        if (node instanceof ASTAnyTypeDeclaration) {
            visitTypeDecl((ASTAnyTypeDeclaration) node, param);
        }
        return null;
    }

    public Object visitTypeDecl(ASTAnyTypeDeclaration node, Object data) {

        super.visitJavaNode(node, data);

        if (JavaMetrics.WEIGHED_METHOD_COUNT.supports(node)) {
            int classWmc = MetricsUtil.computeMetric(JavaMetrics.WEIGHED_METHOD_COUNT, node, cycloOptions);

            if (classWmc >= classReportLevel) {
                int classHighest = (int) MetricsUtil.computeStatistics(JavaMetrics.CYCLO, node.getOperations(), cycloOptions).getMax();

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
    public final Object visit(ASTMethodDeclaration node, Object data) {
        visitMethodLike(node, data);
        return super.visit(node, data);
    }

    @Override
    public final Object visit(ASTConstructorDeclaration node, Object data) {
        visitMethodLike(node, data);
        return super.visit(node, data);
    }

    private void visitMethodLike(ASTMethodOrConstructorDeclaration node, Object data) {
        if (JavaMetrics.CYCLO.supports(node)) {
            int cyclo = MetricsUtil.computeMetric(JavaMetrics.CYCLO, node, cycloOptions);
            if (cyclo >= methodReportLevel) {


                String opname = PrettyPrintingUtil.displaySignature(node);

                String kindname = node instanceof ASTConstructorDeclaration ? "constructor" : "method";


                addViolation(data, node, new String[] {kindname,
                                                       opname,
                                                       "",
                                                       "" + cyclo,});
            }
        }
    }

}
