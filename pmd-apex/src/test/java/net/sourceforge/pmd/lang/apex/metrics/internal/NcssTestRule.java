/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.internal;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.test.AbstractMetricTestRule;

public class NcssTestRule extends AbstractMetricTestRule.OfInt {

    public NcssTestRule() {
        super(ApexMetrics.NCSS);
    }

    @Override
    protected boolean reportOn(Node node) {
        return super.reportOn(node)
                && (node instanceof ASTMethod || node instanceof ASTUserClassOrInterface);
    }

    @Override
    protected String violationMessage(Node node, Integer result) {
        return AllMetricsTest.formatApexMessage(node, result, super.violationMessage(node, result));
    }
}
