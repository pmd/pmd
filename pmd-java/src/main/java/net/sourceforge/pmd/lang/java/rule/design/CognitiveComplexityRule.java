/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.metrics.JavaMetrics.COGNITIVE_COMPLEXITY;
import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Cognitive complexity rule.
 *
 * @author Denis Borovikov
 * @see JavaMetrics#COGNITIVE_COMPLEXITY
 */
public class CognitiveComplexityRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Integer> REPORT_LEVEL_DESCRIPTOR
        = PropertyFactory.intProperty("reportLevel").desc("Cognitive Complexity reporting threshold")
                         .require(positive()).defaultValue(15).build();

    public CognitiveComplexityRule() {
        super(ASTExecutableDeclaration.class);
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
    }

    private int getReportLevel() {
        return getProperty(REPORT_LEVEL_DESCRIPTOR);
    }

    @Override
    public final RuleContext visit(ASTMethodDeclaration node, RuleContext data) {
        return visitMethod(node, data);
    }

    @Override
    public final RuleContext visit(ASTConstructorDeclaration node, RuleContext data) {
        return visitMethod(node, data);
    }

    private RuleContext visitMethod(ASTExecutableDeclaration node, RuleContext data) {
        if (!COGNITIVE_COMPLEXITY.supports(node)) {
            return data;
        }

        int cognitive = MetricsUtil.computeMetric(COGNITIVE_COMPLEXITY, node);
        final int reportLevel = getReportLevel();
        if (cognitive >= reportLevel) {
            data.addViolation(node, node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                     PrettyPrintingUtil.displaySignature(node),
                                     String.valueOf(cognitive),
                                     String.valueOf(reportLevel));
        }

        return data;
    }
}
