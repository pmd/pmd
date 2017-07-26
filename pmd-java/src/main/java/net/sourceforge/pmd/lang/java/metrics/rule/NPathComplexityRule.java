/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.rule;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * @author Clément Fournier
 */
public class NPathComplexityRule extends AbstractJavaMetricsRule {

    private static final IntegerProperty REPORT_LEVEL_DESCRIPTOR = new IntegerProperty(
        "reportLevel", "N-Path Complexity reporting threshold", 1, 30, 200, 1.0f);


    private static int reportLevel = 200;


    public NPathComplexityRule() {
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);

        super.visit(node, data);
        return data;
    }


    @Override
    public final Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        int npath = (int) JavaMetrics.get(JavaOperationMetricKey.NPATH, node);
        if (npath >= reportLevel) {
            addViolation(data, node, new String[] {node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                                   node.getQualifiedName().getOperation(), "" + npath, });
        }

        return data;
    }

}
