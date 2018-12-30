/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

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
public abstract class AbstractStatisticalJavaRule extends AbstractJavaRule implements StatisticalRule {

    private final StatisticalRuleHelper helper = new StatisticalRuleHelper(this);

    @Override
    public void addDataPoint(DataPoint point) {
        helper.addDataPoint(point);
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        return new Object[0];
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        super.apply(nodes, ctx);
        helper.apply(ctx);
    }
}
