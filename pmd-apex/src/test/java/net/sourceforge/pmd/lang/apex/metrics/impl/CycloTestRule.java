/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.test.AbstractMetricTestRule;

/**
 * Tests standard cyclo.
 *
 * @author Clément Fournier
 */
public class CycloTestRule extends AbstractMetricTestRule.OfInt {

    public CycloTestRule() {
        super(ApexMetrics.CYCLO);
    }


    @Override
    protected boolean reportOn(Node node) {
        return node instanceof ASTUserClassOrInterface || node instanceof ASTMethod;
    }



    @Override
    protected String violationMessage(Node node, Integer result) {
        return AllMetricsTest.formatApexMessage(node, result, super.violationMessage(node, result));
    }

}
