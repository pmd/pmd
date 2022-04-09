/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.test.AbstractMetricTestRule;

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
            && (node instanceof ASTMethodOrConstructorDeclaration
            || node instanceof ASTAnyTypeDeclaration);
    }

    @Override
    protected String violationMessage(Node node, Integer result) {
        return AllMetricsTest.formatJavaMessage(node, result, super.violationMessage(node, result));
    }
}
