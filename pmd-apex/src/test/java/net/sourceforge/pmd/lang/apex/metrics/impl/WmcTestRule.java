/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.test.AbstractMetricTestRule;

/**
 * @author Clément Fournier
 */
public class WmcTestRule extends AbstractMetricTestRule.OfInt {

    public WmcTestRule() {
        super(ApexMetrics.WEIGHED_METHOD_COUNT);
    }


    @Override
    protected String violationMessage(Node node, Integer result) {
        return AllMetricsTest.formatApexMessage(node, result, super.violationMessage(node, result));
    }

}
