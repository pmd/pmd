/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.test.AbstractMetricTestRule;

/**
 * @since 7.21.0.
 */
public abstract class JavaIntMetricWithOptionsTestRule<O extends Enum<O> & MetricOption> extends AbstractMetricTestRule.OfIntWithOptions<O> {

    protected JavaIntMetricWithOptionsTestRule(Metric<?, Integer> metric, Class<O> metricOptionsEnum) {
        super(metric, metricOptionsEnum);
    }

    @Override
    protected boolean reportOn(Node node) {
        return super.reportOn(node)
            && (node instanceof ASTExecutableDeclaration
            || node instanceof ASTTypeDeclaration);
    }

    @Override
    protected String violationMessage(Node node, Integer result) {
        return AllMetricsTest.formatJavaMessage(node, result);
    }
}
