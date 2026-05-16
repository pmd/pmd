/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.rule.internal.CommonPropertyDescriptors;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Switch Density - This is the number of statements over the number of
 * cases within a switch. The higher the value, the more work each case
 * is doing.
 *
 * <p>It's my theory, that when the Switch Density is high, you should start
 * looking at Subclasses or State Pattern to alleviate the problem.</p>
 *
 * @author David Dixon-Peugh
 * @author Clément Fournier
 */
public class SwitchDensityRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Integer> REPORT_LEVEL =
            CommonPropertyDescriptors.reportLevelProperty()
                    .desc("Threshold at or above which a switch statement or expression is reported")
                    .require(positive())
                    .defaultValue(10)
                    .build();

    public SwitchDensityRule() {
        super(ASTSwitchStatement.class, ASTSwitchExpression.class);
        definePropertyDescriptor(REPORT_LEVEL);
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        RuleContext ctx = (RuleContext) data;

        visitSwitchLike(node, ctx);

        return null;
    }

    @Override
    public Object visit(ASTSwitchExpression node, Object data) {
        RuleContext ctx = (RuleContext) data;

        visitSwitchLike(node, ctx);

        return null;
    }

    /**
     * @deprecated since 7.25.0. This method should have never been public.
     */
    @Deprecated
    public Void visitSwitchLike(ASTSwitchLike node, Object data) {
        RuleContext ctx = (RuleContext) data;

        visitSwitchLike(node, ctx);

        return null;
    }

    private void visitSwitchLike(ASTSwitchLike node, RuleContext ctx) {
        // note: this does not cross find boundaries.
        int stmtCount = node.descendants(ASTStatement.class).count();
        int labelCount = node.getBranches()
                .map(ASTSwitchBranch::getLabel)
                .sumBy(label -> label.isDefault() || label.isPatternLabel() ? 1 : label.getExprList().count());

        // note: if labelCount is zero, double division will produce +Infinity or NaN, not ArithmeticException
        double density = stmtCount / (double) labelCount;
        if (density >= getProperty(REPORT_LEVEL)) {
            ctx.addViolation(node);
        }
    }
}
