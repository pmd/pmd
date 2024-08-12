/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.metric;

import static net.sourceforge.pmd.lang.java.metrics.JavaMetrics.COGNITIVE_COMPLEXITY;

import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.rule.MetricRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Cognitive complexity rule.
 *
 * @author Maximilian Waidelich
 */
public class CognitiveComplexityRule extends AbstractJavaRulechainRule implements MetricRule {

    public CognitiveComplexityRule() {
        super(ASTExecutableDeclaration.class);
    }

    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        visitMethod((ASTExecutableDeclaration) node, (RuleContext) data);
        return data;
    }

    private void visitMethod(ASTExecutableDeclaration node, RuleContext data) {
        if (COGNITIVE_COMPLEXITY.supports(node)) {

            int cognitive = MetricsUtil.computeMetric(COGNITIVE_COMPLEXITY, node);
            MetricRule.addViolation(node, asCtx(data), "CognitiveComplexity", MetricRule.Type.METHOD,
                    PrettyPrintingUtil.displaySignature(node), cognitive);
        }
    }
}
