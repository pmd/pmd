/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.Node;
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
    public boolean dependsOn(AstProcessingStage<?> stage) {
        return true;
    }

    @Override
    protected String violationMessage(Node node, Integer result) {
        return AllMetricsTest.formatJavaMessage(node, result, super.violationMessage(node, result));
    }
}
