/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.metrics.JavaMetrics.COGNITIVE_COMPLEXITY;
import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

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
        super(ASTMethodOrConstructorDeclaration.class);
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
    }

    private int getReportLevel() {
        return getProperty(REPORT_LEVEL_DESCRIPTOR);
    }

    @Override
    public final Object visit(ASTMethodDeclaration node, Object data) {
        return visitMethod(node, data);
    }

    @Override
    public final Object visit(ASTConstructorDeclaration node, Object data) {
        return visitMethod(node, data);
    }

    private Object visitMethod(ASTMethodOrConstructorDeclaration node, Object data) {
        if (!COGNITIVE_COMPLEXITY.supports(node)) {
            return data;
        }

        int cognitive = MetricsUtil.computeMetric(COGNITIVE_COMPLEXITY, node);
        final int reportLevel = getReportLevel();
        if (cognitive >= reportLevel) {
            addViolation(data, node, new String[] { node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                                    PrettyPrintingUtil.displaySignature(node),
                                                    String.valueOf(cognitive),
                                                    String.valueOf(reportLevel) });
        }

        return data;
    }
}
