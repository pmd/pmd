/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.metric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics.NcssOption;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.rule.MetricRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Simple rule for Ncss.
 *
 * @author Maximilian Waidelich
 */
public class NcssCountRule extends AbstractJavaRulechainRule implements MetricRule {

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
        super(ASTExecutableDeclaration.class, ASTTypeDeclaration.class);
        definePropertyDescriptor(NCSS_OPTIONS_DESCRIPTOR);
    }


    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        MetricOptions ncssOptions = MetricOptions.ofOptions(getProperty(NCSS_OPTIONS_DESCRIPTOR));

        if (node instanceof ASTTypeDeclaration) {
            visitTypeDecl((ASTTypeDeclaration) node, ncssOptions, (RuleContext) data);
        } else if (node instanceof ASTExecutableDeclaration) {
            visitMethod((ASTExecutableDeclaration) node, ncssOptions, (RuleContext) data);
        } else {
            throw AssertionUtil.shouldNotReachHere("node is not handled: " + node);
        }
        return data;
    }


    private void visitTypeDecl(ASTTypeDeclaration node,
                               MetricOptions ncssOptions,
                               RuleContext data) {

        if (JavaMetrics.NCSS.supports(node)) {
            int classSize = MetricsUtil.computeMetric(JavaMetrics.NCSS, node, ncssOptions);

            MetricRule.addViolation(node, asCtx(data), "NCSS", Type.CLASS,
                    node.getSimpleName(), classSize);
        }
    }


    private void visitMethod(ASTExecutableDeclaration node,
                             MetricOptions ncssOptions,
                             RuleContext data) {

        if (JavaMetrics.NCSS.supports(node)) {
            int methodSize = MetricsUtil.computeMetric(JavaMetrics.NCSS, node, ncssOptions);
            MetricRule.addViolation(node, asCtx(data), "NCSS", Type.METHOD,
                    PrettyPrintingUtil.displaySignature(node), methodSize);
        }
    }

}
