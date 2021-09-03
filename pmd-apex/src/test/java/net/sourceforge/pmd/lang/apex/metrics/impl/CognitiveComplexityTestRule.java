/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.test.AbstractMetricTestRule;

/**
 * @author Gwilym Kuiper
 */
public class CognitiveComplexityTestRule extends AbstractMetricTestRule.OfInt {

    public CognitiveComplexityTestRule() {
        super(ApexMetrics.COGNITIVE_COMPLEXITY);
    }

    @Override
    protected boolean reportOn(Node node) {
        return node instanceof ASTMethod;
    }

    @Override
    protected String violationMessage(Node node, Integer result) {
        return AllMetricsTest.formatApexMessage(node, result, super.violationMessage(node, result));
    }

}
