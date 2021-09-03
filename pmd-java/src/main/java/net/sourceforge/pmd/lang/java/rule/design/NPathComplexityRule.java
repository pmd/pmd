/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.math.BigInteger;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Simple n-path complexity rule.
 *
 * @author Clément Fournier
 * @author Jason Bennett
 */
public class NPathComplexityRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Integer> REPORT_LEVEL_DESCRIPTOR
        = PropertyFactory.intProperty("reportLevel").desc("N-Path Complexity reporting threshold")
                         .require(positive()).defaultValue(200).build();

    public NPathComplexityRule() {
        super(ASTMethodOrConstructorDeclaration.class);
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
    }


    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        return visitMethod((ASTMethodOrConstructorDeclaration) node, (RuleContext) data);
    }

    private Object visitMethod(ASTMethodOrConstructorDeclaration node, RuleContext data) {
        int reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        if (!JavaMetrics.NPATH.supports(node)) {
            return data;
        }

        BigInteger npath = MetricsUtil.computeMetric(JavaMetrics.NPATH, node);
        if (npath.compareTo(BigInteger.valueOf(reportLevel)) >= 0) {
            addViolation(data, node, new String[] {node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                                   PrettyPrintingUtil.displaySignature(node),
                                                   String.valueOf(npath),
                                                   String.valueOf(reportLevel)});
        }

        return data;
    }
}

