/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.stat.StatisticalRule;
import net.sourceforge.pmd.lang.rule.stat.StatisticalRuleHelper;
import net.sourceforge.pmd.stat.DataPoint;


/**
 * @deprecated see {@link StatisticalRule}
 */
@Deprecated
public abstract class AbstractStatisticalVmRule extends AbstractVmRule implements StatisticalRule {

    private final StatisticalRuleHelper helper = new StatisticalRuleHelper(this);

    @Override
    public void addDataPoint(final DataPoint point) {
        helper.addDataPoint(point);
    }

    @Override
    public Object[] getViolationParameters(final DataPoint point) {
        return new Object[0];
    }

    @Override
    public void apply(final List<? extends Node> nodes, final RuleContext ctx) {
        super.apply(nodes, ctx);
        helper.apply(ctx);
    }
}
