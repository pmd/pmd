/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractStatisticalJavaRule;
import net.sourceforge.pmd.stat.DataPoint;

/**
 * Switch Density - This is the number of statements over the number of
 * cases within a switch. The higher the value, the more work each case
 * is doing.
 *
 * <p>Its my theory, that when the Switch Density is high, you should start
 * looking at Subclasses or State Pattern to alleviate the problem.</p>
 *
 * @author David Dixon-Peugh
 */
public class SwitchDensityRule extends AbstractStatisticalJavaRule {

    private static class SwitchDensity {
        private int labels = 0;
        private int stmts = 0;

        public void addSwitchLabel() {
            labels++;
        }

        public void addStatement() {
            stmts++;
        }

        public void addStatements(int stmtCount) {
            stmts += stmtCount;
        }

        public int getStatementCount() {
            return stmts;
        }

        public double getDensity() {
            if (labels == 0) {
                return 0;
            }
            return (double) stmts / (double) labels;
        }
    }

    public SwitchDensityRule() {
        super();
        setProperty(MINIMUM_DESCRIPTOR, 10d);
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        SwitchDensity oldData = null;

        if (data instanceof SwitchDensity) {
            oldData = (SwitchDensity) data;
        }

        SwitchDensity density = new SwitchDensity();

        node.childrenAccept(this, density);

        DataPoint point = new DataPoint();
        point.setNode(node);
        point.setScore(density.getDensity());
        point.setMessage(getMessage());

        addDataPoint(point);

        if (data instanceof SwitchDensity) {
            ((SwitchDensity) data).addStatements(density.getStatementCount());
        }
        return oldData;
    }

    @Override
    public Object visit(ASTStatement statement, Object data) {
        if (data instanceof SwitchDensity) {
            ((SwitchDensity) data).addStatement();
        }

        statement.childrenAccept(this, data);

        return data;
    }

    @Override
    public Object visit(ASTSwitchLabel switchLabel, Object data) {
        if (data instanceof SwitchDensity) {
            ((SwitchDensity) data).addSwitchLabel();
        }

        switchLabel.childrenAccept(this, data);
        return data;
    }
}
