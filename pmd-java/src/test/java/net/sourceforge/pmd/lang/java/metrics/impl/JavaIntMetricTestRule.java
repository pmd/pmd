/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.test.AbstractMetricTestRule;

/**
 *
 */
public abstract class JavaIntMetricTestRule extends AbstractMetricTestRule.OfInt {

    protected JavaIntMetricTestRule(Metric<?, Integer> metric) {
        super(metric);
    }

    @Override
    protected boolean reportOn(Node node) {
        return super.reportOn(node)
            && (node instanceof ASTExecutableDeclaration
            || node instanceof ASTTypeDeclaration);
    }

    @Override
    protected String violationMessage(Node node, Integer result) {
        return AllMetricsTest.formatJavaMessage(node, result, super.violationMessage(node, result));
    }
}
