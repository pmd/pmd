/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.metrics.internal;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.plsql.ast.ExecutableCode;
import net.sourceforge.pmd.lang.plsql.ast.OracleObject;
import net.sourceforge.pmd.lang.plsql.metrics.PlsqlMetrics;
import net.sourceforge.pmd.lang.test.AbstractMetricTestRule;

public class NcssTestRule extends AbstractMetricTestRule.OfInt {
    public NcssTestRule() {
        super(PlsqlMetrics.NCSS);
    }

    @Override
    protected boolean reportOn(Node node) {
        return super.reportOn(node)
                && (node instanceof ExecutableCode || node instanceof OracleObject || node instanceof RootNode);
    }

    @Override
    protected String violationMessage(Node node, Integer result) {
        return AllMetricsTest.formatPlsqlMessage(node, result, super.violationMessage(node, result));
    }
}
