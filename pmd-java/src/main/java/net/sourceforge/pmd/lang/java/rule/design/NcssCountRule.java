/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics.NcssOption;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.AssertionUtil;


/**
 * Simple rule for Ncss. Maybe to be enriched with type specific thresholds.
 *
 * @author Clément Fournier
 */
public final class NcssCountRule extends AbstractJavaRulechainRule {


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

    static {
        Map<String, NcssOption> options = new HashMap<>();
        options.put(NcssOption.COUNT_IMPORTS.valueName(), NcssOption.COUNT_IMPORTS);

        NCSS_OPTIONS_DESCRIPTOR = PropertyFactory.enumListProperty("ncssOptions", options)
                                                 .desc("Choose options for the computation of Ncss")
                                                 .emptyDefaultValue()
                                                 .build();

    }


    public NcssCountRule() {
        super(ASTMethodOrConstructorDeclaration.class, ASTAnyTypeDeclaration.class);
        definePropertyDescriptor(METHOD_REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(CLASS_REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(NCSS_OPTIONS_DESCRIPTOR);
    }


    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        int methodReportLevel = getProperty(METHOD_REPORT_LEVEL_DESCRIPTOR);
        int classReportLevel = getProperty(CLASS_REPORT_LEVEL_DESCRIPTOR);
        MetricOptions ncssOptions = MetricOptions.ofOptions(getProperty(NCSS_OPTIONS_DESCRIPTOR));

        if (node instanceof ASTAnyTypeDeclaration) {
            visitTypeDecl((ASTAnyTypeDeclaration) node, classReportLevel, ncssOptions, (RuleContext) data);
        } else if (node instanceof ASTMethodOrConstructorDeclaration) {
            visitMethod((ASTMethodOrConstructorDeclaration) node, methodReportLevel, ncssOptions, (RuleContext) data);
        } else {
            throw AssertionUtil.shouldNotReachHere("unreachable");
        }
        return data;
    }


    private void visitTypeDecl(ASTAnyTypeDeclaration node,
                               int level,
                               MetricOptions ncssOptions,
                               RuleContext data) {

        if (JavaMetrics.NCSS.supports(node)) {
            int classSize = MetricsUtil.computeMetric(JavaMetrics.NCSS, node, ncssOptions);
            int classHighest = (int) MetricsUtil.computeStatistics(JavaMetrics.NCSS, node.getOperations(), ncssOptions).getMax();

            if (classSize >= level) {
                String[] messageParams = {PrettyPrintingUtil.getPrintableNodeKind(node),
                                          node.getSimpleName(),
                                          classSize + " (Highest = " + classHighest + ")", };

                addViolation(data, node, messageParams);
            }
        }
    }


    private void visitMethod(ASTMethodOrConstructorDeclaration node,
                             int level,
                             MetricOptions ncssOptions,
                             RuleContext data) {

        if (JavaMetrics.NCSS.supports(node)) {
            int methodSize = MetricsUtil.computeMetric(JavaMetrics.NCSS, node, ncssOptions);
            if (methodSize >= level) {
                addViolation(data, node, new String[] {
                    node instanceof ASTMethodDeclaration ? "method" : "constructor",
                    PrettyPrintingUtil.displaySignature(node), "" + methodSize, });
            }
        }
    }

}
