/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

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
public class SwitchDensityRule extends AbstractCounterCheckRule<ASTSwitchStatement> {

    public SwitchDensityRule() {
        super(ASTSwitchStatement.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 10;
    }

    @Override
    protected boolean isViolation(ASTSwitchStatement node, int reportLevel) {
        return new SwitchDensityVisitor().compute(node) >= reportLevel;
    }

    private static class SwitchDensityVisitor extends JavaParserVisitorAdapter {

        private int labels = 0;
        private int stmts = 0;
        private ASTSwitchStatement root;


        double compute(ASTSwitchStatement root) {
            this.root = root;
            root.jjtAccept(this, null);
            return labels == 0 ? 0 : ((double) stmts) / labels;
        }


        @Override
        public Object visit(ASTStatement statement, Object data) {
            stmts++;
            return super.visit(statement, data);
        }

        @Override
        public Object visit(ASTExpression node, Object data) {
            // don't recurse on anonymous class, etc
            return data;
        }

        @Override
        public Object visit(ASTSwitchLabel switchLabel, Object data) {
            if (switchLabel.jjtGetParent() == root) {
                labels++;
            }
            return super.visit(switchLabel, data);
        }
    }
}
