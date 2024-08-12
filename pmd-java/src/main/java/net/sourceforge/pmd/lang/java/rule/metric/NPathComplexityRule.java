/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.metric;


import java.math.BigInteger;

import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.rule.MetricRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Simple n-path complexity rule.
 *
 * @author Maximilian Waidelich
 */
public class NPathComplexityRule extends AbstractJavaRulechainRule implements MetricRule {

    public NPathComplexityRule() {
        super(ASTExecutableDeclaration.class);
    }

    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        visitMethod((ASTExecutableDeclaration) node, (RuleContext) data);
        return data;
    }

    private void visitMethod(ASTExecutableDeclaration node, RuleContext data) {
        if (JavaMetrics.NPATH.supports(node)) {

            BigInteger npath = MetricsUtil.computeMetric(JavaMetrics.NPATH, node);
            MetricRule.addViolation(node, asCtx(data), "NPathComplexity", MetricRule.Type.METHOD,
                    PrettyPrintingUtil.displaySignature(node), npath.toString());
        }
    }
}

