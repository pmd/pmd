package net.sourceforge.pmd.lang.vm.rule.basic;

import net.sourceforge.pmd.lang.vm.ast.ASTprocess;
import net.sourceforge.pmd.lang.vm.rule.AbstractStatisticalVmRule;
import net.sourceforge.pmd.stat.DataPoint;

public class ExcessiveTemplateLengthRule extends AbstractStatisticalVmRule {

    @Override
    public Object visit(final ASTprocess node, final Object data) {
        final DataPoint point = new DataPoint();
        point.setNode(node);
        point.setScore(1.0 * (node.getEndLine() - node.getBeginLine()));
        point.setMessage(getMessage());
        addDataPoint(point);
        return node.childrenAccept(this, data);
    }
}
