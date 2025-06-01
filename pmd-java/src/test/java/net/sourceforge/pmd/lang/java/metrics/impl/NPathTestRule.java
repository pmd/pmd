/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.test.AbstractMetricTestRule;

/**
 * @author Clément Fournier
 */
public class NPathTestRule extends AbstractMetricTestRule<Long> {

    public NPathTestRule() {
        super(JavaMetrics.NPATH_COMP);
    }

    @Override
    protected String violationMessage(Node node, Long result) {
        return AllMetricsTest.formatJavaMessage(node, result);
    }

    @Override
    protected Long parseReportLevel(String value) {
        return Long.parseLong(value);
    }

    @Override
    protected Long defaultReportLevel() {
        return 0L;
    }
}
