/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.metric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics.CycloOption;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.rule.MetricRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;


/**
 * Cyclomatic complexity rule using metrics.
 *
 * @author Maximilian Waidelich
 */
public class CyclomaticComplexityRule extends AbstractJavaRulechainRule implements MetricRule {

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


    public CyclomaticComplexityRule() {
        super(ASTExecutableDeclaration.class);
        definePropertyDescriptor(CYCLO_OPTIONS_DESCRIPTOR);
    }


    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        visitMethod((ASTExecutableDeclaration) node, (RuleContext) data);
        return data;
    }

    private void visitMethod(ASTExecutableDeclaration node, RuleContext data) {
        MetricOptions cycloOptions = MetricOptions.ofOptions(getProperty(CYCLO_OPTIONS_DESCRIPTOR));

        if (JavaMetrics.CYCLO.supports(node)) {
            int cyclo = MetricsUtil.computeMetric(JavaMetrics.CYCLO, node, cycloOptions);
            MetricRule.addViolation(node, asCtx(data), "CyclomaticComplexity", Type.METHOD,
                    PrettyPrintingUtil.displaySignature(node), cyclo);
        }
    }

}
