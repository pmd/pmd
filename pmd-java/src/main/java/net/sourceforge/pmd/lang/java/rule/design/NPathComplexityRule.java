/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.logging.Logger;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;
import net.sourceforge.pmd.properties.DoubleProperty;
import net.sourceforge.pmd.properties.IntegerProperty;


/**
 * Simple n-path complexity rule.
 *
 * @author Clément Fournier
 * @author Jason Bennett
 */
public class NPathComplexityRule extends AbstractJavaMetricsRule {

    private static final Logger LOG = Logger.getLogger(NPathComplexityRule.class.getName());

    private static final DoubleProperty MINIMUM_DESCRIPTOR
        = DoubleProperty.named("minimum").desc("Deprecated! Minimum reporting threshold")
                        .range(0d, 2000d).defaultValue(200d).uiOrder(2.0f).build();


    private static final IntegerProperty REPORT_LEVEL_DESCRIPTOR
        = IntegerProperty.named("reportLevel").desc("N-Path Complexity reporting threshold")
                         .range(1, 2000).defaultValue(200).uiOrder(1.0f).build();


    private int reportLevel = 200;


    public NPathComplexityRule() {
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(MINIMUM_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        reportLevel = getReportLevel();

        super.visit(node, data);
        return data;
    }


    private int getReportLevel() {
        double oldProp = getProperty(MINIMUM_DESCRIPTOR);
        if (oldProp != 200.0) {
            LOG.warning("Rule NPathComplexity uses deprecated property 'minimum'. Future versions of PMD will remove support for this property. Please use 'reportLevel' instead!");
            return (int) oldProp;
        }

        return getProperty(REPORT_LEVEL_DESCRIPTOR);
    }


    @Override
    public final Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        int npath = (int) JavaMetrics.get(JavaOperationMetricKey.NPATH, node);
        if (npath >= reportLevel) {
            addViolation(data, node, new String[]{node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                                  node.getQualifiedName().getOperation(), "" + npath, });
        }

        return data;
    }
}

